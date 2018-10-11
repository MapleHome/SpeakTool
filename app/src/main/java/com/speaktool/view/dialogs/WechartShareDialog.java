package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.speaktool.R;
import com.speaktool.api.CourseItem;

public class WechartShareDialog extends Dialog implements View.OnClickListener {
    private Button btnShareToWechartMoments;
    private Button btnShareToWechart;
    private Button btnCancelExit;
    private CourseItem mCourseItem;
    private Context mActivityContext;

    public WechartShareDialog(Context context, CourseItem course) {
        this(context, R.style.dialogTheme, course);
    }

    public WechartShareDialog(Context context, int theme, CourseItem course) {
        super(context, theme);
        mActivityContext = context;
        mCourseItem = course;
        init();
    }

    private void init() {
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_wechart_sharechoice);
        btnShareToWechartMoments = (Button) findViewById(R.id.btnShareToWechartMoments);
        btnShareToWechartMoments.setOnClickListener(this);
        btnShareToWechart = (Button) findViewById(R.id.btnShareToWechart);
        btnShareToWechart.setOnClickListener(this);
        btnCancelExit = (Button) findViewById(R.id.btnCancelExit);
        btnCancelExit.setOnClickListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == btnShareToWechartMoments) {
            this.dismiss();

        } else if (v == btnShareToWechart) {
            this.dismiss();

        } else if (v == btnCancelExit) {
            this.dismiss();

        }
    }
}
