package com.speektool.tasks;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.collect.Maps;
import com.http.UniversalHttp;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.bean.UserBean;
import com.speektool.dao.UserDatabase;

/**
 * 用户登陆
 * 
 * @author shaoshuai
 * 
 */
public class TaskUserLogin extends BaseRunnable<Integer, Void> {
	/** 用户登陆回调 */
	public static interface UserLoginCallback {
		/** 连接失败 */
		void onConnectFail();

		/** 登陆失败 */
		void onLoginFail();

		/** 登陆成功 */
		void onLoginSuccess();
	}

	private final WeakReference<UserLoginCallback> mListener;
	private UserBean userBean;

	public TaskUserLogin(UserLoginCallback listener, UserBean userBean) {
		mListener = new WeakReference<UserLoginCallback>(listener);
		this.userBean = userBean;
	}

	@Override
	public void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	@Override
	public Void doBackground() {

		Map<String, String> params = Maps.newHashMap();
		params.put("account", userBean.getAccount());
		params.put("password", userBean.getPassword());
		// http://www.speaktool.com/api/userLogin.do
		// ======================用户登陆完成返回信息===================================
		// {
		// 		"result": 0,
		// 		"returnData": {
		// 			"email": "939078792@qq.com",
		// 			"intro": "更改赫兹日龙",
		// 			"photoURL": "userPhoto/7b2d5a11803b4363977bf8923dbd36a6.jpg",
		// 			"realName": "小可爱",
		// 			"token": "07b93b37323a48279aa67c1a48ad6780",
		// 			"uid": "7b2d5a11803b4363977bf8923dbd36a6"
		// 		},
		// 		"version": "1.0"
		// }
		String result = UniversalHttp.post(Const.USER_LOGIN_URL, params);
		if (TextUtils.isEmpty(result)) {// 结果为空
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					UserLoginCallback listener = mListener.get();
					if (null != listener) {
						listener.onConnectFail();// 连接失败
					}
				}
			});
			return null;
		}
		try {
			Log.e("登陆任务", "返回信息：" + result);
			JSONObject response = new JSONObject(result);
			int resultcode = response.getInt("result");
			if (resultcode == 0) {
				// save session.
				JSONObject returnData = response.getJSONObject("returnData");
				String uid = returnData.getString("uid");
				userBean.setId(uid);
				String nick = returnData.getString("realName");
				userBean.setNickName(nick);
				String portraitUrl = returnData.getString("photoURL");
				portraitUrl = Const.SPEEKTOOL_SERVER__URL + portraitUrl;
				userBean.setPortraitPath(portraitUrl);
				String introduce = returnData.getString("intro");
				userBean.setIntroduce(introduce);
				String email = returnData.getString("email");
				userBean.setEmail(email);
				//
				UserDatabase.saveUserLocalSession(userBean, SpeekToolApp.app());
				// 登陆成功
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						UserLoginCallback listener = mListener.get();
						if (null != listener) {
							listener.onLoginSuccess();
						}
					}
				});
			} else {//
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						UserLoginCallback listener = mListener.get();
						if (null != listener) {
							listener.onLoginFail();// 登录失败
						}
					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					UserLoginCallback listener = mListener.get();
					if (null != listener) {
						listener.onLoginFail();// 登陆失败
					}
				}
			});
		}
		return null;
	}
}
