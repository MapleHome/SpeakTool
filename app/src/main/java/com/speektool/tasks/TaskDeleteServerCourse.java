package com.speektool.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.api.CourseItem;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.UserBean;
import com.speektool.dao.UserDatabase;

/**
 * 删除服务端记录
 * 
 * @author shaoshuai
 * 
 */
public class TaskDeleteServerCourse extends BaseRunnable<Integer, Void> {

	public static interface DeleteServerCourseCallback {

		void onDeleteSuccess();

		void onDeleteFail();

		void onConnectFail();
	}

	private static final String tag = TaskDeleteServerCourse.class.getSimpleName();

	private final WeakReference<DeleteServerCourseCallback> mListener;

	private CourseItem course;

	private boolean isNeedDeleteLocalRecord;

	public TaskDeleteServerCourse(DeleteServerCourseCallback listener, CourseItem course,
			boolean isNeedDeleteLocalRecord) {

		mListener = new WeakReference<DeleteServerCourseCallback>(listener);
		this.course = course;
		this.isNeedDeleteLocalRecord = isNeedDeleteLocalRecord;

	}

	@Override
	public Void doBackground() {
		Map<String, String> params = Maps.newHashMap();

		UserBean userBean = UserDatabase.getUserLocalSession(SpeekToolApp.app());
		params.put("uid", userBean.getId());
		params.put("password", userBean.getPassword());
		params.put("courseId", course.getCourseId());
		String result =
//				UniversalHttp.post(Const.COURSE_DELETE_URL, params);
		null;
		if (TextUtils.isEmpty(result)) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					DeleteServerCourseCallback listener = mListener.get();
					if (null != listener) {
						listener.onConnectFail();
					}

				}
			});
			return null;
		}
		try {
			JSONObject response = new JSONObject(result);
			int resultCode = response.getInt("result");
			if (resultCode == 0) {// have permission.
				if (isNeedDeleteLocalRecord) {
					deleteLoacalRecord((LocalRecordBean) course);
				}
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						DeleteServerCourseCallback listener = mListener.get();
						if (null != listener) {
							listener.onDeleteSuccess();
						}
					}
				});

			} else {
				if (isNeedDeleteLocalRecord) {
					deleteLoacalRecord((LocalRecordBean) course);
				}
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						DeleteServerCourseCallback listener = mListener.get();
						if (null != listener) {
							listener.onDeleteFail();
						}

					}
				});

			}

		} catch (Exception e) {
			e.printStackTrace();
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					DeleteServerCourseCallback listener = mListener.get();
					if (null != listener) {
						listener.onConnectFail();
					}

				}
			});
		}

		return null;
	}

	private void deleteLoacalRecord(LocalRecordBean localRecord) {
		String dirpath = localRecord.getRecordDir();
		File dir = new File(dirpath);
		if (dir == null || !dir.exists())
			return;

		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files)
				f.delete();

		}

		//
		dir.delete();

	}

}
