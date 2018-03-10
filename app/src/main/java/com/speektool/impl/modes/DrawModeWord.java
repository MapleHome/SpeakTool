package com.speektool.impl.modes;

import android.view.MotionEvent;
import android.widget.AbsoluteLayout;

import com.speektool.api.Draw;
import com.speektool.api.DrawMode;
import com.speektool.api.Page;
import com.speektool.busevents.CloseEditPopupWindowEvent;
import com.speektool.manager.DrawModeManager;
import com.speektool.ui.layouts.WordEdit;

import de.greenrobot.event.EventBus;

/**
 * 绘画模式——文字
 * 
 * @author shaoshuai
 * 
 */
@SuppressWarnings("deprecation")
public class DrawModeWord implements DrawMode {

	public DrawModeWord() {
		super();
	}

	@Override
	public boolean touchDraw(MotionEvent event, Draw draw) {
		final Page drawBoard = draw.getCurrentBoard();
		if (drawBoard.getFocusedView() != null) {
			drawBoard.getFocusedView().exitFocus();
			// 通过EventBus订阅者发送消息
			EventBus.getDefault().post(new CloseEditPopupWindowEvent());
		}
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			WordEdit edit = new WordEdit(draw.context(), draw,
					drawBoard.makeShapeId());
			AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
					AbsoluteLayout.LayoutParams.WRAP_CONTENT,
					AbsoluteLayout.LayoutParams.WRAP_CONTENT, (int) x, (int) y);
			edit.setLayoutParams(lp);
			drawBoard.draw(edit);
			drawBoard.saveShape(edit);
			//
			edit.intoEdit(true);
			DrawModeManager.getIns().setDrawMode(new DrawModeChoice());
			break;
		}
		return true;
	}

	@Override
	public DrawModeCode getModeCode() {
		return DrawModeCode.WORD;
	}

}
