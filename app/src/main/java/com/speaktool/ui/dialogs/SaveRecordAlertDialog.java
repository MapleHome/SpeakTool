package com.speaktool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.common.base.Preconditions;
import com.speaktool.R;
import com.speaktool.api.Draw;

/**
 * 保存记录
 *
 * @author shaoshuai
 */
public class SaveRecordAlertDialog extends Dialog implements View.OnClickListener {
    private Button btnSaveVideo;// 保存
    private Button btnNotSaveVideo;// 不保存
    private Button btnCancelExit;// 取消

    private Context mActivityContext;
    private Draw mDraw;

    public SaveRecordAlertDialog(Context context, Draw draw) {
        this(context, R.style.dialogThemeFullScreen, draw);
    }

    public SaveRecordAlertDialog(Context context, int theme, Draw draw) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
        mActivityContext = context;
        mDraw = draw;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_savevideo_alert);
        btnSaveVideo = (Button) findViewById(R.id.btnSaveVideo);
        btnNotSaveVideo = (Button) findViewById(R.id.btnNotSaveVideo);
        btnCancelExit = (Button) findViewById(R.id.btnCancelExit);

        btnSaveVideo.setOnClickListener(this);
        btnNotSaveVideo.setOnClickListener(this);
        btnCancelExit.setOnClickListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == btnSaveVideo) {
            // mDraw.saveRecord(null);
            this.dismiss();
            FillSaveInfoDialog filldia = new FillSaveInfoDialog(mActivityContext, mDraw);
            filldia.show();
        } else if (v == btnNotSaveVideo) {
            this.dismiss();
            mDraw.exitDrawWithoutSave();
        } else if (v == btnCancelExit) {
            this.dismiss();
        }
    }
}
