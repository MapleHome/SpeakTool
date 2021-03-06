package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        super(context, R.style.dialogTheme);
        this.setCanceledOnTouchOutside(false);
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
