package com.speektool.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.speektool.Const;

public class TaskUploadException extends BaseRunnable<Integer, Void> {

	public static interface UploadExceptionCallback {
		void onConnectFail();

		void onUploadFail();

		void onUploadSuccess();

	}
	private static final String tag=TaskUploadException.class.getSimpleName();
	private final WeakReference<UploadExceptionCallback> mListener;
	private File exceptionZip;

	public TaskUploadException(UploadExceptionCallback listener,
			File exceptionZip) {

		mListener = new WeakReference<UploadExceptionCallback>(listener);
		this.exceptionZip = exceptionZip;
	}

	@Override
	public void onPostExecute(Void result) {

		super.onPostExecute(result);
	}

	@Override
	public Void doBackground() {

		Map<String, File> paramsFile = Maps.newHashMap();
		paramsFile.put("mobileExceptionZip", exceptionZip);
		String result =
//				UniversalHttp.post(Const.EXCEPTION_UPLOAD_URL, null, paramsFile);
		null;
		if (TextUtils.isEmpty(result)) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					UploadExceptionCallback listener = mListener.get();
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

				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						UploadExceptionCallback listener = mListener.get();
						if (null != listener) {
							listener.onUploadSuccess();
						}

					}
				});

			} else {//

				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						UploadExceptionCallback listener = mListener.get();
						if (null != listener) {
							listener.onUploadFail();
						}

					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					UploadExceptionCallback listener = mListener.get();
					if (null != listener) {
						listener.onUploadFail();
					}

				}
			});
		}

		return null;

	}

}
