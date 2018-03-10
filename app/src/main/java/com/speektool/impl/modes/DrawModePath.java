package com.speektool.impl.modes;

import java.util.List;

import android.graphics.Path;
import android.view.MotionEvent;

import com.google.common.collect.Lists;
import com.speektool.api.Draw;
import com.speektool.api.DrawMode;
import com.speektool.api.FocusedView;
import com.speektool.api.Page;
import com.speektool.bean.CreatePenData;
import com.speektool.bean.MoveData;
import com.speektool.bean.PositionData;
import com.speektool.busevents.CloseEditPopupWindowEvent;
import com.speektool.impl.cmd.create.CmdCreatePen;
import com.speektool.impl.shapes.Path_;
import com.speektool.impl.shapes.Point_;
import com.speektool.paint.DrawPaint;
import com.speektool.paint.EraserPaint;

import de.greenrobot.event.EventBus;

/**
 * 用于绘制路径或点。
 * 
 * @author lchli
 * 
 */
public class DrawModePath implements DrawMode {

	private static final String tag = DrawModePath.class.getSimpleName();
	private int mX;
	private int mY;

	public DrawModePath() {
		super();
	}

	private List<MoveData> points;
	private boolean isEraser = getModeCode() == DrawModeCode.ERASER;
	private CmdCreatePen cmd;
	private int downx, downy;
	private int color;
	private int strokeWidth;
	private Path_ mPath_;

	@Override
	public boolean touchDraw(MotionEvent event, Draw draw) {
		final Page drawBoard = draw.getCurrentBoard();

		FocusedView focusView = drawBoard.getFocusedView();
		if (focusView != null) {
			focusView.exitFocus();
			EventBus.getDefault().post(new CloseEditPopupWindowEvent());
		}
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			color = isEraser ? EraserPaint.getGlobalPaintInfo().getColor() : DrawPaint.getGlobalPaintInfo().getColor();
			strokeWidth = isEraser ? EraserPaint.getGlobalPaintInfo().getStrokeWidth() : DrawPaint.getGlobalPaintInfo()
					.getStrokeWidth();

			Path path = new Path();
			path.moveTo(x, y);

			mPath_ = new Path_(path, drawBoard.makeShapeId(), color, strokeWidth, isEraser);
			if (!mPath_.isEraser()) {
				drawBoard.drawOnTemp(mPath_);

			} else {
				drawBoard.drawOnBuffer(mPath_);

			}
			drawBoard.refresh();
			//
			cmd = new CmdCreatePen();
			cmd.setTime(draw.getPageRecorder().recordTimeNow());

			points = Lists.newArrayList();
			points.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmd.getTime(), x, y));
			//
			downx = mX = x;
			downy = mY = y;

		}
			break;
		case MotionEvent.ACTION_MOVE: {

			//
			mPath_.getPath().quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			if (!mPath_.isEraser()) {
				drawBoard.drawOnTemp(mPath_);

			} else {
				drawBoard.drawOnBuffer(mPath_);

			}
			drawBoard.refresh();
			//
			points.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmd.getTime(), x, y));

			//
			mX = x;
			mY = y;
		}
			break;
		case MotionEvent.ACTION_UP:

			drawBoard.drawOnTemp(null);// must set to null.
			//
			boolean isMoved = x - downx != 0 || y - downy != 0;
			if (isMoved) {
				drawBoard.drawOnBuffer(mPath_);
				drawBoard.saveShape(mPath_);
			} else {
				Point_ point = new Point_(x, y, mPath_.getId(), color, strokeWidth, isEraser);
				drawBoard.drawOnBuffer(point);
				drawBoard.saveShape(point);
			}
			drawBoard.refresh();
			//
			points.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmd.getTime(), x, y));
			CreatePenData data = new CreatePenData();
			data.setMinXY(new PositionData(downx, downy));
			data.setMaxXY(new PositionData(x, y));
			data.setAlpha(1);
			data.setPoints(points);
			data.setType(isEraser ? "eraser" : "pen");
			data.setShapeID(mPath_.getId());
			String colorhex = "#" + Integer.toHexString(color).substring(2);
			data.setStrokeColor(colorhex);
			data.setStrokeWidth(strokeWidth);

			//
			cmd.setData(data);
			cmd.setEndTime(draw.getPageRecorder().recordTimeNow());

			drawBoard.sendCommand(cmd, false);
			// end.
			points = null;
			cmd = null;

			break;
		}

		return true;
	}

	@Override
	public DrawModeCode getModeCode() {
		return DrawModeCode.PATH;
	}

}