package com.speektool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.ishare_lib.ui.dialog.AlertDialog;
import com.ishare_lib.utils.DeviceUtils;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.activity.DrawActivity;
import com.speektool.activity.MainActivity;
import com.speektool.activity.PlayUrlVideoActivity;
import com.speektool.activity.PlayVideoActivity;
import com.speektool.api.AsyncDataLoader;
import com.speektool.api.CourseItem;
import com.speektool.api.Draw.PlayMode;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.RecordUploadBean;
import com.speektool.bean.ServerRecordBean;
import com.speektool.bean.ThirdParty;
import com.speektool.bean.UserBean;
import com.speektool.busevents.CourseThumbnailLoadedEvent;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.dao.UserDatabase;
import com.speektool.impl.platforms.PartnerPlat;
import com.speektool.impl.platforms.QQPlat;
import com.speektool.impl.platforms.SinaPlat;
import com.speektool.impl.platforms.TencentWeiboPlat;
import com.speektool.impl.platforms.WechartPlat;
import com.speektool.impl.player.PlayProcess;
import com.speektool.service.PlayService;
import com.speektool.service.UploadService;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.tasks.MyThreadFactory;
import com.speektool.tasks.TaskDeleteServerCourse;
import com.speektool.tasks.TaskDeleteServerCourse.DeleteServerCourseCallback;
import com.speektool.tasks.TaskGetThirdpartys;
import com.speektool.tasks.TaskGetThirdpartys.TaskGetThirdpartysCallback;
import com.speektool.utils.RecordFileUtils;
import com.speektool.utils.T;
import com.speektool.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

/**
 * 分享
 *
 * @author shaoshuai
 */
