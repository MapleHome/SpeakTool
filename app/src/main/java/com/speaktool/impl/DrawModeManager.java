package com.speaktool.impl;

import android.view.MotionEvent;

import com.speaktool.api.Draw;
import com.speaktool.api.DrawMode;
import com.speaktool.busevents.DrawModeChangedEvent;
import com.speaktool.impl.modes.DrawModeChoice;
import com.speaktool.impl.modes.DrawModeCode;

import org.greenrobot.eventbus.EventBus;


/**
 * 绘画状态管理者
 * 
 * @author shaoshuai
 * 
 */
public class DrawModeManager {
	private static DrawModeManager sIns = new DrawModeManager();

	private DrawModeManager() {
		super();
	}

	/** 饿汉式-单利模式 */
	public static DrawModeManager getIns() {
		return sIns;
	}



	private DrawMode mode = new DrawModeChoice();

	/** 获取绘制模式 */
	public DrawMode getDrawMode() {
		return mode;
	}

	/** 获取模式代码 */
	public DrawModeCode getModeCode() {
		return mode.getModeCode();
	}

	/** 设置绘画模式 */
	public void setDrawMode(DrawMode mode) {
		if (mode == this.mode)
			return;
		// 通过EventBus订阅者发送消息
		EventBus.getDefault().post(new DrawModeChangedEvent(this.mode.getModeCode(), mode.getModeCode()));
		this.mode = mode;
	}

	public boolean doTouchEvent(MotionEvent event, Draw draw) {
		return mode.touchDraw(event, draw);
	}

}
