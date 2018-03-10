package com.speektool.api;

/**
 * 课程记录
 * 
 * @author shaoshuai
 * 
 */
public interface CourseItem {
	/** 获取课程记录标题 */
	String getRecordTitle();

	/** 视频记录缩略图路径 */
	String getThumbnailImgPath();

	/** 课程记录总时间 */
	long getDuration();

	/** 获取课程记录创建时间 */
	long getCreateTime();

	/** 获取课程记录分享地址 */
	String getShareUrl();

	/** 设置课程记录分享地址 */
	void setShareUrl(String url);

	/** 获取课程记录ID */
	String getCourseId();

	/** 设置课程记录ID */
	void setCourseId(String courseId);

	/** 获取课程记录介绍 */
	String getIntroduce();

	/** 是否正在上传 */
	boolean isUploading();

	/** 设置是否正在上传 */
	void setUploading(boolean b);

}
