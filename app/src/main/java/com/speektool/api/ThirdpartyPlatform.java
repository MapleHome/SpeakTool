package com.speektool.api;

/**
 * 第三方平台
 * 
 * @author shaoshuai
 * 
 */
public interface ThirdpartyPlatform {
	/**
	 * 登陆
	 * 
	 * @param mLoginCallback
	 */
	void login(LoginCallback mLoginCallback);

	/**
	 * 分享
	 * 
	 * @param mItemBean
	 */
	void share(CourseItem mItemBean);

}
