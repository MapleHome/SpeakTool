package com.speaktool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.maple.msdialog.AlertDialog;
import com.speaktool.R;
import com.speaktool.SpeakToolApp;
import com.speaktool.api.AsyncDataLoader;
import com.speaktool.api.CourseItem;
import com.speaktool.api.Draw.PlayMode;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.bean.ThirdParty;
import com.speaktool.bean.UserBean;
import com.speaktool.busevents.CourseThumbnailLoadedEvent;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.dao.UserDatabase;
import com.speaktool.impl.player.PlayProcess;
import com.speaktool.service.PlayService;
import com.speaktool.tasks.MyThreadFactory;
import com.speaktool.tasks.TaskGetThirdpartys;
import com.speaktool.tasks.TaskGetThirdpartys.TaskGetThirdpartysCallback;
import com.speaktool.ui.activity.DrawActivity;
import com.speaktool.ui.activity.MainActivity;
import com.speaktool.ui.activity.PlayVideoActivity;
import com.speaktool.utils.DeviceUtils;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.T;

import java.io.File;
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
        TaskGetThirdpartysCallback {

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
        ivBack = findViewById(R.id.ivBack);// 返回按钮
        tvTips = findViewById(R.id.tvTips);// 视频标题
        ivPlay = findViewById(R.id.ivPlay);// 播放按钮
        ivThumbnail = findViewById(R.id.ivThumbnail);// 视频缩略图
        // 底部功能
        ivDeleteVideo = findViewById(R.id.ivDeleteVideo);// 删除
        ivCopyLink = findViewById(R.id.ivCopyLink);// 复制视频路径
        ivShare = findViewById(R.id.ivShare);// 分享按钮
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

                LocalRecordBean localItem = (LocalRecordBean) mItemBean;
//                toDrawPager(localItem);
                toPlayVideoPage(localItem);
//                playLocalRecord(localItem);
                break;
            case R.id.ivShare:// 分享
                more();
                break;
            case R.id.ivDeleteVideo:// 删除
                showDeleteDialog();
                break;
            case R.id.ivCopyLink:// 复制路径
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


    @Override
    public void onDismiss(DialogInterface dialog) {
        singleExecutor.shutdownNow();
        EventBus.getDefault().unregister(this);
    }


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
                // mItemBean

            }
        });
        UserBean session = UserDatabase.getUserLocalSession(mActivityContext);
        singleExecutor.execute(new TaskGetThirdpartys(this, TaskGetThirdpartys.PartyType.SHARE, session.getCompanyId()));
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
    private void uploadFileForCopylink() {
        LocalRecordBean localItem = (LocalRecordBean) mItemBean;
        RecordUploadBean recordUploadBean = RecordFileUtils.getSpklUploadBeanFromDir(localItem.getRecordDir(),
                mActivityContext);
        if (recordUploadBean == null) {
            T.showShort(mContext, "上传失败！");
            return;
        }
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


    private void showDeleteDialog() {
        new AlertDialog(mActivityContext)
                .setTitle("提示")
                .setMessage("请问是否确定删除录像？")
                .setRightButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRecord();
                    }

                })
                .setLeftButton("取消", null)
                .show();
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
                        deleteLocalRecord((LocalRecordBean) mItemBean);
                        SpeakToolApp.getUiHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialogHelper.dismissLoading();
                                afterDeleteSuccess();
                            }
                        });
                    }
                });
            } else {
                // deleteServerRecord(true);
            }
        } else {
            // deleteServerRecord(false);
        }
    }


    /**
     * 删除本地记录
     */
    private void deleteLocalRecord(LocalRecordBean localRecord) {
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


    private void afterDeleteSuccess() {
        new AlertDialog(mActivityContext)
                .setTitle("提示")
                .setMessage("课程删除成功！")
                .setRightButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new RefreshCourseListEvent());
                        dismiss();
                    }
                }).show();
    }

    // -----------------------------------------------------------------------------------------------

    /**
     * 播放本地视频
     */
    private void playLocalRecord(LocalRecordBean localItem) {
        Log.e("点击播放按钮", "播放本地视频" + localItem.getRecordDir());
        Intent playIntent = new Intent(mContext, PlayService.class);
        playIntent.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_PLAY);
        playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 【表格】 /storage/emulated/0/.spktl/records/15215343233
        playIntent.putExtra(PlayProcess.EXTRA_RECORD_DIR, localItem.getRecordDir());
        mContext.startService(playIntent);
    }


    /**
     * 去本地播放界面
     */
    private void toPlayVideoPage(LocalRecordBean item) {
        Intent it = new Intent(getContext(), PlayVideoActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayVideoActivity.EXTRA_RECORD_BEAN, item);
        getContext().startActivity(it);
    }

    /**
     * 去画板页面
     *
     * @param item
     */
    private void toDrawPager(LocalRecordBean item) {
        Intent it = new Intent(getContext(), DrawActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(DrawActivity.EXTRA_PLAY_MODE, PlayMode.PLAY);
        it.putExtra(DrawActivity.EXTRA_RECORD_BEAN, item);
        getContext().startActivity(it);
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
}
