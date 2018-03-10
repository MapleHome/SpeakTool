package com.speektool.api;

import android.view.View;

/**
 * 视图 转换器
 * 
 * @author shaoshuai
 * 
 */
public interface ViewTransformer {
	/**
	 * 旋转
	 * 
	 * @param degree 角度
	 * @param view 视图
	 */
	void rotateBy(float degree, View view);

	/**
	 * 旋转
	 * 
	 * @param degree
	 * @param view
	 */
	void rotateTo(float degree, View view);

	/**
	 * 缩放
	 * 
	 * @param factorX
	 * @param factorY
	 * @param view
	 */
	void scaleBy(float factorX, float factorY, View view);

	/**
	 * 缩放
	 * 
	 * @param factorX
	 * @param factorY
	 * @param view
	 */
	void scaleTo(float factorX, float factorY, View view);

	/**
	 * 移动
	 * 
	 * @param x
	 * @param y
	 * @param view
	 */
	void MoveTo(int x, int y, View view);

}
