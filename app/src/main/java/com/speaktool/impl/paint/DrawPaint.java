package com.speaktool.impl.paint;

import android.graphics.Paint;

import com.speaktool.R;
import com.speaktool.SpeakApp;
import com.speaktool.bean.PaintInfoBean;
import com.speaktool.tasks.MyColors;
import com.speaktool.utils.DensityUtils;

/**
 * 绘画笔
 *
 * @author Maple Shao
 */
public class DrawPaint {
    private static final float DEFAULT_STROKE_WIDTH = 1.5f; //默认笔迹宽度（dip）

    private Paint mDrawPaint = new Paint();
    private static PaintInfoBean globalPaintInfo;

    static {
        globalPaintInfo = new PaintInfoBean(
                MyColors.BLACK,
                R.drawable.black,
                R.drawable.black_seleted,
                DensityUtils.dp2px(SpeakApp.app(), DEFAULT_STROKE_WIDTH)
        );
    }

    public DrawPaint() {
        this(globalPaintInfo.getColor(), globalPaintInfo.getStrokeWidth());
    }

    public DrawPaint(int color, int strokeWidth) {
        initCommon();
        mDrawPaint.setColor(color);
        mDrawPaint.setStrokeWidth(strokeWidth);
    }

    /**
     * 初始化通用属性
     */
    private void initCommon() {
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setFlags(Paint.DITHER_FLAG);
        mDrawPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
    }

    public Paint getPaint() {
        return mDrawPaint;
    }

    public static PaintInfoBean getGlobalPaintInfo() {
        return globalPaintInfo;
    }
}
