package com.speaktool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.common.base.Preconditions;
import com.speaktool.R;

/**
 * QQ分享选择对话框
 *
 * @author Maple Shao
 */
public class QQShareChoiceDialog extends Dialog implements View.OnClickListener {
    private Button btnShareToQZone;// 分享到QQ空间
    private Button btnShareToQQ;// 分享到QQ好友
    private Button btnCancelExit;// 取消


    public QQShareChoiceDialog(Context context) {
        this(context, R.style.dialogTheme);
    }

    public QQShareChoiceDialog(Context context, int theme) {
        super(context, theme);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");

        init();
    }

    private void init() {
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_qq_sharechoice);

        btnShareToQZone = (Button) findViewById(R.id.btnShareToQZone);
        btnShareToQQ = (Button) findViewById(R.id.btnShareToQQ);
        btnCancelExit = (Button) findViewById(R.id.btnCancelExit);

        btnShareToQZone.setOnClickListener(this);
        btnShareToQQ.setOnClickListener(this);
        btnCancelExit.setOnClickListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        this.dismiss();

    }

    @Override
    public void onClick(View v) {
        if (v == btnShareToQZone) {// 分享到QQ空间
            this.dismiss();

        } else if (v == btnShareToQQ) {// 分享到QQ
            this.dismiss();

        } else if (v == btnCancelExit) {// 取消
            this.dismiss();
        }
    }


}
