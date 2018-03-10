package com.speektool.busevents;

import com.speektool.bean.RecordUploadBean;

public class RequestUploadCourseEvent {

	private RecordUploadBean recordUploadBean;

	private UploadCourseCallback callback;

	public static interface UploadCourseCallback {

		void onUploadSuccess();
	}

	public UploadCourseCallback getCallback() {
		return callback;
	}

	public void setCallback(UploadCourseCallback callback) {
		this.callback = callback;
	}

	public RecordUploadBean getRecordUploadBean() {
		return recordUploadBean;
	}

	public void setRecordUploadBean(RecordUploadBean recordUploadBean) {
		this.recordUploadBean = recordUploadBean;
	}

}
