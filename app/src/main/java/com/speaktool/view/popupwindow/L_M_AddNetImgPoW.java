package com.speaktool.view.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.common.collect.Lists;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.SpeakToolApp;
import com.speaktool.api.AsyncDataLoader;
import com.speaktool.api.Draw;
import com.speaktool.bean.NetPictureBean;
import com.speaktool.bean.PicDataHolder;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.busevents.NetPictureThumbnailLoadedEvent;
import com.speaktool.tasks.MyThreadFactory;
import com.speaktool.tasks.TaskGetNetImage;
import com.speaktool.tasks.TaskGetNetImage.NetImageLoadListener;
import com.speaktool.tasks.TaskLoadPictureSearchCategory;
import com.speaktool.tasks.TaskSearchNetPictures;
import com.speaktool.tasks.TaskSearchNetPictures.SearchNetPicturesCallback;
import com.speaktool.ui.adapters.AdapterNetPictures;
import com.speaktool.ui.base.AbsListScrollListener;
import com.speaktool.utils.BitmapScaleUtil;
import com.speaktool.utils.NetUtil;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.T;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.view.gif.GifDrawable;
import com.speaktool.view.layouts.ItemViewNetPicture;
import com.speaktool.view.layouts.SearchView;
import com.speaktool.view.popupwindow.CategoryPoW.SearchCategoryChangedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 顶部功能栏——更多功能——添加图片——网络图片
 *
 * @author shaoshuai
 */
