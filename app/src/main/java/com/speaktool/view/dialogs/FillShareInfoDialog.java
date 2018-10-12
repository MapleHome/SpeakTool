package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.speaktool.R;
import com.speaktool.api.CourseItem;
import com.speaktool.utils.DeviceUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分享信息
 *
 * @author shaoshuai
 */
public class FillShareInfoDialog extends Dialog {
    @BindView(R.id.ll_root) LinearLayout ll_root;// 根视图
    @BindView(R.id.iv_ShareIcon) ImageView ivShareIcon;// 分享图标
    @BindView(R.id.tvShareTitle) TextView tvShareTitle;// 分享标题
    @BindView(R.id.btnCancel) Button btnCancel;// 取消
    @BindView(R.id.btnSend) Button btnOk;// 发表
    @BindView(R.id.ivShareThumb) ImageView ivShareThumb;// 图片
    @BindView(R.id.etShareContent) EditText etShareContent;// 编辑框

    private String mBaseContent;// 分享文本内容
    private Context mActivityContext;
    private CourseItem mCourseItem;

    public FillShareInfoDialog(Context context, CourseItem course) {
        this(context, R.style.dialogTheme, course);
    }

    public FillShareInfoDialog(Context context, int theme, CourseItem course) {
        super(context, theme);
        mActivityContext = context;
        mCourseItem = course;
        mBaseContent = getContext()
                .getString(R.string.share_baseContent, course.getRecordTitle(), course.getShareUrl());
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fillshareinfo);
        ButterKnife.bind(this);

        resetLayout();
        etShareContent.setText(mBaseContent);
        etShareContent.setSelection(mBaseContent.length());

        String iconUrl = mCourseItem.getThumbnailImgPath();
        Glide.with(mActivityContext)
                .load(new File(iconUrl))
                .into(ivShareThumb);

        initDataForType();

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
        ViewGroup.LayoutParams lp1 = ll_root.getLayoutParams();
        lp1.height = height;
        lp1.width = width;
        ll_root.setLayoutParams(lp1);
    }

    private void initDataForType() {
        if (1 == 1) {
            tvShareTitle.setText("新浪微博分享");
            ivShareIcon.setImageResource(R.drawable.share_platform_sinaweibo);
        } else {
            tvShareTitle.setText("腾讯微博分享");
            ivShareIcon.setImageResource(R.drawable.share_platform_qqweibo);
        }
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }


}
