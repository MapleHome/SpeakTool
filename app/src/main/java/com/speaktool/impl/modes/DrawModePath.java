package com.speaktool.impl.modes;

import android.graphics.Path;
import android.view.MotionEvent;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.FocusedView;
import com.speaktool.impl.api.Page;
import com.speaktool.impl.bean.CreatePenData;
import com.speaktool.impl.bean.MoveData;
import com.speaktool.impl.bean.PositionData;
import com.speaktool.busevents.CloseEditPopupWindowEvent;
import com.speaktool.impl.cmd.create.CmdCreatePen;
import com.speaktool.impl.paint.DrawPaint;
import com.speaktool.impl.paint.EraserPaint;
import com.speaktool.impl.shapes.Path_;
import com.speaktool.impl.shapes.Point_;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 用于绘制路径或点。
 *
 * @author lchli
 */
public class DrawModePath implements DrawMode {
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
    public boolean touchDraw(MotionEvent event, BaseDraw draw) {
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

                points = new ArrayList<>();
                points.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmd.getTime(), x, y));
                //
                downx = mX = x;
                downy = mY = y;

            }
            break;
            case MotionEvent.ACTION_MOVE: {
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
