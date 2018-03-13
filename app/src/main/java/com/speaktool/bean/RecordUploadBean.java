package com.speaktool.bean;

import java.io.Serializable;

/**
 * 记录上传
 * 
 * @author shaoshuai
 * 
 */
public class RecordUploadBean implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 视频课程类型 */
	public static final int COURSE_TYPE_VIDEO = 1;
	/** 脚本课程类型 */
	public static final int COURSE_TYPE_SCRIPT = 2;

	/** 标题 */
	private String title;
	/** 标签 */
	private String tab;
	/** 类型 */
	private String type;
	/** 简介 */
	private String introduce;
	/** 是否公开发布 */
	private boolean isPublicPublish;// TODO 弄反了。默认选中，缺返回false
	/** 缩略图路径 */
	private String thumbNailPath;// thumbNailPath=/storage/emulated/0/.spktl/records/14ebdd4361e/14ebdd4dec4.jpg,
	//
	/** 视频文集路径 */
	private String videoFilePath;// videoFilePath=null,
	/** 压缩文件路径 */
	private String zipFilePath; // zipFilePath=/storage/emulated/0/.spktl/records/14ebdd4361e/record.zip,
	/** 时长 */
	private long duration;// duration=5068

	/** 用户ID */
	private String uid;// uid=7b2d5a11803b4363977bf8923dbd36a6,
	/** 课程ID */
	private String courseId;// courseId=2bc32c3729404bd887306a89fc8a61e6,
	/** 课程类型 */
	private int courseType;// courseType=2,
	/** 缩略图名称 */
	private String thumbNailName;// thumbNailName=null,
	//
	/** 窗口宽度 */
	private int makeWindowWidth;// makeWindowWidth=1920,
	/** 窗口高度 */
	private int makeWindowHeight;// makeWindowHeight=750,
	//
	/** 课程目录 */
	private String recordDir;// recordDir=/storage/emulated/0/.spktl/records/14ebdd4361e,
	//
	/** 上传地址 */
	private String uploadUrl;// uploadUrl=http://www.speaktool.com/api/uploadCourse.do

	@Override
	public String toString() {
		return "RecordUploadBean [标题=" + title + ", 标签=" + tab + ", 类型=" + type + ", 简介=" + introduce + ", 是否公开发布="
				+ isPublicPublish + ", 缩略图=" + thumbNailPath + ", 视频文件=" + videoFilePath + ", 压缩文件=" + zipFilePath
				+ ", 时长=" + duration + ", uid=" + uid + ", courseId=" + courseId + ", 课程类型=" + courseType
				+ ", thumbNailName=" + thumbNailName + ", 宽度=" + makeWindowWidth + ", 高度=" + makeWindowHeight
				+ ", 课程目录=" + recordDir + ", uploadUrl=" + uploadUrl + "]";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	/** 获取课程简介 */
	public String getIntroduce() {
		return introduce;
	}

	/** 设置课程简介 */
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public boolean isPublicPublish() {
		return isPublicPublish;
	}

	public void setPublicPublish(boolean isPublicPublish) {
		this.isPublicPublish = isPublicPublish;
	}

	public String getThumbNailPath() {
		return thumbNailPath;
	}

	public void setThumbNailPath(String thumbNailPath) {
		this.thumbNailPath = thumbNailPath;
	}

	public String getVideoFilePath() {
		return videoFilePath;
	}

	public void setVideoFilePath(String videoFilePath) {
		this.videoFilePath = videoFilePath;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public int getCourseType() {
		return courseType;
	}

	public void setCourseType(int courseType) {
		this.courseType = courseType;
	}

	public String getThumbNailName() {
		return thumbNailName;
	}

	public void setThumbNailName(String thumbNailName) {
		this.thumbNailName = thumbNailName;
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

	public String getRecordDir() {
		return recordDir;
	}

	public void setRecordDir(String recordDir) {
		this.recordDir = recordDir;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

}
