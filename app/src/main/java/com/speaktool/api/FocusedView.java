package com.speaktool.api;

/**
 * 聚焦 视图
 * 
 * @author shaoshuai
 * 
 */
public interface FocusedView {

	void intoFocus();

	void exitFocus();

	boolean isInFocus();

	void setIsInFocus(boolean isfocus);

}
