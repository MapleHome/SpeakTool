package com.speaktool.impl;

import android.widget.EditText;

import com.speaktool.api.EditTransformer;

/**
 * 默认编辑转化器
 * 
 * @author shaoshuai
 * 
 */
public class DefEditTransformer extends DefViewTransformer implements
		EditTransformer {

	@Override
	public void changeColor(EditText edit, int newColor) {
		edit.setTextColor(newColor);
	}

}
