package com.speektool.manager;

import android.view.MotionEvent;

import com.speektool.api.Draw;
import com.speektool.api.DrawMode;
import com.speektool.busevents.DrawModeChangedEvent;
import com.speektool.impl.modes.DrawModeChoice;
import com.speektool.impl.modes.DrawModeCode;

import de.greenrobot.event.EventBus;

/**
 * 绘画状态管理者
 * 
 * @author shaoshuai
 * 
 */
public class DrawModeManager {

	private DrawMode mode = new DrawModeChoice();

	private static DrawModeManager sIns = new DrawModeManager();

	private DrawModeManager() {
		super();
	}

	/** 饿汉式-单利模式 */
	public static DrawModeManager getIns() {
		return sIns;
	}

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
