package com.speektool.tasks;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.http.UniversalHttp;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.bean.SearchCategoryBean;
import com.speektool.bean.UserBean;
import com.speektool.dao.RecordCategoriesDatabase;
import com.speektool.dao.UserDatabase;
import com.speektool.utils.JsonUtil;

public class TaskLoadRecordCategories extends BaseRunnable<Integer, Void> {

	public static interface RecordTypeLoadListener {

		void onRecordTypeLoaded(List<SearchCategoryBean> result);
	}

	private static final String tag = TaskLoadRecordCategories.class.getSimpleName();
	private final WeakReference<RecordTypeLoadListener> mListener;
	private boolean isNeedAllType;

	public TaskLoadRecordCategories(RecordTypeLoadListener listener, boolean isNeedAllType) {

		mListener = new WeakReference<RecordTypeLoadListener>(listener);
		this.isNeedAllType = isNeedAllType;
	}

	@Override
	public void onPostExecute(Void result) {

		super.onPostExecute(result);
	}

	@Override
	public Void doBackground() {

		final List<SearchCategoryBean> recs = RecordCategoriesDatabase.getLocalCategories(SpeekToolApp.app(),
				isNeedAllType);
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				RecordTypeLoadListener listener = mListener.get();
				if (null != listener) {

					listener.onRecordTypeLoaded(recs);
				}

			}
		});

		final UserBean session = UserDatabase.getUserLocalSession(SpeekToolApp.app());
		if (session == null || session.getLoginState() == UserBean.STATE_OUT) {
			return null;

		}

		Map<String, String> params = Maps.newHashMap();
		params.put("uid", session.getId());
		String result = UniversalHttp.post(Const.COURSE_TYPES_URL, params);
		if (TextUtils.isEmpty(result)) {

			return null;

		}
		try {
			JSONObject response = new JSONObject(result);
			int resultcode = response.getInt("result");
			if (resultcode == 0) {
				// save session.
				JSONArray returnData = response.getJSONArray("returnData");
				// GLogger.e("lich", "search returnData:" +
				// returnData.toString());
				Type collectionType2 = new TypeToken<List<SearchCategoryBean>>() {
				}.getType();
				final List<SearchCategoryBean> serverTypeist = JsonUtil.fromJonGeneric(returnData.toString(),
						collectionType2);
				if (serverTypeist == null || serverTypeist.isEmpty()) {

					return null;
				}

				if (recs == null || recs.isEmpty()) {
					uiHandler.post(new Runnable() {

						@Override
						public void run() {
							RecordTypeLoadListener listener = mListener.get();
							if (null != listener) {

								listener.onRecordTypeLoaded(serverTypeist);
							}
						}
					});
					return null;
				}
				for (SearchCategoryBean serverType : serverTypeist) {
					if (!recs.contains(serverType))
						recs.add(serverType);
				}
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						RecordTypeLoadListener listener = mListener.get();
						if (null != listener) {
							listener.onRecordTypeLoaded(recs);
						}
					}
				});
			} else {
				// ignore.
			}
		} catch (JSONException e) {
			e.printStackTrace();

		}

		return null;

	}

}
