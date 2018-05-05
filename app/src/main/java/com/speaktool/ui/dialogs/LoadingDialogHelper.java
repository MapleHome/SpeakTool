package com.speaktool.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;

public class LoadingDialogHelper {

    private Context mContext;
    private Dialog mLoadingDialog;

    public LoadingDialogHelper(Context context) {
        super();
        this.mContext = context;
    }

    public Dialog showLoading(String msg, OnKeyListener onKeyListener) {
        return showLoading(msg, onKeyListener, false);
    }

    public Dialog showLoading(String msg, OnKeyListener onKeyListener, boolean cancelable) {
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
            return mLoadingDialog;
        }
        mLoadingDialog = ProgressDialogOffer.offerDialogAsActivity(mContext, msg);
        mLoadingDialog.setOnKeyListener(onKeyListener);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.show();
        return mLoadingDialog;
    }

    public void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public Dialog showLoading() {
        return showLoading("正在加载", null);
    }

    public Dialog showLoading(boolean cancelable) {
        return showLoading("正在加载", null, cancelable);
    }

}
