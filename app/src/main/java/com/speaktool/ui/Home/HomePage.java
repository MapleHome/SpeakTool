package com.speaktool.ui.Home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.common.collect.Lists;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.speaktool.R;
import com.speaktool.SpeakToolApp;
import com.speaktool.api.AsyncDataLoader;
import com.speaktool.api.CourseItem;
import com.speaktool.bean.CourseSearchBean;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.service.AsyncDataLoaderFactory;
import com.speaktool.tasks.TaskLoadRecords;
import com.speaktool.tasks.TaskLoadRecords.RecordsUi;
import com.speaktool.tasks.ThreadPoolWrapper;
import com.speaktool.ui.adapters.RecordsAdapter;
import com.speaktool.ui.base.AbsListScrollListener;
import com.speaktool.ui.base.BaseFragment;
import com.speaktool.view.dialogs.CourseItemDesDialog;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.view.layouts.ItemViewLocalRecord;
import com.speaktool.utils.T;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 所有记录界面
 *
 * @author shaoshuai
 */
public class HomePage extends BaseFragment {
    @BindView(R.id.prgw_ServerRecords) PullToRefreshGridView gridViewAllRecords;// 所有记录

    private MainActivity mActivity;
    private LoadingDialog mLoadingDialog;
    private List<CourseItem> mCurrentData = Lists.newArrayList();// 搜索记录
    private RecordsAdapter mAdapterAllRecords;

    private ThreadPoolWrapper singleExecutor = ThreadPoolWrapper.newThreadPool(1);
    private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader = AsyncDataLoaderFactory
            .newCourseThumbnailAsyncLoader();

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();

        // 记录列表
        mAdapterAllRecords = new RecordsAdapter(mContext, null, mAppIconAsyncLoader);
        gridViewAllRecords.setMode(Mode.BOTH);
        gridViewAllRecords.setAdapter(mAdapterAllRecords);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mLoadingDialog = new LoadingDialog(mActivity);
        // tvSearchEmpty.setVisibility(View.INVISIBLE);
        // 默认设置
        refreshIndexPage();
    }

    public void refreshIndexPage() {
        mCurrentData.clear();
        // 加载本地记录
        searchRecords(mActivity.mCurSearchType, mActivity.mCurSearchKeyWords);
    }

    @Override
    public void initListener() {
        gridViewAllRecords.setOnScrollListener(mAllListOnScrollListener);
        gridViewAllRecords.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseItem course = (CourseItem) parent.getAdapter().getItem(position);
                new CourseItemDesDialog(mContext, course, mAppIconAsyncLoader).show();
            }
        });
        // 刷新加载监听
        gridViewAllRecords.setOnRefreshListener(new OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                refreshIndexPage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                if (!mIsHaveMoreData) {
                T.showShort(mContext, "没有更多数据了");
//                    gridViewAllRecords.onRefreshComplete();
//                    return;
//                }
//                searchRecords(mActivity.mCurSearchType, mActivity.mCurSearchKeyWords, true);
            }
        });
    }

    /**
     * 搜索课程记录
     */
    public void searchRecords(SearchCategoryBean mSearchType, String mSearchKeywords) {
        mLoadingDialog.show("正在加载...");

        mActivity.setSearchView(mSearchType, mSearchKeywords);

        CourseSearchBean info = new CourseSearchBean();
        info.setUid("UID");
        info.setPageSize(20);// 页大小
        info.setPageNumber(1);// 第几页
        info.setCategory(mSearchType);// 搜索类型
        info.setKeywords(mSearchKeywords);// 关键字

        mCurrentData.clear();
        singleExecutor.execute(new TaskLoadRecords(indexRecordsUi, info, mCurrentData));
    }

    private RecordsUi indexRecordsUi = new RecordsUi() {

        @Override
        public void onRecordsLoaded(List<CourseItem> datas) {
            refreshIndexAdp(datas);
            gridViewAllRecords.onRefreshComplete();
        }

        @Override
        public void onFail(List<CourseItem> datas) {
            refreshIndexAdp(datas);
            gridViewAllRecords.onRefreshComplete();
            T.showShort(mContext, "服务器链接失败！请检查网络");
        }

    };

    private void refreshIndexAdp(List<CourseItem> datas) {
        mLoadingDialog.dismiss();
        if (datas == null || datas.isEmpty()) {
            // tvSearchEmpty.setText("未找到录像");
            // tvSearchEmpty.setVisibility(View.VISIBLE);
            // tvSearchEmpty.setProgressbarVisibility(View.GONE);
        }
        mAdapterAllRecords.refresh(datas);
        //
        SpeakToolApp.getUiHandler().post(new Runnable() {

            @Override
            public void run() {
                int first = gridViewAllRecords.getRefreshableView().getFirstVisiblePosition();
                int last = gridViewAllRecords.getRefreshableView().getLastVisiblePosition();

                mAllListOnScrollListener.setVisibleItems(first, last);
                mAllListOnScrollListener.whenIdle();
            }
        });

    }

    // =====================监听====================================================
    /**
     * Scroll 监听
     */
    private AbsListScrollListener mAllListOnScrollListener = new AbsListScrollListener() {

        @Override
        public void whenIdle() {
            final int itemcount = gridViewAllRecords.getRefreshableView().getAdapter().getCount() - 1;
            final int min = Math.min(itemcount, mlastvisibleItem);

            for (int i = mfirstVisibleItem; i <= min; i++) {
                CourseItem bean = (CourseItem) gridViewAllRecords.getRefreshableView().getAdapter().getItem(i);
                final String imageUrl = bean.getThumbnailImgPath();
                if (TextUtils.isEmpty(imageUrl))
                    continue;
                final ItemViewLocalRecord item = gridViewAllRecords.findViewWithTag(imageUrl);
                if (item == null)
                    continue;
                if (mAppIconAsyncLoader == null) {
                    return;
                }
                final Bitmap cache = mAppIconAsyncLoader.load(imageUrl);
                //
                if (cache != null) {
                    item.setThumbnail(cache);
                } else {
                    item.setThumbnail(getDefBmp());
                }
            }
        }

        @Override
        public void whenFling() {
            mAppIconAsyncLoader.cancelAll();
        }
    };

    private Bitmap getDefBmp() {
        return mAdapterAllRecords.getDefBmp();
    }

    public ItemViewLocalRecord findViewWithTag(String key) {
        return gridViewAllRecords.findViewWithTag(key);
    }

}
