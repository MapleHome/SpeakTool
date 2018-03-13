package com.speaktool.busevents;

import com.speaktool.bean.ThirdpartyRecordUploadBean;

public class RequestUploadCourseToThirdPartyEvent {

	private ThirdpartyRecordUploadBean uploadBean;

	private ThirdpartyUploadCourseCallback callback;

	public static interface ThirdpartyUploadCourseCallback {

		void onUploadSuccess();
	}

	public ThirdpartyUploadCourseCallback getCallback() {
		return callback;
	}

	public void setCallback(ThirdpartyUploadCourseCallback callback) {
		this.callback = callback;
	}

	public ThirdpartyRecordUploadBean getUploadBean() {
		return uploadBean;
	}

	public void setUploadBean(ThirdpartyRecordUploadBean uploadBean) {
		this.uploadBean = uploadBean;
	}

}
