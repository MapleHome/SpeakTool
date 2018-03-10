package com.speektool.api;

import android.view.MotionEvent;

import com.speektool.impl.modes.DrawModeCode;

/**
 * 绘制模式
 * 
 * @author shaoshuai
 * 
 */
public interface DrawMode {

	boolean touchDraw(MotionEvent event, Draw draw);

	/** 获取模式代码 */
	DrawModeCode getModeCode();
}
