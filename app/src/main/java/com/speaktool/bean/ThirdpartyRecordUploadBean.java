package com.speaktool.bean;

import java.io.Serializable;

/**
 * 第三方记录上传
 * 
 * @author shaoshuai
 * 
 */
public class ThirdpartyRecordUploadBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int COURSE_TYPE_VIDEO = 1;
	public static final int COURSE_TYPE_SCRIPT = 2;

	private String title;
	private String tab;
	private String type;
	private String introduce;
	private boolean isPublicPublish;
	private String thumbNailPath;
	//
	private String videoFilePath;
	private String zipFilePath;
	private long duration;
	private String courseId;
	private int courseType;
	private String thumbNailName;
	//
	private int makeWindowWidth;
	private int makeWindowHeight;
	//
	private String thirdpartyUseAccount;
	private String token;
	private String appSign;
	private String moduleId;
	//
	private String uploadUrl;

	//

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "ThirdpartyRecordUploadBean [title=" + title + ", tab=" + tab
				+ ", type=" + type + ", introduce=" + introduce
				+ ", isPublicPublish=" + isPublicPublish + ", thumbNailPath="
				+ thumbNailPath + ", videoFilePath=" + videoFilePath
				+ ", zipFilePath=" + zipFilePath + ", duration=" + duration
				+ ", courseId=" + courseId + ", courseType=" + courseType
				+ ", thumbNailName=" + thumbNailName + ", makeWindowWidth="
				+ makeWindowWidth + ", makeWindowHeight=" + makeWindowHeight
				+ ", thirdpartyUseAccount=" + thirdpartyUseAccount + ", token="
				+ token + ", appSign=" + appSign + ", moduleId=" + moduleId
				+ ", uploadUrl=" + uploadUrl + "]";
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

	public String getIntroduce() {
		return introduce;
	}

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

	public String getThirdpartyUseAccount() {
		return thirdpartyUseAccount;
	}

	public void setThirdpartyUseAccount(String thirdpartyUseAccount) {
		this.thirdpartyUseAccount = thirdpartyUseAccount;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAppSign() {
		return appSign;
	}

	public void setAppSign(String appSign) {
		this.appSign = appSign;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

}
