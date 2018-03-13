package com.speaktool.impl.platforms;

import android.content.Context;

import com.speaktool.api.LoginCallback;
import com.speaktool.api.ThirdPartyRunState;
import com.speaktool.ui.dialogs.FillShareInfoDialog;

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
