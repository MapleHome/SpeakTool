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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
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
import com.speaktool.utils.T;
import com.speaktool.view.dialogs.CourseItemDesDialog;
import com.speaktool.view.layouts.ItemViewLocalRecord;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 所有记录界面
 *
 * @author shaoshuai
 */
public class HomePage extends BaseFragment {
    @BindView(R.id.srl_refreshLayout) SmartRefreshLayout srl_refreshLayout;
    @BindView(R.id.gv_records) GridView gv_records;// 所有记录

    private MainActivity mActivity;
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

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        // 记录列表
        mAdapterAllRecords = new RecordsAdapter(mContext, null, mAppIconAsyncLoader);
        gv_records.setAdapter(mAdapterAllRecords);

        srl_refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        srl_refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
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
        // 刷新加载监听
        srl_refreshLayout
                .setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshlayout) {
                        refreshIndexPage();
                    }
                })
                .setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(RefreshLayout refreshlayout) {
                        searchRecords(mActivity.mCurSearchType, mActivity.mCurSearchKeyWords);
                    }
                });
        gv_records.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseItem course = (CourseItem) parent.getAdapter().getItem(position);
                new CourseItemDesDialog(mContext, course, mAppIconAsyncLoader).show();
            }
        });
    }

    /**
     * 搜索课程记录
     */
    public void searchRecords(SearchCategoryBean mSearchType, String mSearchKeywords) {

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
        }

        @Override
        public void onFail(List<CourseItem> datas) {
            refreshIndexAdp(datas);
            T.showShort(mContext, "服务器链接失败！请检查网络");
        }

    };

    private void refreshIndexAdp(List<CourseItem> datas) {
        if (datas == null || datas.isEmpty()) {
            // tvSearchEmpty.setText("未找到录像");
            // tvSearchEmpty.setVisibility(View.VISIBLE);
            // tvSearchEmpty.setProgressbarVisibility(View.GONE);
        }
        mAdapterAllRecords.refresh(datas);
        srl_refreshLayout.finishRefresh();
        srl_refreshLayout.finishLoadMore();
        //
        SpeakToolApp.getUiHandler().post(new Runnable() {

            @Override
            public void run() {
                int first = gv_records.getFirstVisiblePosition();
                int last = gv_records.getLastVisiblePosition();

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
            final int itemcount = gv_records.getAdapter().getCount() - 1;
            final int min = Math.min(itemcount, mlastvisibleItem);

            for (int i = mfirstVisibleItem; i <= min; i++) {
                CourseItem bean = (CourseItem) gv_records.getAdapter().getItem(i);
                final String imageUrl = bean.getThumbnailImgPath();
                if (TextUtils.isEmpty(imageUrl))
                    continue;
                final ItemViewLocalRecord item = gv_records.findViewWithTag(imageUrl);
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
        return gv_records.findViewWithTag(key);
    }

}