public class ShareDialog extends Dialog implements View.OnClickListener, OnDismissListener,
        ThirdPartyRunState, TaskGetThirdpartysCallback {

    private View firstFrame;// dialog跟布局
    private View loadingLayout;// 加载框跟布局

    private ImageView ivBack;// 返回
    private TextView tvTips;// 视频标题
    private ImageView ivPlay;// 播放
    private ImageView ivThumbnail;// 缩略图

    private ImageView ivShare;// 分享
    private ImageView ivCopyLink;// 复制路径
    private ImageView ivDeleteVideo;// 删除
    // 其他
    private Context mContext;
    private MainActivity mActivityContext;
    private CourseItem mItemBean;// 课程记录条目
    private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader;

    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new MyThreadFactory(
            "loadThirdpartysThread"));

    public ShareDialog(Context context, CourseItem itembean, AsyncDataLoader<String, Bitmap> loader) {
        this(context, R.style.dialogTheme, itembean, loader);
    }

    public ShareDialog(Context context, int theme, CourseItem itembean, AsyncDataLoader<String, Bitmap> loader) {
        super(context, theme);
        mContext = context;
        Preconditions.checkArgument(context instanceof Activity, "在Dialog中Context必须是Activity.");
        mActivityContext = (MainActivity) context;
        mLoadingDialogHelper = new LoadingDialogHelper(mActivityContext);

        mItemBean = itembean;
        mAppIconAsyncLoader = loader;

        this.setCanceledOnTouchOutside(false);
        this.setOnDismissListener(this);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_sharevideo);

        firstFrame = findViewById(R.id.firstFrame);// 跟布局
        resetLayout();
        loadingLayout = findViewById(R.id.loadingLayout);// 加载框跟布局
        loadingLayout.setVisibility(View.GONE);
        //
        ivBack = (ImageView) findViewById(R.id.ivBack);// 返回按钮
        tvTips = (TextView) findViewById(R.id.tvTips);// 视频标题
        ivPlay = (ImageView) findViewById(R.id.ivPlay);// 播放按钮
        ivThumbnail = (ImageView) findViewById(R.id.ivThumbnail);// 视频缩略图
        // 底部功能
        ivDeleteVideo = (ImageView) findViewById(R.id.ivDeleteVideo);// 删除
        ivCopyLink = (ImageView) findViewById(R.id.ivCopyLink);// 复制视频路径
        ivShare = (ImageView) findViewById(R.id.ivShare);// 分享按钮
        //

        ivBack.setOnClickListener(this);// 返回
        ivPlay.setOnClickListener(this);// 播放
        ivShare.setOnClickListener(this);// 分享
        ivCopyLink.setOnClickListener(this);// 复制路径
        ivDeleteVideo.setOnClickListener(this);// 删除
        loadingLayout.setOnClickListener(this);// 加载根视图

        tvTips.setText(mItemBean.getRecordTitle());// 设置标题
        Bitmap cache = mAppIconAsyncLoader.load(mItemBean.getThumbnailImgPath());
        // 缩略图路径：/storage/emulated/0/.spktl/records/15215343233/15215391310.jpg
        if (cache != null) {
            ivThumbnail.setImageBitmap(cache);// 设置缩略图
        }

        super.onCreate(savedInstanceState);
    }

    private void resetLayout() {
        // 调整dialog背景大小
        int width = DeviceUtils.getScreenWidth(mActivityContext);
        int height = DeviceUtils.getScreenHeight(mActivityContext);
        if (DeviceUtils.isPad(mActivityContext)) {// 平板
            if (DeviceUtils.isHengPing(mActivityContext)) {// 横屏
                width = (int) (width * 0.5);
                height = (int) (height * 0.5);
            } else {// 竖屏
                width = (int) (width * 0.7);
                height = (int) (height * 0.5);
            }
        } else {// 手机
            if (DeviceUtils.isHengPing(mActivityContext)) {// 横屏
                width = (int) (width * 0.5);
                height = (int) (height * 0.85);
            } else {// 竖屏
                width = (int) (width * 0.85);
                height = (int) (height * 0.5);
            }
        }

        ViewGroup.LayoutParams lp1 = firstFrame.getLayoutParams();
        lp1.height = height;
        lp1.width = width;
        firstFrame.setLayoutParams(lp1);
    }

    public void onEventMainThread(CourseThumbnailLoadedEvent event) {
        if (event.getKey().equals(mItemBean.getThumbnailImgPath())) {
            ivThumbnail.setImageBitmap(event.getIcon());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPlay:// 播放
                dismiss();
                if (mItemBean instanceof LocalRecordBean) {
                    playLocalRecord();// 播放本地视频
                } else {
                    playServerRecord();// 播放服务器视频
                }
                break;
            case R.id.ivShare:// 分享
                if (!loginSessionCheck()) {
                    break;
                }
                if (mActivityContext.isUploading(mItemBean.getThumbnailImgPath())) {
                    T.showShort(mContext, "课程正在上传,请稍后再试！");
                    break;
                }
                more();
                break;
            case R.id.ivDeleteVideo:// 删除
                if (mActivityContext.isUploading(mItemBean.getThumbnailImgPath())) {
                    T.showShort(mContext, "课程正在上传,请稍后再试！");
                    break;
                }
                showDeleteDialog();
                break;
            case R.id.ivCopyLink:// 复制路径
                if (mActivityContext.isUploading(mItemBean.getThumbnailImgPath())) {
                    T.showShort(mContext, "课程正在上传,请稍后再试！");
                    break;
                }
                if (mItemBean instanceof LocalRecordBean) {
                    copyLinkLocalRecord();// 复制本地记录路径
                } else {
                    copyLinkServerRecord();// 复制服务器记录路径
                }
                break;
            case R.id.ivBack:// 返回
                dismiss();
                break;
        }

    }

    @Override
    public void onConnectFail() {
        T.showShort(mContext, "服务器链接失败！请检查网络。");
    }

    @Override
    public void onResponseFail() {
        T.showShort(mContext, "服务器响应错误！");
    }

    /**
     * 用户是否登陆
     */
    private boolean loginSessionCheck() {
        int state = UserDatabase.getUserLoginState(getContext());
        if (state == UserBean.STATE_OUT) {
            dismiss();
            T.showShort(mContext, "当前用户未登陆！");
            return false;
        } else
            return true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        singleExecutor.shutdownNow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (isLoading()) {
            onFinishRun();
            return;
        }
        this.dismiss();
    }

    private boolean isLoading() {
        return loadingLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onStartRun() {
        SpeekToolApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                loadingLayout.setVisibility(View.VISIBLE);
            }
        });
        SpeekToolApp.getUiHandler().postDelayed(hideLoadingRunnable, 5000);
    }

    @Override
    public void onFinishRun() {
        SpeekToolApp.getUiHandler().removeCallbacks(hideLoadingRunnable);
        SpeekToolApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                loadingLayout.setVisibility(View.GONE);
                dismiss();
            }
        });
    }

    private Runnable hideLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            loadingLayout.setVisibility(View.GONE);
        }
    };
    // -------------------------------点击事件----------------------------------------------------
    private ThirdpartyListDialog mThirdpartyListDialog;

    @Override
    public void onThirdpartyLoaded(List<ThirdParty> result) {
        if (mThirdpartyListDialog.isShowing()) {
            mThirdpartyListDialog.refreshListData(result);
        }
    }

    /**
     * 分享
     */
    private void more() {
        mThirdpartyListDialog = new ThirdpartyListDialog(mActivityContext);
        mThirdpartyListDialog.show();
        mThirdpartyListDialog.setListItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mThirdpartyListDialog.dismiss();
                //
                ThirdParty party = (ThirdParty) parent.getAdapter().getItem(position);
                if (party == null) {
                    T.showShort(mContext, "分享失败！");
                    return;
                }
                int partyId = party.getId();
                switch (partyId) {
                    case ThirdParty.ID_QQ:
                        QQPlat lQQPlat = new QQPlat(mActivityContext, ShareDialog.this);
                        lQQPlat.share(mItemBean);
                        break;
                    case ThirdParty.ID_QQ_WEIBO:
                        TencentWeiboPlat lTencentWeiboPlat = new TencentWeiboPlat(mActivityContext, ShareDialog.this);
                        lTencentWeiboPlat.share(mItemBean);
                        break;
                    case ThirdParty.ID_SINA_WEIBO:
                        SinaPlat lSinaPlat = new SinaPlat(mActivityContext, ShareDialog.this);
                        lSinaPlat.share(mItemBean);
                        break;
                    case ThirdParty.ID_WECHART:
                        WechartPlat lWechartPlat = new WechartPlat(mActivityContext, ShareDialog.this);
                        lWechartPlat.share(mItemBean);
                        break;
                    case ThirdParty.ID_NET_COMPANY:
                        PartnerPlat lPartnerPlat = new PartnerPlat(mActivityContext, ShareDialog.this, party);
                        lPartnerPlat.share(mItemBean);
                        break;
                }
            }
        });
        UserBean session = UserDatabase.getUserLocalSession(mActivityContext);
        singleExecutor
                .execute(new TaskGetThirdpartys(this, TaskGetThirdpartys.PartyType.SHARE, session.getCompanyId()));
    }

    /**
     * 复制本地路径
     */
    private void copyLinkLocalRecord() {
        String shareUrl = mItemBean.getShareUrl();
        if (shareUrl != null) {
            copyUploadedLink(shareUrl);
        } else {
            // upload then share.
            if (loginSessionCheck())
                try {
                    uploadFileForCopylink();
                } catch (Exception e) {
                    e.printStackTrace();
                    T.showShort(mActivityContext, "upload fail!");
                }
        }
    }

    /**
     * 上传文件并复制连接
     */
    private void uploadFileForCopylink() throws Exception {
        LocalRecordBean localItem = (LocalRecordBean) mItemBean;
        RecordUploadBean recordUploadBean = RecordFileUtils.getSpklUploadBeanFromDir(localItem.getRecordDir(),
                mActivityContext);
        if (recordUploadBean == null) {
            T.showShort(mContext, "上传失败！");
            return;
        }
        Intent requestUploadIntent = new Intent(mActivityContext, UploadService.class);
        requestUploadIntent.putExtra(UploadService.EXTRA_ACTION, UploadService.ACTION_UPLOAD_TO_SPEAKTOOL);
        requestUploadIntent.putExtra(UploadService.EXTRA_REQUEST_DATA, recordUploadBean);
        requestUploadIntent.putExtra(UploadService.EXTRA_REQUEST_CODE, UploadRequestCode.COPY_LINK);
        mActivityContext.startService(requestUploadIntent);
        //
        dismiss();
    }

    /**
     * 复制服务器路径
     */
    private void copyLinkServerRecord() {
        copyUploadedLink(mItemBean.getShareUrl());
    }

    private void copyUploadedLink(String url) {
        ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(url);
        T.showShort(mContext, "录像地址已复制到剪贴板！");
    }

    /**
     * 播放本地视频
     */
    private void playLocalRecord() {
        LocalRecordBean localItem = (LocalRecordBean) mItemBean;
        Log.e("点击播放按钮", "播放本地视频" + localItem.getRecordDir());
        Intent playIntent = new Intent(mContext, PlayService.class);
        playIntent.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_PLAY);
        playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 【表格】 /storage/emulated/0/.spktl/records/15215343233
        playIntent.putExtra(PlayProcess.EXTRA_RECORD_DIR, localItem.getRecordDir());
        mContext.startService(playIntent);
    }

    /**
     * 播放服务器视频
     */
    private void playServerRecord() {
        ServerRecordBean serverItem = (ServerRecordBean) mItemBean;
        Log.e("点击播放按钮", "播放服务器视频");
        Log.e("播放服务器视频", serverItem.getRecordTitle() + "路径:" + serverItem.getVideoURL());
        String zipurlfile = serverItem.getZipURL();
        if (TextUtils.isEmpty(zipurlfile)) {// 检查 mp4 和 Zip.
            // 压缩文件url是空,开始检查Video文件url
            String videoFileUrl = serverItem.getVideoURL();
            if (TextUtils.isEmpty(videoFileUrl)) {
                // Video文件url是空，返回播放
                T.showShort(mContext, "播放失败！");
                return;
            }
            // 播放Video文件
            toPlayPage(videoFileUrl);// 去播放界面
            return;
        }
        final String zipurlfileReal = Const.SPEEKTOOL_SERVER__URL + zipurlfile;
        // downloading.
        final Dialog dia = ProgressDialogOffer.offerDialogAsActivity(mActivityContext, "正在加载");
        dia.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final File tempzip = new File(Const.TEMP_DIR, "temp.zip");
                if (tempzip.exists()) {
                    tempzip.delete();// 删除tempzip
                }
                try {
                    tempzip.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    dia.dismiss();
                    showPlayFailInfoInUi();
                    return;
                }
                final File tempdir = new File(Const.TEMP_DIR + "temp/");
                if (tempdir.exists()) {
                    deleteDir(tempdir);// 删除tempdir
                }
                tempdir.mkdirs();
                //
                File saveFile =
//						UniversalHttp.downloadFile(zipurlfileReal, "", tempzip);
                        null;
                if (saveFile != null) {
                    try {
                        ZipUtils.upZipFile(tempzip, tempdir.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        dia.dismiss();
                        showPlayFailInfoInUi();
                        return;
                    }
                    LocalRecordBean item = new LocalRecordBean();
                    item.setDuration(mItemBean.getDuration());
                    item.setRecordDir(tempdir.getAbsolutePath());
                    //
                    dia.dismiss();
                    // toDrawPage(item);// 去播放界面
                    toPlayVideoPage(item);
                } else {
                    dia.dismiss();
                    showPlayFailInfoInUi();
                }
            }
        }).start();
    }

    protected void deleteDir(File dir) {
        if (dir == null || !dir.exists())
            return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files)
                f.delete();
        }
        dir.delete();
    }

    private void showPlayFailInfoInUi() {
        SpeekToolApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                T.showShort(mContext, "播放失败！");
            }
        });
    }

    private void showDeleteDialog() {
        new AlertDialog(mActivityContext).builder().setTitle("提示").setMsg("请问是否确定删除录像？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRecord();
                    }

                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    private final LoadingDialogHelper mLoadingDialogHelper;

    /**
     * 删除记录
     */
    private void deleteRecord() {
        if (mItemBean instanceof LocalRecordBean) {
            if (mItemBean.getShareUrl() == null) {
                mLoadingDialogHelper.showLoading("正在加载", null);
                singleExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        deleteLoacalRecord((LocalRecordBean) mItemBean);
                        SpeekToolApp.getUiHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialogHelper.dismissLoading();
                                afterDeleteSuccess();
                            }
                        });
                    }
                });
            } else {
                deleteServerRecord(true);
            }
        } else {
            deleteServerRecord(false);
        }
    }

    /**
     * 删除服务器端记录
     */
    private void deleteServerRecord(final boolean isNeedDeleteLocalRecord) {
        if (!loginSessionCheck())
            return;
        mLoadingDialogHelper.showLoading("正在加载", null);
        singleExecutor.execute(new TaskDeleteServerCourse(mDeleteServerCourseCallback, mItemBean,
                isNeedDeleteLocalRecord));
    }

    /**
     * 删除本地记录
     */
    private void deleteLoacalRecord(LocalRecordBean localRecord) {
        String dirpath = localRecord.getRecordDir();
        File dir = new File(dirpath);
        if (dir == null || !dir.exists())
            return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files)
                f.delete();
        }
        dir.delete();
    }

    private DeleteServerCourseCallback mDeleteServerCourseCallback = new DeleteServerCourseCallback() {
        @Override
        public void onDeleteSuccess() {
            mLoadingDialogHelper.dismissLoading();
            afterDeleteSuccess();
        }

        @Override
        public void onDeleteFail() {
            mLoadingDialogHelper.dismissLoading();
            afterDeleteSuccess();
        }

        @Override
        public void onConnectFail() {
            mLoadingDialogHelper.dismissLoading();
            T.showShort(mContext, "服务器链接失败！请检查网络。");
        }
    };

    private void afterDeleteSuccess() {
        new AlertDialog(mActivityContext).builder().setTitle("提示").setMsg("课程删除成功！")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new RefreshCourseListEvent());
                        dismiss();
                    }
                }).show();
    }

    // -----------------------------------------------------------------------------------------------

    /**
     * 去播放界面
     */
    private void toPlayPage(String videoFileUrl) {
        Intent it = new Intent(getContext(), PlayUrlVideoActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayUrlVideoActivity.EXTRA_VIDEO_URL, Const.SPEEKTOOL_SERVER__URL + videoFileUrl);
        getContext().startActivity(it);
    }

    /**
     * 新建一个讲讲画板
     */
    private void toDrawPage(LocalRecordBean item) {
        Intent it = new Intent(getContext(), DrawActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(DrawActivity.EXTRA_PLAY_MODE, PlayMode.PLAY);
        it.putExtra(DrawActivity.EXTRA_RECORD_BEAN, item);
        getContext().startActivity(it);
    }

    /**
     * 去本地播放界面
     */
    private void toPlayVideoPage(LocalRecordBean item) {
        Intent it = new Intent(getContext(), PlayVideoActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayVideoActivity.EXTRA_PLAY_MODE, PlayMode.PLAY);
        it.putExtra(PlayVideoActivity.EXTRA_RECORD_BEAN, item);
        getContext().startActivity(it);
    }
}
