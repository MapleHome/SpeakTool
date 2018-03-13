package com.speaktool.api;

import android.widget.EditText;

/**
 * 编辑转换器
 * 
 * @author shaoshuai
 * 
 */
public interface EditTransformer extends ViewTransformer {
	/**
	 * 改变颜色
	 * 
	 * @param edit
	 * @param newColor
	 */
	void changeColor(EditText edit, int newColor);
}
