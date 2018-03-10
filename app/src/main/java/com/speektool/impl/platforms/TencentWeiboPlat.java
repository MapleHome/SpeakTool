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
        //
        if (mThirdPartyRunState != null) {
            mThirdPartyRunState.onStartRun();
        }


        if (mThirdPartyRunState != null) {
            mThirdPartyRunState.onFinishRun();
        }

    }

    /**
     * 腾讯微博登陆完成
     */
    private void tencentLoginComplete(HashMap<String, Object> retmap) {
        Object platformUserName = retmap.get("name");
        Object introduction = retmap.get("introduction");
        Object nick = retmap.get("nick");
        final Object head = retmap.get("head");

        final UserBean userBean = new UserBean();
        userBean.setType(UserBean.USER_TYPE_TENCENT);// 类型
        if (platformUserName != null)
            userBean.setWidgetUserId(platformUserName.toString());//
        if (introduction != null)
            userBean.setIntroduce(introduction.toString());// 自我介绍
        if (nick != null)
            userBean.setNickName(nick.toString());// 昵称
        if (head != null && !TextUtils.isEmpty(head.toString())) {
            userBean.setPortraitPath(head.toString() + "/50");// 头像路径
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
        FillShareInfoDialog dia = new FillShareInfoDialog(mContext, ShareLocation.TENCENT_WEIBO, mCourseItem,
                mThirdPartyRunState);
        dia.show();
    }

    @Override
    protected UploadRequestCode getUploadRequestCode() {
        return UploadRequestCode.SHARE_TENCENT_WEIBO;
    }

}
