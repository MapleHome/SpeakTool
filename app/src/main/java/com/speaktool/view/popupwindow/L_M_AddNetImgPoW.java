package com.speaktool.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.speaktool.R;
import com.speaktool.SpeakToolApp;
import com.speaktool.api.Draw;
import com.speaktool.bean.NetPictureBean;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.tasks.MyThreadFactory;
import com.speaktool.tasks.TaskSearchNetPictures;
import com.speaktool.tasks.TaskSearchNetPictures.SearchNetPicturesCallback;
import com.speaktool.ui.adapters.AdapterNetPictures;
import com.speaktool.utils.NetUtil;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.T;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.view.layouts.SearchView;
import com.speaktool.view.popupwindow.CategoryPoW.SearchCategoryChangedListener;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 顶部功能栏——更多功能——添加图片——网络图片
 *
 * @author shaoshuai
 */
public class L_M_AddNetImgPoW extends BasePopupWindow implements OnClickListener,
        OnDismissListener, OnItemClickListener, SearchCategoryChangedListener {
    private SearchView mSearchView;
    private SmartRefreshLayout layContent;
    private GridView mNetPicsGrid;
    private ImageView netImagePreview;

    private Draw mDraw;
    private ExecutorService singleExecutor = Executors
            .newSingleThreadExecutor(new MyThreadFactory("getNetImageThread"));
    private AdapterNetPictures mAdapterNetPictures;
    private LoadingDialog mLoadingDialog;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_add_net_image, null);
    }

    public L_M_AddNetImgPoW(Context context, View view, Draw draw) {
        this(context, view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
    }

    public L_M_AddNetImgPoW(Context context, View view, int w, int h, Draw draw) {
        super(context, view, w, h);
        mDraw = draw;
        EventBus.getDefault().register(this);
        mLoadingDialog = new LoadingDialog(mContext);

        mSearchView = (SearchView) mRootView.findViewById(R.id.searchView);
        layContent = mRootView.findViewById(R.id.layContent);
        netImagePreview = (ImageView) mRootView.findViewById(R.id.netImagePreview);
        mNetPicsGrid = (GridView) mRootView.findViewById(R.id.listNetImage);

        // mNetPicsGrid.getRefreshableView().setColumnWidth(mRootView.getWidth()/6);
//        mNetPicsGrid.setMode(Mode.BOTH);
//        mNetPicsGrid.setOnRefreshListener(this);
        layContent.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!isHaveMore) {
                    layContent.finishLoadMore();
                    T.showShort(mContext, "没有更多数据了");
                    return;
                }
                dosearch(mCurrentSearchKey, mCurrentSearchIndex);
            }
        });

        SearchCategoryBean type = new SearchCategoryBean("网络搜索", SearchCategoryBean.CID_BAIDU_SEARCH);
        mSearchView.setCategory(type);
        //
        mNetPicsGrid.setEmptyView(makeEmptyView("还没有图片，请重新搜索。"));

        mAdapterNetPictures = new AdapterNetPictures(mContext, null);
        mNetPicsGrid.setAdapter(mAdapterNetPictures);

        mNetPicsGrid.setOnItemClickListener(this);
        mSearchView.setDropdownClickListener(this);
        mSearchView.setSearchClickListener(this);
        netImagePreview.setOnClickListener(this);
        this.setOnDismissListener(this);
        switchUi(type.getCategoryId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSearch:
                onSearchClicked();
                break;
            case R.id.layDropdownHandle:
                switchVisibilityPopDropdown(v);
                break;
            case R.id.netImagePreview:
                final BitmapDrawable bd = (BitmapDrawable) netImagePreview.getDrawable();
                if (bd != null) {
                    mLoadingDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String resname = RecordFileUtils.copyBitmapToRecordDir(bd.getBitmap(),
                                    mDraw.getRecordDir());

                            SpeakToolApp.getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mDraw.getCurrentBoard().addImg(resname);
                                }
                            });
                            mLoadingDialog.dismiss();
                        }
                    }).start();
                }
                break;
        }
    }


    private void switchUi(int id) {
        switch (id) {
            case SearchCategoryBean.CID_BAIDU_SEARCH:
                netImagePreview.setVisibility(View.GONE);
                mNetPicsGrid.setVisibility(View.VISIBLE);
                mSearchView.setSearchKey("");
                break;
            case SearchCategoryBean.CID_PIC_URL:
                netImagePreview.setVisibility(View.VISIBLE);
                mNetPicsGrid.setVisibility(View.GONE);
                mSearchView.setSearchKey("http://img0.imgtn.bdimg.com/it/u=3631852859,2060994785&fm=21&gp=0.jpg");
                break;
        }
    }

    private View makeEmptyView(String info) {
        TextView searchEmpty = new TextView(mContext);
        searchEmpty.setGravity(Gravity.CENTER);
        searchEmpty.setText(info);
        return searchEmpty;
    }

    private int mCurrentSearchIndex = 0;
    private String mCurrentSearchKey = "";
    private static final int PAGE_SIZE = 48;

    private void onSearchClicked() {
        String inputKeyword = mSearchView.getSearchKeywords();
        if (TextUtils.isEmpty(inputKeyword)) {
            T.showShort(mContext, "搜索关键字不能为空！");
            return;
        }
        int type = mSearchView.getCategory().getCategoryId();
        switch (type) {
            case SearchCategoryBean.CID_BAIDU_SEARCH:
                baiduSearch(inputKeyword);
                break;
            case SearchCategoryBean.CID_PIC_URL:
                picUrlSearch(inputKeyword);
                break;
        }
    }

    private void picUrlSearch(String inputUrl) {
//        mLoadingDialog.show();
//        singleExecutor.execute(new TaskGetNetImage(this, inputUrl));
//        netImagePreview.setImageBitmap(result);
    }

    private void baiduSearch(String inputKeyword) {
        String encodedKey = "";
        try {
            encodedKey = URLEncoder.encode(inputKeyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mCurrentSearchIndex = 0;
        oldData.clear();
        isHaveMore = true;
        mCurrentSearchKey = encodedKey;
        dosearch(encodedKey, mCurrentSearchIndex);

    }

    private void refresh() {
        if (TextUtils.isEmpty(mCurrentSearchKey)) {
            layContent.finishRefresh();
            return;
        }
        mCurrentSearchIndex = 0;
        oldData.clear();
        isHaveMore = true;
        dosearch(mCurrentSearchKey, mCurrentSearchIndex);
    }

    private SearchNetPicturesCallback mSearchNetPicturesCallback = new SearchNetPicturesCallback() {

        @Override
        public void onSuccess(List<NetPictureBean> result) {
            layContent.finishLoadMore();
            layContent.finishRefresh();
            mLoadingDialog.dismiss();
            //
            if (result.isEmpty()) {
                T.showShort(mContext, "没有更多数据了");
                isHaveMore = false;
                return;
            }
            if (result.size() < PAGE_SIZE) {
                isHaveMore = false;
            }
            mCurrentSearchIndex += PAGE_SIZE;
            oldData.addAll(result);
            mAdapterNetPictures.refresh(oldData);
            //
//            SpeakToolApp.getUiHandler().post(new Runnable() {
//                @Override
//                public void run() {
//                    int first = mNetPicsGrid.getFirstVisiblePosition();
//                    int last = mNetPicsGrid.getLastVisiblePosition();
//                    netpicGridAbsListScrollListener.setVisibleItems(first, last);
//                    netpicGridAbsListScrollListener.whenIdle();
//                }
//            });
        }

        @Override
        public void onFail() {
            layContent.finishRefresh();
            mLoadingDialog.dismiss();
            T.showShort(mContext, "服务器链接失败！请检查网络");
        }

        @Override
        public void onConnectFail() {
            layContent.finishRefresh();
            mLoadingDialog.dismiss();
            T.showShort(mContext, "服务器链接失败！请检查网络");
        }
    };

    private void dosearch(String encodedKey, int startIndex) {
        if (TextUtils.isEmpty(encodedKey)) {
            layContent.finishRefresh();
            return;
        }
        if (!NetUtil.isHaveNet(mContext)) {
            layContent.finishRefresh();
            T.showShort(mContext, "网络不可用！");
            return;
        }
        mLoadingDialog.show();
        singleExecutor.execute(new TaskSearchNetPictures(mSearchNetPicturesCallback, encodedKey, startIndex));
    }

    private void switchVisibilityPopDropdown(View anchor) {
        List<SearchCategoryBean> ret = new ArrayList<>();
        ret.add(new SearchCategoryBean("网络搜索", SearchCategoryBean.CID_BAIDU_SEARCH));
        ret.add(new SearchCategoryBean("网址搜索", SearchCategoryBean.CID_PIC_URL));

        CategoryPoW pp = new CategoryPoW(mContext, mAnchorView, anchor, this);
        pp.refreshCategoryList(ret);
        pp.showPopupWindow(WeiZhi.Bottom);
    }

    @Override
    public void onDismiss() {
        EventBus.getDefault().unregister(this);
        singleExecutor.shutdownNow();
    }

    private boolean isHaveMore = true;
    private List<NetPictureBean> oldData = new ArrayList<>();

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        NetPictureBean bean = (NetPictureBean) parent.getAdapter().getItem(position);

        // gif
//        bean.picUrl
//        String gifName = RecordFileUtils.copyGifToRecordDir(cache.gif, mDraw.getRecordDir());
//        mDraw.getCurrentBoard().addImg(gifName);
        // bitmap.
//        Bitmap bmpScaled = BitmapScaleUtil.decodeSampledBitmapFromUrl(bean.picUrl,
//                Const.MAX_MEMORY_BMP_CAN_ALLOCATE, "http://img4.duitang.com");
//        String resName = RecordFileUtils.copyBitmapToRecordDir(bmpScaled, mDraw.getRecordDir());
//        mDraw.getCurrentBoard().addImg(resName);


    }

    @Override
    public void onSearchCategoryChanged(SearchCategoryBean categoryNew) {
        if (!mSearchView.getCategory().equals(categoryNew)) {
            mSearchView.setCategory(categoryNew);
            switchUi(categoryNew.getCategoryId());
        }
    }

}
