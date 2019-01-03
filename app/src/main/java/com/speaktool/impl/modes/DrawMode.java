package com.speaktool.impl.modes;

import android.view.MotionEvent;

import com.speaktool.impl.api.BaseDraw;

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
