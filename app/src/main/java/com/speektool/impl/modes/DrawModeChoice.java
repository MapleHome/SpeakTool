package com.speektool.impl.modes;

import android.view.MotionEvent;

import com.speektool.api.Draw;
import com.speektool.api.DrawMode;
import com.speektool.api.FocusedView;
import com.speektool.api.Page;
import com.speektool.busevents.CloseEditPopupWindowEvent;

import de.greenrobot.event.EventBus;

/**
 * 绘画模式——选择
 * 
 * @author shaoshuai
 * 
 */
public class DrawModeChoice implements DrawMode {
	/** 绘制模式-手 */
	public DrawModeChoice() {
		super();
	}

	@Override
	public boolean touchDraw(MotionEvent event, Draw draw) {
		final Page drawBoard = draw.getCurrentBoard();
		FocusedView focusView = drawBoard.getFocusedView();
		if (focusView != null) {
			focusView.exitFocus();
			// 通过EventBus订阅者发送消息
			EventBus.getDefault().post(new CloseEditPopupWindowEvent());
		}
		return false;
	}

	@Override
	public DrawModeCode getModeCode() {
		return DrawModeCode.CHOICE;
	}
}