public class L_M_AddNetImgPoW extends BasePopupWindow implements OnClickListener, OnDismissListener, OnItemClickListener,
        NetImageLoadListener, SearchCategoryChangedListener {
    private SearchView mSearchView;
    private SmartRefreshLayout layContent;
    private GridView mNetPicsGrid;
    private ImageView netImagePreview;

    private Draw mDraw;
    private ExecutorService singleExecutor = Executors
            .newSingleThreadExecutor(new MyThreadFactory("getNetImageThread"));
    private AdapterNetPictures mAdapterNetPictures;
    private AsyncDataLoader<String, PicDataHolder> mDataLoader;
    private LoadingDialog mLoadingDialog;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_add_net_image, null);
    }

    public L_M_AddNetImgPoW(Context context, View view, Draw draw, AsyncDataLoader<String, PicDataHolder> loader) {
        this(context, view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw, loader);
    }

    public L_M_AddNetImgPoW(Context context, View view, int w, int h, Draw draw,
                            AsyncDataLoader<String, PicDataHolder> loader) {
        super(context, view, w, h);
        mDraw = draw;
        mDataLoader = loader;
        EventBus.getDefault().register(this);
        mLoadingDialog = new LoadingDialog(mContext);

        mSearchView = (SearchView) mRootView.findViewById(R.id.searchView);
        layContent = mRootView.findViewById(R.id.layContent);
        netImagePreview = (ImageView) mRootView.findViewById(R.id.netImagePreview);
        mNetPicsGrid = (GridView) mRootView.findViewById(R.id.listNetImage);

        // mNetPicsGrid.getRefreshableView().setColumnWidth(mRootView.getWidth()/6);
//        mNetPicsGrid.setMode(Mode.BOTH);
//        mNetPicsGrid.setOnRefreshListener(this);
        mNetPicsGrid.setOnScrollListener(netpicGridAbsListScrollListener);
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

        SearchCategoryBean defCategory = TaskLoadPictureSearchCategory.getDefCategory(mContext);
        mSearchView.setCategory(defCategory);
        //
        mNetPicsGrid.setEmptyView(makeEmptyView("还没有图片，请重新搜索。"));

        mAdapterNetPictures = new AdapterNetPictures(mContext, null);
        mNetPicsGrid.setAdapter(mAdapterNetPictures);

        mNetPicsGrid.setOnItemClickListener(this);
        mSearchView.setDropdownClickListener(this);
        mSearchView.setSearchClickListener(this);
        netImagePreview.setOnClickListener(this);
        this.setOnDismissListener(this);
        switchUi(defCategory.getCategoryId());
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

    private AbsListScrollListener netpicGridAbsListScrollListener = new AbsListScrollListener() {
        @Override
        public void whenIdle() {
            for (int i = mfirstVisibleItem; i <= mlastvisibleItem; i++) {
                NetPictureBean bean = (NetPictureBean) mAdapterNetPictures.getItem(i);
                final String imageUrl = bean.thumbUrl;
                if (TextUtils.isEmpty(imageUrl))
                    continue;
                final ItemViewNetPicture item = (ItemViewNetPicture) mNetPicsGrid.findViewWithTag(imageUrl);
                if (item == null)
                    continue;
                if (mDataLoader == null) {
                    return;
                }
                final PicDataHolder cache = mDataLoader.load(imageUrl, item.getWidth(), item.getHeight());
                //
                if (cache != null) {
                    if (BitmapScaleUtil.isGif(imageUrl)) {
                        GifDrawable gifd;
                        try {
                            gifd = new GifDrawable(cache.gif);
                            item.setImage(gifd);
                        } catch (IOException e) {
                            e.printStackTrace();
                            item.setImage(new BitmapDrawable(getErrorBmp()));
                        }
                    } else {// bmp.
                        Bitmap bpScaled = BitmapFactory.decodeByteArray(cache.bpScaled, 0, cache.bpScaled.length);
                        item.setImage(new BitmapDrawable(bpScaled));
                    }
                } else {
                    item.setLoading();
                }
            }
        }

        @Override
        public void whenFling() {
            mDataLoader.cancelAll();
        }
    };

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
        mLoadingDialog.show();
        singleExecutor.execute(new TaskGetNetImage(this, inputUrl));
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
            SpeakToolApp.getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    int first = mNetPicsGrid.getFirstVisiblePosition();
                    int last = mNetPicsGrid.getLastVisiblePosition();
                    netpicGridAbsListScrollListener.setVisibleItems(first, last);
                    netpicGridAbsListScrollListener.whenIdle();
                }
            });
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
        CategoryPoW pp = new CategoryPoW(mContext, mAnchorView, anchor, this);
        pp.refreshCategoryList(TaskLoadPictureSearchCategory.getCategories(mContext));
        pp.showPopupWindow(WeiZhi.Bottom);
    }

    @Override
    public void onDismiss() {
        EventBus.getDefault().unregister(this);
        singleExecutor.shutdownNow();
        mDataLoader = null;

    }

    private boolean isHaveMore = true;
    private List<NetPictureBean> oldData = Lists.newArrayList();

    private void addImgFail() {
        SpeakToolApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                T.showShort(mContext, "图片添加失败！");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        final NetPictureBean bean = (NetPictureBean) parent.getAdapter().getItem(position);
        if (bean == null)
            return;
        mLoadingDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BitmapScaleUtil.isGif(bean.picUrl)) {// gif.
                    PicDataHolder cache = mDataLoader.getCache(bean.picUrl);
                    if (cache != null) {// cache.
                        try {
                            final String gifName = RecordFileUtils.copyGifToRecordDir(cache.gif, mDraw.getRecordDir());
                            SpeakToolApp.getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mDraw.getCurrentBoard().addImg(gifName);
                                }
                            });
                        } catch (Exception e) {
                            addImgFail();
                            e.printStackTrace();
                        }
                    } else {// download.
                        try {
                            byte[] buf =
//									UniversalHttp.downloadFile(bean.picUrl, "http://img4.duitang.com");// gif.
                                    null;
                            if (buf != null) {
                                final String gifName = RecordFileUtils.copyGifToRecordDir(buf, mDraw.getRecordDir());
                                SpeakToolApp.getUiHandler().post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // stub
                                        mDraw.getCurrentBoard().addImg(gifName);
                                    }
                                });
                            } else {
                                addImgFail();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            addImgFail();
                        }
                    }
                } else {// bitmap.
                    PicDataHolder cache = mDataLoader.getCache(bean.picUrl);
                    if (cache != null) {
                        final String resName = RecordFileUtils.copyBitmapToRecordDir(cache.bpScaled,
                                mDraw.getRecordDir());
                        SpeakToolApp.getUiHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mDraw.getCurrentBoard().addImg(resName);
                            }
                        });
                    } else {// download bmp.
                        Bitmap bmpScaled = BitmapScaleUtil.decodeSampledBitmapFromUrl(bean.picUrl,
                                Const.MAX_MEMORY_BMP_CAN_ALLOCATE, "http://img4.duitang.com");

                        if (bmpScaled != null) {
                            final String resName = RecordFileUtils.copyBitmapToRecordDir(bmpScaled,
                                    mDraw.getRecordDir());
                            SpeakToolApp.getUiHandler().post(new Runnable() {

                                @Override
                                public void run() {
                                    mDraw.getCurrentBoard().addImg(resName);
                                }
                            });
                        } else {// if downoad fail,we use its thumbpic.

                            ItemViewNetPicture itemview = (ItemViewNetPicture) view;
                            Bitmap thumb = itemview.getThumbBitmap();
                            if (thumb != null) {

                                final String resName = RecordFileUtils.copyBitmapToRecordDir(thumb,
                                        mDraw.getRecordDir());
                                SpeakToolApp.getUiHandler().post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // stub
                                        mDraw.getCurrentBoard().addImg(resName);
                                    }
                                });
                            } else {
                                addImgFail();
                            }
                        }
                    }
                }
                mLoadingDialog.dismiss();
            }
        }).start();
    }

    @Override
    public void onNetImageLoaded(Bitmap result) {
        mLoadingDialog.dismiss();
        if (result != null)
            netImagePreview.setImageBitmap(result);
        else
            T.showShort(mContext, "服务器链接失败！请检查网络");
    }

    @Override
    public void onSearchCategoryChanged(SearchCategoryBean categoryNew) {
        if (!mSearchView.getCategory().equals(categoryNew)) {
            mSearchView.setCategory(categoryNew);
            switchUi(categoryNew.getCategoryId());
        }
    }

    private Bitmap getErrorBmp() {
        return BitmapFactory.decodeResource(SpeakToolApp.app().getResources(), R.drawable.error);
    }

    @Subscribe
    public void onEventMainThread(NetPictureThumbnailLoadedEvent event) {
        String key = event.getKey();
        ItemViewNetPicture item = (ItemViewNetPicture) mNetPicsGrid.findViewWithTag(key);
        //
        if (item != null) {
            if (event.isError()) {
                item.setImage(new BitmapDrawable(getErrorBmp()));
                return;
            }
            item.setImage(event.getIcon());
        }
    }
}
