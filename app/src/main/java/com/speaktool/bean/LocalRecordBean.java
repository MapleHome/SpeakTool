package com.speaktool.bean;

import java.io.Serializable;

import com.speaktool.api.CourseItem;

/**
 * 本地记录
 * 
 * @author shaoshuai
 * 
 */
public class LocalRecordBean implements Serializable, CourseItem {
	private static final long serialVersionUID = 3846767521452450072L;

	private String recordTitle;
	private String recordDir;
	private long createTime;

	private long duration;

	private String tab;
	private String type;
	private String introduce;

	private String url;

	private String courseId;
	//
	private int makeWindowWidth;
	private int makeWindowHeight;
	// just use upload.
	private int progress;
	private String thumbnailImgPath;

	public String getThumbnailImgPath() {
		return thumbnailImgPath;
	}

	public void setThumbnailImgPath(String thumbnailImgPath) {
		this.thumbnailImgPath = thumbnailImgPath;
	}

	public String getRecordTitle() {
		return recordTitle;
	}

	public void setRecordTitle(String recordTitle) {
		this.recordTitle = recordTitle;
	}

	public String getRecordDir() {
		return recordDir;
	}

	public void setRecordDir(String recordDir) {
		this.recordDir = recordDir;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
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
		return url;
	}

	@Override
	public void setShareUrl(String url) {
		this.url = url;
	}

	@Override
	public String getCourseId() {
		return courseId;
	}

	@Override
	public void setCourseId(String courseId) {
		this.courseId = courseId;

	}

	public int getMakeWindowWidth() {
		return makeWindowWidth;
	}

	public void setMakeWindowWidth(int makeWindowWidth) {
		this.makeWindowWidth = makeWindowWidth;
	}

	public int getMakeWindowHeight() {
		return makeWindowHeight;
	}

	public void setMakeWindowHeight(int makeWindowHeight) {
		this.makeWindowHeight = makeWindowHeight;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

}
