package com.speektool.impl.platforms;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.speektool.SpeekToolApp;
import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.bean.UserBean;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.ui.dialogs.QQShareChoiceDialog;

import java.util.HashMap;

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

        if (mThirdPartyRunState != null) {
            mThirdPartyRunState.onStartRun();
        }

        if (mThirdPartyRunState != null) {
            mThirdPartyRunState.onFinishRun();
        }

    }

    /**
     * QQ登录完成
     *
     * @param retmap
     */
    private void qqLoginComplete(HashMap<String, Object> retmap) {
        Object platformUserName = retmap.get("nickname");
        final Object figureurl_qq_1 = retmap.get("figureurl_qq_1");
        Object msg = retmap.get("msg");

        final UserBean userBean = new UserBean();
        userBean.setType(UserBean.USER_TYPE_QQ);// 类型
        if (platformUserName != null) {
            Log.e("QQ登陆：设置WidgetUserID", "" + platformUserName.toString());
            userBean.setWidgetUserId(platformUserName.toString());//
            userBean.setNickName(platformUserName.toString());// 昵称
        }
        if (msg != null) {
            userBean.setIntroduce(msg.toString());// 自我介绍
        }
        //
        if (figureurl_qq_1 != null && !TextUtils.isEmpty(figureurl_qq_1.toString())) {
            userBean.setPortraitPath(figureurl_qq_1.toString());// 头像
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
        QQShareChoiceDialog qqDia = new QQShareChoiceDialog(mContext);
        qqDia.show();
    }

    @Override
    protected UploadRequestCode getUploadRequestCode() {
        return UploadRequestCode.SHARE_QQ;
    }

}
