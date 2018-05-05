package com.speaktool.ui.dialogs.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.speaktool.R;
import com.speaktool.api.CourseItem;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 微信分享
 */
public class WechartShareDialog extends Dialog {
    private CourseItem mCourseItem;
    private Context mActivityContext;

    public WechartShareDialog(Context context, CourseItem course) {
        this(context, R.style.dialogTheme, course);
    }

    public WechartShareDialog(Context context, int theme, CourseItem course) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
        mActivityContext = context;
        mCourseItem = course;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wechart_sharechoice);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnShareToWechartMoments)
    public void shareToWechartMoments() {
        this.dismiss();
    }

    @OnClick(R.id.btnShareToWechart)
    public void shareToWechart() {
        this.dismiss();
    }

    @OnClick(R.id.btnCancelExit)
    public void clickCancel() {
        this.dismiss();
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

}
