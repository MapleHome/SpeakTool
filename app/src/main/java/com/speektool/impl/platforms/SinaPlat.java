package com.speektool.impl.platforms;

import android.content.Context;
import android.text.TextUtils;

import com.speektool.SpeekToolApp;
import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.bean.UserBean;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.ui.dialogs.FillShareInfoDialog;

import java.util.HashMap;

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
        //
        if (mThirdPartyRunState != null) {
            mThirdPartyRunState.onStartRun();
        }

        if (mThirdPartyRunState != null) {
            mThirdPartyRunState.onFinishRun();
        }


    }

    private void sinaLoginComplete(HashMap<String, Object> retmap) {
        Object platformUserName = retmap.get("name");
        Object description = retmap.get("description");
        final Object profile_image_url = retmap.get("profile_image_url");
        // GLogger.e(tag, platformUserName);

        final UserBean userBean = new UserBean();
        userBean.setType(UserBean.USER_TYPE_SINA);
        if (platformUserName != null) {
            userBean.setWidgetUserId(platformUserName.toString());
            userBean.setNickName(platformUserName.toString());
        }
        if (description != null) {
            userBean.setIntroduce(description.toString());
        }
        if (profile_image_url != null && !TextUtils.isEmpty(profile_image_url.toString())) {
            userBean.setPortraitPath(profile_image_url.toString());
        }

        SpeekToolApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                checkPlatformUserExist(userBean);
            }
        });
    }

    @Override
    protected void shareUploadedRecord() {
        FillShareInfoDialog dia = new FillShareInfoDialog(mContext, ShareLocation.SINA_WEIBO, mCourseItem,
                mThirdPartyRunState);
        dia.show();
    }

    @Override
    protected UploadRequestCode getUploadRequestCode() {
        return UploadRequestCode.SHARE_SINA_WEIBO;
    }
}
