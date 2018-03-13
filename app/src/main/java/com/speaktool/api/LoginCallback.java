package com.speaktool.api;

/** 登陆回调 */
public interface LoginCallback {
	/** 成功 */
	public static final int SUCCESS = 1;
	/** 失败 */
	public static final int FAIL = 2;
	/** 取消 */
	public static final int CANCEL = 3;

	void onLoginFinish(int resultCode);
}