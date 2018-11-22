package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.speaktool.R;


/**
 * 加载Dialog
 *
 * @author shaoshuai
 */
public class LoadingDialog extends Dialog {

    private final TextView tvMsg;

    public LoadingDialog(Context context) {
        this(context, "loading...");
    }

    public LoadingDialog(Context context, String msg) {
        super(context, R.style.CustomProgressDialog);

        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        this.setCancelable(true);
        this.setContentView(R.layout.dialog_common_progress);

        tvMsg = findViewById(R.id.tvMsg);
        tvMsg.setTextColor(Color.WHITE);
        tvMsg.setText(msg);

    }

    public void show(String msg) {
        show(msg, true);
    }

    public void show(String msg, boolean cancelable) {
        this.setCancelable(cancelable);// 可撤销
        tvMsg.setText(msg);
        this.show();
    }

}
