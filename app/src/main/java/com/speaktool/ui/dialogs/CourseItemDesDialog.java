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
import com.speaktool.utils.T;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 课程条目信息
 *
 * @author shaoshuai
 */
public class CourseItemDesDialog extends Dialog implements OnDismissListener, TaskGetThirdpartysCallback {
    @BindView(R.id.firstFrame) View firstFrame;// dialog跟布局
    @BindView(R.id.ivBack) ImageView ivBack;// 返回
    @BindView(R.id.tvTips) TextView tvTips;// 视频标题
    @BindView(R.id.ivPlay) ImageView ivPlay;// 播放
    @BindView(R.id.ivThumbnail) ImageView ivThumbnail;// 缩略图
    // btn
    @BindView(R.id.ivShare) ImageView ivShare;// 分享
    @BindView(R.id.ivCopyLink) ImageView ivCopyLink;// 复制路径
    @BindView(R.id.ivDeleteVideo) ImageView ivDeleteVideo;// 删除
    // 其他
    private Context mContext;
    private MainActivity mActivityContext;
    private CourseItem mItemBean;// 课程记录条目
    private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader;
    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new MyThreadFactory(
            "loadThirdpartysThread"));

    public CourseItemDesDialog(Context context, CourseItem itembean, AsyncDataLoader<String, Bitmap> loader) {
        this(context, R.style.dialogTheme, itembean, loader);
    }

    public CourseItemDesDialog(Context context, int theme, CourseItem itembean, AsyncDataLoader<String, Bitmap> loader) {
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sharevideo);
        ButterKnife.bind(this);

        resetLayout();
        initData();
    }

    private void initData() {
        tvTips.setText(mItemBean.getRecordTitle());// 设置标题
        Bitmap cache = mAppIconAsyncLoader.load(mItemBean.getThumbnailImgPath());
        // 缩略图路径：/storage/emulated/0/.spktl/records/15215343233/15215391310.jpg
        if (cache != null) {
            ivThumbnail.setImageBitmap(cache);// 设置缩略图
        }
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

    @OnClick(R.id.ivBack)
    public void back() {
        dismiss();
    }

    @OnClick(R.id.ivPlay)
    public void toPlayPage() {
        dismiss();
        LocalRecordBean localItem = (LocalRecordBean) mItemBean;
//        toDrawPager(localItem);
        toPlayVideoPage(localItem);
//        playLocalRecord(localItem);
    }

    /**
     * 分享
     */
    @OnClick(R.id.ivShare)
    public void more() {
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
    @OnClick(R.id.ivCopyLink)
    public void copyLinkLocalRecord() {
        String shareUrl = mItemBean.getShareUrl();
        if (shareUrl != null) {
            ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(shareUrl);
            T.showShort(mContext, "录像地址已复制到剪贴板！");
        } else {
            T.showShort(mContext, "录像地址是空的！");
        }
    }

    @OnClick(R.id.ivDeleteVideo)
    public void showDeleteDialog() {
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
