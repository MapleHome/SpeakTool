package com.speektool.impl.platforms;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.speektool.SpeekToolApp;
import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.bean.UserBean;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.ui.dialogs.FillShareInfoDialog;

/**
 * 腾许微博平台
 * 
 * @author shaoshuai
 * 
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

		TencentWeibo lTencentWeibo = (TencentWeibo) ShareSDK.getPlatform(mContext.getApplicationContext(),
				TencentWeibo.NAME);
		lTencentWeibo.removeAccount();
		// 设置平台动作监听
		lTencentWeibo.setPlatformActionListener(new PlatformActionListener() {
			// 错误
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				if (mThirdPartyRunState != null) {
					mThirdPartyRunState.onFinishRun();
				}
			}

			// 完成
			@Override
			public void onComplete(Platform plat, int action, HashMap<String, Object> retmap) {
				if (mThirdPartyRunState != null) {
					mThirdPartyRunState.onFinishRun();
				}
				tencentLoginComplete(retmap);
			}

			// 取消
			@Override
			public void onCancel(Platform arg0, int arg1) {
				if (mThirdPartyRunState != null) {
					mThirdPartyRunState.onFinishRun();
				}
			}
		});

		lTencentWeibo.SSOSetting(true);
		lTencentWeibo.showUser(null);

	}

	/** 腾讯微博登陆完成 */
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
