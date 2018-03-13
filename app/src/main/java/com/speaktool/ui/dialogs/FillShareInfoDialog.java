package com.speaktool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.SpeakToolApp;
import com.speaktool.api.CourseItem;
import com.speaktool.utils.BitmapScaleUtil;
import com.speaktool.utils.DeviceUtils;
import com.speaktool.utils.NetUtil;

import java.lang.ref.WeakReference;

/**
 * 分享信息
 *
 * @author shaoshuai
 */
public class FillShareInfoDialog extends Dialog implements View.OnClickListener {
    private LinearLayout ll_root;// 根视图
    private ImageView ivShareIcon;// 分享图标
    private TextView tvShareTitle;// 分享标题
    private Button btnCancel;// 取消
    private Button btnOk;// 发表
    private ImageView ivShareThumb;// 图片
    private EditText etShareContent;// 编辑框

    private String mBaseContent;// 分享文本内容
    private Context mActivityContext;
    private CourseItem mCourseItem;
    private WeakReference<ImageView> ivShareThumbRef;

    public FillShareInfoDialog(Context context, CourseItem course) {
        this(context, R.style.dialogTheme, course);

    }

    public FillShareInfoDialog(Context context, int theme, CourseItem course) {
        super(context, theme);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
        mActivityContext = context;
        mCourseItem = course;
        mBaseContent = getContext()
                .getString(R.string.share_baseContent, course.getRecordTitle(), course.getShareUrl());
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_fillshareinfo);

        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        ivShareIcon = (ImageView) findViewById(R.id.iv_ShareIcon);
        tvShareTitle = (TextView) findViewById(R.id.tvShareTitle);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnSend);
        ivShareThumb = (ImageView) findViewById(R.id.ivShareThumb);
        etShareContent = (EditText) findViewById(R.id.etShareContent);

        resetLayout();
        etShareContent.setText(mBaseContent);
        etShareContent.setSelection(mBaseContent.length());

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        ivShareThumbRef = new WeakReference<ImageView>(ivShareThumb);
        final String iconUrl = mCourseItem.getThumbnailImgPath();
        if (!TextUtils.isEmpty(iconUrl)) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    Bitmap bmp;
                    if (NetUtil.isNetPath(iconUrl)) {
                        bmp = BitmapScaleUtil
                                .decodeSampledBitmapFromUrl(iconUrl, Const.MAX_MEMORY_BMP_CAN_ALLOCATE, "");
                    } else {
                        bmp = BitmapScaleUtil.decodeSampledBitmapFromPath(iconUrl, Const.MAX_MEMORY_BMP_CAN_ALLOCATE);

                    }
                    if (bmp == null)
                        return;
                    final Bitmap bmpcopy = bmp;
                    SpeakToolApp.getUiHandler().post(new Runnable() {

                        @Override
                        public void run() {
                            ImageView ivThumb = ivShareThumbRef.get();
                            if (ivThumb != null) {
                                ivThumb.setImageBitmap(bmpcopy);
                            }

                        }
                    });

                }
            }).start();
        }
        //
        initDataForType();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }


}
