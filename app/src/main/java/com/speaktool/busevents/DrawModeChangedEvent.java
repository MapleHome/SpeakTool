package com.speaktool.busevents;

import com.speaktool.impl.modes.DrawModeCode;

/**
 * 绘制模式改变事件
 * 
 * @author shaoshuai
 * 
 */
public class DrawModeChangedEvent {
	/** 前一个绘制模式 */
	private final DrawModeCode preMode;
	/** 当前绘制模式 */
	private final DrawModeCode nowMode;

	/**
	 * 绘制模式改变
	 * 
	 * @param preMode
	 *            -前一个绘制模式
	 * @param nowMode
	 *            -当前绘制模式
	 */
	public DrawModeChangedEvent(DrawModeCode preMode, DrawModeCode nowMode) {
		this.preMode = preMode;
		this.nowMode = nowMode;
	}

	/** 获取前一个绘制模式 */
	public DrawModeCode getPreMode() {
		return preMode;
	}

	/** 获取当前绘制模式 */
	public DrawModeCode getNowMode() {
		return nowMode;
	}

}
