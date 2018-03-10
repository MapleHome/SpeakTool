package com.speektool.tasks;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.http.UniversalHttp;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.bean.UserBean;
import com.speektool.dao.UserDatabase;

public class TaskUserLogout extends BaseRunnable<Integer, Void> {

	public static interface UserLogoutCallback {
		void onConnectFail();

		void onResponseFail();

		void onSuccess();

	}

	private static final String tag = TaskUserLogout.class.getSimpleName();
	private final WeakReference<UserLogoutCallback> mListener;
	private UserBean userBean;

	public TaskUserLogout(UserLogoutCallback listener, UserBean user) {

		mListener = new WeakReference<UserLogoutCallback>(listener);
		this.userBean = user;

	}

	@Override
	public void onPostExecute(Void result) {

		super.onPostExecute(result);
	}

	@Override
	public Void doBackground() {

		Map<String, String> params = Maps.newHashMap();
		params.put("uid", userBean.getId());

		String result = UniversalHttp.post(Const.USER_LOGINOUT_URL, params);

		if (TextUtils.isEmpty(result)) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					UserLogoutCallback listener = mListener.get();
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
				UserDatabase.deleteUserLocalSession(userBean, SpeekToolApp.app());
				//
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						UserLogoutCallback listener = mListener.get();
						if (null != listener) {
							listener.onSuccess();
						}
					}
				});
			} else {//
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						UserLogoutCallback listener = mListener.get();
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
					UserLogoutCallback listener = mListener.get();
					if (null != listener) {
						listener.onResponseFail();
					}
				}
			});
		}
		return null;
	}
}
