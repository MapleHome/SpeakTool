package com.speaktool.impl.modes;

import android.view.MotionEvent;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.FocusedView;
import com.speaktool.api.Page;
import com.speaktool.busevents.CloseEditPopupWindowEvent;

import org.greenrobot.eventbus.EventBus;


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
	public boolean touchDraw(MotionEvent event, BaseDraw draw) {
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
