package com.speektool.tasks;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.http.UniversalHttp;
import com.speektool.SpeekToolApp;
import com.speektool.bean.UserBean;
import com.speektool.manager.AppManager;

/**
 * 获取第三方用户信息
 * 
 * @author shaoshuai
 * 
 */
public class TaskGetThirdpartyUserInfo extends BaseRunnable<Integer, Void> {

	/** 获取第三方用户信息回调 */
	public static interface GetThirdpartyUserInfoCallback {
		/** 连接失败 */
		void onConnectFail();

		/** 应答失败 */
		void onResponseFail();

		/** 成功 */
		void onSuccess(UserBean thirdPartyUserBean);

	}

	private final WeakReference<GetThirdpartyUserInfoCallback> mListener;
	private UserBean thirdPartyUserBean;
	private String url;

	public TaskGetThirdpartyUserInfo(GetThirdpartyUserInfoCallback listener, UserBean thirdPartyUserBean, String url) {
		mListener = new WeakReference<GetThirdpartyUserInfoCallback>(listener);
		this.thirdPartyUserBean = thirdPartyUserBean;
		this.url = url;
	}

	@Override
	public void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	@Override
	public Void doBackground() {
		Map<String, String> params = Maps.newHashMap();
		params.put("account", thirdPartyUserBean.getWidgetUserId());
		params.put("token", thirdPartyUserBean.getToken());
		params.put("appSign", AppManager.getAppSignEncodedByMd5(SpeekToolApp.app()));
		String result = UniversalHttp.post(url, params);

		if (TextUtils.isEmpty(result)) {
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					GetThirdpartyUserInfoCallback listener = mListener.get();
					if (null != listener) {
						listener.onConnectFail();
					}
				}
			});
			return null;
		}
		try {
			JSONObject response = new JSONObject(result);
			int resultcode = response.getInt("result");
			if (resultcode == 0) {
				JSONObject returnData = response.getJSONObject("returnData");

				String nick = returnData.getString("nickName");// 昵称
				thirdPartyUserBean.setNickName(nick);

				String portraitUrl = returnData.getString("photoURL");// 头像
				thirdPartyUserBean.setPortraitPath(portraitUrl);

				String introduce = returnData.getString("intro");// 自我介绍
				thirdPartyUserBean.setIntroduce(introduce);

				String email = returnData.getString("email");// 邮箱
				thirdPartyUserBean.setEmail(email);
				//
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						GetThirdpartyUserInfoCallback listener = mListener.get();
						if (null != listener) {
							listener.onSuccess(thirdPartyUserBean);
						}
					}
				});
			} else {//
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						GetThirdpartyUserInfoCallback listener = mListener.get();
						if (null != listener) {
							listener.onResponseFail();
						}
					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					GetThirdpartyUserInfoCallback listener = mListener.get();
					if (null != listener) {
						listener.onResponseFail();
					}
				}
			});
		}
		return null;
	}
}
