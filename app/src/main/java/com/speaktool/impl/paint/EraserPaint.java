package com.speaktool.impl.paint;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.speaktool.SpeakToolApp;
import com.speaktool.bean.PaintInfoBean;
import com.speaktool.utils.DensityUtils;
import com.speaktool.tasks.MyColors;

/**
 * 橡皮擦
 *
 * @author Maple Shao
 */
public class EraserPaint {

    private static final float DEFAULT_STROKE_WIDTH = 15;// dp
    private final Paint eraserPaint = new Paint();
    private static PaintInfoBean globalPaintInfo;

    static {
        globalPaintInfo = new PaintInfoBean();
        globalPaintInfo.setColor(MyColors.BLACK);
        globalPaintInfo.setStrokeWidth(DensityUtils.dp2px(SpeakToolApp.app(), DEFAULT_STROKE_WIDTH));
    }

    public EraserPaint() {
        super();
        initCommom();
        eraserPaint.setColor(globalPaintInfo.getColor());
        eraserPaint.setStrokeWidth(globalPaintInfo.getStrokeWidth());//
    }

    public EraserPaint(int strokeWidth) {
        super();
        initCommom();
        eraserPaint.setColor(globalPaintInfo.getColor());
        eraserPaint.setStrokeWidth(strokeWidth);//
    }

    public Paint getPaint() {
        return eraserPaint;
    }

    private void initCommom() {
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.SQUARE);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        eraserPaint.setAntiAlias(true);
    }

    public static PaintInfoBean getGlobalPaintInfo() {
        return globalPaintInfo;
    }
}
