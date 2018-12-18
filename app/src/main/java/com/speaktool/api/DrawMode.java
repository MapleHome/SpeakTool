package com.speaktool.api;

import android.view.MotionEvent;

import com.speaktool.impl.modes.DrawModeCode;

/**
 * 绘制模式
 * 
 * @author shaoshuai
 * 
 */
public interface DrawMode {

	boolean touchDraw(MotionEvent event, BaseDraw draw);

	/** 获取模式代码 */
	DrawModeCode getModeCode();
}
