package com.speektool.tasks;

import java.io.File;
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
 * 用户注册
 * 
 * @author shaoshuai
 * 
 */
public class TaskUserRegister extends BaseRunnable<Integer, Void> {
	private final WeakReference<UserRegisterCallback> mListener;
	private UserBean userBean;

	/**
	 * 用户注册接口回调
	 * 
	 * @author shaoshuai
	 * 
	 */
	public static interface UserRegisterCallback {
		/** 连接失败 */
		void onConnectFail();

		/** 注册失败 */
		void onRegisterFail();

		/** 注册成功 */
		void onRegisterSuccess();

		/** 用户已存在 */
		void onUserAlreadyExist();
	}

	public TaskUserRegister(UserRegisterCallback listener, UserBean userBean) {

		mListener = new WeakReference<UserRegisterCallback>(listener);
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
		params.put("email", userBean.getEmail());

		if (!TextUtils.isEmpty(userBean.getNickName())) {
			params.put("realName", userBean.getNickName());
		}
		if (!TextUtils.isEmpty(userBean.getIntroduce())) {
			params.put("intro", userBean.getIntroduce());
		}
		if (!TextUtils.isEmpty(userBean.getWidgetUserId())) {
			params.put("widgetsUserid", userBean.getWidgetUserId());
		}
		if (userBean.getType() != 0) {
			params.put("userType", userBean.getType() + "");
		}
		//
		Map<String, File> paramsFile = Maps.newHashMap();
		if (!TextUtils.isEmpty(userBean.getPortraitPath())) {
			File photo = new File(userBean.getPortraitPath());
			paramsFile.put("photo", photo);
		}
		String result = UniversalHttp.post(Const.USER_REGISTER_URL, params, paramsFile);
		if (TextUtils.isEmpty(result)) {
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					UserRegisterCallback listener = mListener.get();
					if (null != listener) {
						listener.onConnectFail();
					}
				}
			});
			return null;
		}
		try {
			Log.e("用户注册", "注册信息：" + result);
			
			JSONObject response = new JSONObject(result);
			int resultcode = response.getInt("result");
			if (resultcode == 0) {
				// save session.
				JSONObject returnData = response.getJSONObject("returnData");
				String uid = returnData.getString("uid");
				userBean.setId(uid);
				// save session.
				UserDatabase.saveUserLocalSession(userBean, SpeekToolApp.app());
				//
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						UserRegisterCallback listener = mListener.get();
						if (null != listener) {
							listener.onRegisterSuccess();
						}
					}
				});

			} else if (resultcode == 1) {//
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						UserRegisterCallback listener = mListener.get();
						if (null != listener) {
							listener.onUserAlreadyExist();
						}
					}
				});
			} else {
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						UserRegisterCallback listener = mListener.get();
						if (null != listener) {
							listener.onRegisterFail();
						}
					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					UserRegisterCallback listener = mListener.get();
					if (null != listener) {
						listener.onRegisterFail();
					}
				}
			});
		}
		return null;
	}
}
