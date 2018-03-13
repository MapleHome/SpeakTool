package com.speaktool.impl.platforms;

import android.content.Context;

import com.speaktool.api.LoginCallback;
import com.speaktool.api.ThirdPartyRunState;
import com.speaktool.ui.dialogs.FillShareInfoDialog;

/**
 * 腾许微博平台
 *
 * @author shaoshuai
 */
public class TencentWeiboPlat extends BasePaltform {

    public TencentWeiboPlat(Context mContext, ThirdPartyRunState mThirdPartyRunState) {
        super(mContext, mThirdPartyRunState);
    }

    @Override
    public void login(LoginCallback loginCallback) {
        this.mLoginCallback = loginCallback;

    }


    @Override
    protected void shareUploadedRecord() {
        FillShareInfoDialog dia = new FillShareInfoDialog(mContext, ShareLocation.TENCENT_WEIBO, mCourseItem,
                mThirdPartyRunState);
        dia.show();
    }


}
