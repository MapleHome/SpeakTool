package com.speektool.ui.fragment;

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
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.activity.MainActivity;
import com.speektool.adapters.RecordsAdapter;
import com.speektool.api.AsyncDataLoader;
import com.speektool.api.CourseItem;
import com.speektool.base.AbsListScrollListener;
import com.speektool.base.BaseFragment;
import com.speektool.bean.CourseSearchBean;
import com.speektool.bean.SearchCategoryBean;
import com.speektool.factory.AsyncDataLoaderFactory;
import com.speektool.tasks.TaskLoadRecords;
import com.speektool.tasks.TaskLoadRecords.RecordsUi;
import com.speektool.tasks.ThreadPoolWrapper;
import com.speektool.ui.dialogs.LoadingDialog;
import com.speektool.ui.dialogs.ShareDialog;
import com.speektool.ui.layouts.ItemViewLocalRecord;
import com.speektool.utils.T;

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
    /**
     * 搜索记录
     */
    private List<CourseItem> mCurrentData = Lists.newArrayList();
    private RecordsAdapter mAdapterAllRecords;

    private static final int GRID_PAGE_SIZE = 20;// 网格页面大小
    private int mPageNumber = 1;

    private boolean mIsHaveMoreData = true;
    private ThreadPoolWrapper singleExecutor = ThreadPoolWrapper.newThreadPool(1);
    private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader = AsyncDataLoaderFactory
            .newCourseThumbnailAsyncLoader();

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();

        // 记录列表
        mAdapterAllRecords = new RecordsAdapter(mContext, null, mAppIconAsyncLoader, mActivity);
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
        mPageNumber = 1;
        mIsHaveMoreData = true;
        // 加载本地记录
        searchRecords(mActivity.mCurSearchType, mActivity.mCurSearchKeyWords, true);
    }

    @Override
    public void initListener() {
        // 课程记录
        gridViewAllRecords.setOnScrollListener(mAllListOnScrollListener);
        // 课程记录-条目点击
        gridViewAllRecords.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseItem course = (CourseItem) parent.getAdapter().getItem(position);
                // 条目详情框
                ShareDialog dia = new ShareDialog(mContext, course, mAppIconAsyncLoader);
                dia.show();
            }
        });
        /** 刷新加载监听 */
        gridViewAllRecords.setOnRefreshListener(new OnRefreshListener2<GridView>() {
            // 下拉刷新
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                refreshIndexPage();
            }

            // 上拉加载
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                if (!mIsHaveMoreData) {
                    T.showShort(mContext, "没有更多数据了");
                    gridViewAllRecords.onRefreshComplete();
                    return;
                }
                searchRecords(mActivity.mCurSearchType, mActivity.mCurSearchKeyWords, true);
            }
        });
    }

    /**
     * 搜索课程记录
     */
    public void searchRecords(SearchCategoryBean mSearchType, String mSearchKeywords, boolean isNeedLoadLocalRecord) {
        mLoadingDialog.show("正在加载...");

        mActivity.setSearchView(mSearchType, mSearchKeywords);

        CourseSearchBean info = new CourseSearchBean();
        info.setUid(SpeekToolApp.getUid());
        info.setPageSize(GRID_PAGE_SIZE);// 页大小
        info.setPageNumber(mPageNumber);// 第几页
        info.setCategory(mSearchType);// 搜索类型
        info.setKeywords(mSearchKeywords);// 关键字

        mCurrentData.clear();
        singleExecutor.execute(new TaskLoadRecords(indexRecordsUi, info, isNeedLoadLocalRecord, mCurrentData));
    }

    private RecordsUi indexRecordsUi = new RecordsUi() {

        @Override
        public void onRecordsLoaded(List<CourseItem> datas) {
            mPageNumber++;
            refreshIndexAdp(datas);
            gridViewAllRecords.onRefreshComplete();
        }

        @Override
        public void onNoMoreData(List<CourseItem> datas) {
            mIsHaveMoreData = false;
            refreshIndexAdp(datas);
            gridViewAllRecords.onRefreshComplete();
        }

        @Override
        public void onFail(List<CourseItem> datas) {
            refreshIndexAdp(datas);
            gridViewAllRecords.onRefreshComplete();
            T.showShort(mContext, "服务器链接失败！请检查网络");
        }

        @Override
        public void onNotLogin(List<CourseItem> datas) {
            refreshIndexAdp(datas);
            gridViewAllRecords.onRefreshComplete();

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
        SpeekToolApp.getUiHandler().post(new Runnable() {

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
                final ItemViewLocalRecord item = (ItemViewLocalRecord) gridViewAllRecords.findViewWithTag(imageUrl);
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
