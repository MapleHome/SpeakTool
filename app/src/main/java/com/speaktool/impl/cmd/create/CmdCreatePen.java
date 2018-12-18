package com.speaktool.impl.cmd.create;

import android.graphics.Color;
import android.graphics.Path;
import android.os.SystemClock;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.impl.bean.CreatePenData;
import com.speaktool.impl.bean.DeleteShapeData;
import com.speaktool.impl.bean.MoveData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.delete.CmdDeletePen;
import com.speaktool.impl.shapes.Path_;
import com.speaktool.impl.shapes.Point_;

import java.util.List;

public class CmdCreatePen extends CmdCreateShape<CreatePenData> {

    private long endTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public void run(final BaseDraw draw, Page bw) {
        final CreatePenData data = getData();
        final int color = Color.parseColor(data.getStrokeColor());
        final int strokeWidth = data.getStrokeWidth();

        final boolean isEraser = data.getType().equals("eraser");
        final long startTime = getTime();

        final List<MoveData> points = data.getPoints();
        final int size = points.size();
        long preT = 0;
        final Path_ mPath_ = new Path_(new Path(), data.getShapeID(), color, strokeWidth, isEraser);
        MoveData preMv;
        for (int i = 0; i < size; i++) {
            final MoveData mv = points.get(i);
            final int j = i;

            final long deltaT = mv.getT() - preT;
            SystemClock.sleep(deltaT);
            preT = mv.getT();
            preMv = mv;
            final MoveData preMvCopy = preMv;
            //
            if (j == 0) {
                draw.postTaskToUiThread(new Runnable() {// DOWN.
                    @Override
                    public void run() {
                        mPath_.getPath().moveTo(mv.getX(), mv.getY());
                        if (!mPath_.isEraser()) {
                            draw.getCurrentBoard().drawOnTemp(mPath_);
                        } else {
                            draw.getCurrentBoard().drawOnBuffer(mPath_);
                        }
                        if (startTime != TIME_DELETE_FLAG)
                            draw.getCurrentBoard().refresh();
                    }
                });
            } else if (j == size - 1) {
                draw.postTaskToUiThread(new Runnable() {// UP.
                    @Override
                    public void run() {
                        draw.getCurrentBoard().drawOnTemp(null);// must set to
                        // null.
                        boolean isMoved = mv.getX() - points.get(0).getX() != 0 || mv.getY() - points.get(0).getY() != 0;
                        if (isMoved) {
                            draw.getCurrentBoard().drawOnBuffer(mPath_);
                            draw.getCurrentBoard().saveShape(mPath_);
                        } else {
                            Point_ point = new Point_(mv.getX(), mv.getY(), mPath_.getId(), color, strokeWidth, isEraser);
                            draw.getCurrentBoard().drawOnBuffer(point);
                            draw.getCurrentBoard().saveShape(point);
                        }
                        if (startTime != TIME_DELETE_FLAG)
                            draw.getCurrentBoard().refresh();
                    }
                });
            } else if (j > 0 && j < size - 1) {
                draw.postTaskToUiThread(new Runnable() {// move.
                    @Override
                    public void run() {
                        mPath_.getPath().quadTo(preMvCopy.getX(), preMvCopy.getY(),
                                (mv.getX() + preMvCopy.getX()) / 2,
                                (mv.getY() + preMvCopy.getY()) / 2);
                        if (!mPath_.isEraser()) {
                            draw.getCurrentBoard().drawOnTemp(mPath_);
                        } else {
                            draw.getCurrentBoard().drawOnBuffer(mPath_);
                        }
                        if (startTime != TIME_DELETE_FLAG)
                            draw.getCurrentBoard().refresh();
                    }
                });
            }
        }
    }

    @Override
    public ICmd inverse() {
        CmdDeletePen undoCmd = new CmdDeletePen();
        undoCmd.setData(new DeleteShapeData(getData().getShapeID()));
        return undoCmd;
    }

    @Override
    public ICmd copy() {
//        CmdCreatePen copy = new CmdCreatePen();
//        copy.setData(getData().copy());
//        copy.setEndTime(getEndTime());
//        copy.setTime(getTime());
        return this;
    }

}
