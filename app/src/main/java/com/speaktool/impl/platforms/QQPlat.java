package com.speaktool.impl.platforms;

import android.content.Context;

import com.speaktool.api.LoginCallback;
import com.speaktool.api.ThirdPartyRunState;
import com.speaktool.ui.dialogs.QQShareChoiceDialog;

/**
 * QQ 平台
 *
 * @author Maple Shao
 */
public class QQPlat extends BasePaltform {

    public QQPlat(Context mContext, ThirdPartyRunState mThirdPartyRunState) {
        super(mContext, mThirdPartyRunState);
    }

    @Override
    public void login(LoginCallback mLoginCallback) {
        this.mLoginCallback = mLoginCallback;

    }


    @Override
    protected void shareUploadedRecord() {
        QQShareChoiceDialog qqDia = new QQShareChoiceDialog(mContext);
        qqDia.show();
    }

}
