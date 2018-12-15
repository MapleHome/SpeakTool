package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maple.msdialog.AlertDialog;
import com.speaktool.R;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.ui.Draw.RecordBean;
import com.speaktool.ui.Player.PlayVideoActivity;
import com.speaktool.utils.DeviceUtils;
import com.speaktool.utils.FileUtils;
import com.speaktool.utils.T;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 分享
 *
 * @author shaoshuai
 */
public class CourseItemDesDialog extends Dialog {
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
    private RecordBean mItemBean;// 课程记录条目

    public CourseItemDesDialog(Context context, RecordBean itembean) {
        super(context, R.style.dialogTheme);
        mContext = context;
        mItemBean = itembean;

        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sharevideo);
        ButterKnife.bind(this);

        resetLayout();

        tvTips.setText(mItemBean.title);// 设置标题
        Glide.with(mContext)
                .load(new File(mItemBean.thumbNailPath))
                .into(ivThumbnail);
    }

    private void resetLayout() {
        // 调整dialog背景大小
        int width = DeviceUtils.getScreenWidth(mContext);
        int height = DeviceUtils.getScreenHeight(mContext);
        if (DeviceUtils.isPad(mContext)) {// 平板
            if (DeviceUtils.isHengPing(mContext)) {// 横屏
                width = (int) (width * 0.5);
                height = (int) (height * 0.5);
            } else {// 竖屏
                width = (int) (width * 0.7);
                height = (int) (height * 0.5);
            }
        } else {// 手机
            if (DeviceUtils.isHengPing(mContext)) {// 横屏
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


    // -------------------------------点击事件----------------------------------------------------

    @OnClick(R.id.ivBack)
    void onBack() {
        this.dismiss();
    }

    @OnClick(R.id.ivPlay)
    void toPlayVideoPage() {
        this.dismiss();

        Intent it = new Intent(getContext(), PlayVideoActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayVideoActivity.EXTRA_RECORD_BEAN, mItemBean);
        getContext().startActivity(it);
    }

    @OnClick(R.id.ivShare)
    void more() {
        T.showShort(mContext, "click more！");
    }

    @OnClick(R.id.ivCopyLink)
    void copyLinkLocalRecord() {
        String shareUrl = mItemBean.thumbNailName;// TODO  url
        if (shareUrl != null) {
            ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(shareUrl);
            T.showShort(mContext, "录像地址已复制到剪贴板！");
        } else {
            T.showShort(mContext, "录像地址为空！");
        }
    }

    @OnClick(R.id.ivDeleteVideo)
    void showDeleteDialog() {
        new AlertDialog(mContext)
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

    private void deleteRecord() {
        File dir = new File(mItemBean.dir);
        FileUtils.deleteDir(dir);

        new AlertDialog(mContext)
                .setTitle("提示")
                .setMessage("课程删除成功！")
                .setRightButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new RefreshCourseListEvent());
                        onBack();
                    }
                }).show();
    }

}
