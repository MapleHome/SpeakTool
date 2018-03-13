package com.speaktool.bean;

import com.speaktool.Const;
import com.speaktool.api.CourseItem;

public class ServerRecordBean implements CourseItem {
	private String courseId;
	private String courseName;
	private String categoryName;
	private String courseIntro;
	private String courseTag;
	private String photoURL;
	private String videoURL;
	private String zipURL;
	private String url;// share url.
	private String teacher;
	private float duration;// /!!!!!!!
	private int visitTimes;
	private String createDate;
	private String createUid;
	private boolean isUploading = false;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCourseIntro() {
		return courseIntro;
	}

	public void setCourseIntro(String courseIntro) {
		this.courseIntro = courseIntro;
	}

	public String getCourseTag() {
		return courseTag;
	}

	public void setCourseTag(String courseTag) {
		this.courseTag = courseTag;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public String getVideoURL() {
		return videoURL;
	}

	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

	public String getZipURL() {
		return zipURL;
	}

	public void setZipURL(String zipURL) {
		this.zipURL = zipURL;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public int getVisitTimes() {
		return visitTimes;
	}

	public void setVisitTimes(int visitTimes) {
		this.visitTimes = visitTimes;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateUid() {
		return createUid;
	}

	public void setCreateUid(String createUid) {
		this.createUid = createUid;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		return (long) duration;
	}

	@Override
	public String getRecordTitle() {
		return getCourseName();
	}

	@Override
	public String getThumbnailImgPath() {
		return Const.SPEEKTOOL_SERVER__URL + getPhotoURL();
	}

	@Override
	public long getCreateTime() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof CourseItem))
			return false;
		CourseItem input = (CourseItem) o;
		if (this.getRecordTitle() == null)
			return false;
		return this.getCourseId().equals(input.getCourseId());
	}

	@Override
	public String getShareUrl() {
		return getUrl();
	}

	@Override
	public void setShareUrl(String url) {
		setUrl(url);

	}

	@Override
	public String getIntroduce() {
		return getCourseIntro();
	}

	@Override
	public boolean isUploading() {
		return isUploading;
	}

	@Override
	public void setUploading(boolean b) {
		isUploading = b;
	}

}
