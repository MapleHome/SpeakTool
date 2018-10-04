package com.speaktool.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.common.base.Preconditions;
import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * QQ分享选择对话框
 *
 * @author Maple Shao
 */
public class QQShareDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.btnShareToQZone) Button btnShareToQZone;// 分享到QQ空间
    @BindView(R.id.btnShareToQQ) Button btnShareToQQ;// 分享到QQ好友
    @BindView(R.id.btnCancelExit) Button btnCancelExit;// 取消


    public QQShareDialog(Context context) {
        this(context, R.style.dialogTheme);
    }

    public QQShareDialog(Context context, int theme) {
        super(context, theme);
        this.setCanceledOnTouchOutside(false);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qq_sharechoice);
        ButterKnife.bind(this);

        btnShareToQZone.setOnClickListener(this);
        btnShareToQQ.setOnClickListener(this);
        btnCancelExit.setOnClickListener(this);
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
