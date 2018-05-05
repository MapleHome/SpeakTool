package com.speaktool.ui.dialogs.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.speaktool.R;
import com.speaktool.utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * QQ分享选择对话框
 *
 * @author Maple Shao
 */
public class QQShareDialog extends Dialog {
    @BindView(R.id.btnShareToQZone) Button btnShareToQZone;// 分享到QQ空间
    @BindView(R.id.btnShareToQQ) Button btnShareToQQ;// 分享到QQ好友
    @BindView(R.id.btnCancelExit) Button btnCancelExit;// 取消

    public QQShareDialog(Context context) {
        this(context, R.style.dialogTheme);
    }

    public QQShareDialog(Context context, int theme) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qq_sharechoice);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnShareToQZone)
    public void clickShareToQZone() {
        T.showShort(getContext(), "分享到QQ空间");
        dismiss();
    }

    @OnClick(R.id.btnShareToQQ)
    void clickShareToQQ() {
        T.showShort(getContext(), "分享到QQ");
        dismiss();
    }

    @OnClick(R.id.btnCancelExit)
    void clickCancelBtn() {
        dismiss();
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

}
