package com.speaktool.impl.modes;

import android.graphics.Path;

import com.speaktool.impl.api.Page;
import com.speaktool.impl.bean.CreatePenData;
import com.speaktool.impl.bean.MoveData;
import com.speaktool.impl.bean.PositionData;
import com.speaktool.impl.cmd.create.CmdCreatePen;
import com.speaktool.impl.shapes.Path_;
import com.speaktool.impl.shapes.Point_;
import com.speaktool.ui.draw.DrawActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lchli on 2015/11/22.
 */
public class StrokeHandler {

    private DrawActivity draw;
    private int mX;
    private int mY;
    private List<MoveData> points;
    private boolean isEraser = false;
    private CmdCreatePen cmd;
    private int downx, downy;
    private int color;
    private int strokeWidth;
    private Path_ mPath_;
    private boolean isFirstPoint = true;

    public StrokeHandler(DrawActivity draw) {
        this.draw = draw;
    }

    public void down(int color, int strokeWidth, boolean isEraser) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.isEraser = isEraser;
        points = new ArrayList<>();
        cmd = new CmdCreatePen();
        Path path = new Path();
        Page drawBoard = draw.getCurrentBoard();
        mPath_ = new Path_(path, drawBoard.makeShapeId(), color, strokeWidth, isEraser);
        isFirstPoint = true;
        if (!mPath_.isEraser()) {
            drawBoard.drawOnTemp(mPath_);
        } else {
            drawBoard.drawOnBuffer(mPath_);
        }
        drawBoard.refresh();
    }

    public void move(int x, int y) {
        if (mPath_ == null) {
            return;
        }
        if (isFirstPoint) {
            downx = x;
            downy = y;
            isFirstPoint = false;
            mPath_.getPath().moveTo(x, y);
            cmd.setTime(draw.getPageRecorder().recordTimeNow());
        } else {
            mPath_.getPath().quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        }
        final Page drawBoard = draw.getCurrentBoard();
        if (!mPath_.isEraser()) {
            drawBoard.drawOnTemp(mPath_);
        } else {
            drawBoard.drawOnBuffer(mPath_);
        }
        drawBoard.refresh();
        points.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmd.getTime(), x, y));
        mX = x;
        mY = y;

    }

    public void up() {
        isFirstPoint = true;
        if (mPath_ == null) {
            return;
        }
        int x = mX;
        int y = mY;
        final Page drawBoard = draw.getCurrentBoard();
        drawBoard.drawOnTemp(null);// must set to null.
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
        cmd.setData(data);
        cmd.setEndTime(draw.getPageRecorder().recordTimeNow());
        drawBoard.sendCommand(cmd, false);
        points = null;
        cmd = null;
        mPath_ = null;
    }

}
