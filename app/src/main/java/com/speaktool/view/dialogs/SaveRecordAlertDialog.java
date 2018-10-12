package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.speaktool.R;
import com.speaktool.api.Draw;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 保存记录
 *
 * @author shaoshuai
 */
public class SaveRecordAlertDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.btnSaveVideo) Button btnSaveVideo;// 保存
    @BindView(R.id.btnNotSaveVideo) Button btnNotSaveVideo;// 不保存
    @BindView(R.id.btnCancelExit) Button btnCancelExit;// 取消

    private Context mActivityContext;
    private Draw mDraw;


    public SaveRecordAlertDialog(Context context, Draw draw) {
        super(context, R.style.dialogThemeFullScreen);
        setCanceledOnTouchOutside(false);
        mActivityContext = context;
        mDraw = draw;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_savevideo_alert);
        ButterKnife.bind(this);

        btnSaveVideo.setOnClickListener(this);
        btnNotSaveVideo.setOnClickListener(this);
        btnCancelExit.setOnClickListener(this);
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
            new FillSaveInfoDialog(mActivityContext, mDraw).show();
        } else if (v == btnNotSaveVideo) {
            this.dismiss();
            mDraw.exitDrawWithoutSave();
        } else if (v == btnCancelExit) {
            this.dismiss();
        }
    }
}
