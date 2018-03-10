package com.speektool.impl.platforms;

import android.content.Context;

import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.ui.dialogs.FillShareInfoDialog;

/**
 * 新浪平台
 *
 * @author shaoshuai
 */
public class SinaPlat extends BasePaltform {

    public SinaPlat(Context mContext, ThirdPartyRunState mThirdPartyRunState) {
        super(mContext, mThirdPartyRunState);
    }

    @Override
    public void login(LoginCallback mLoginCallback) {
        this.mLoginCallback = mLoginCallback;

    }


    @Override
    protected void shareUploadedRecord() {
        FillShareInfoDialog dia = new FillShareInfoDialog(mContext, ShareLocation.SINA_WEIBO, mCourseItem,
                mThirdPartyRunState);
        dia.show();
    }


}
