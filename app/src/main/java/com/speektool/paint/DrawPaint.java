package com.speektool.paint;

import android.graphics.Paint;

import com.ishare_lib.utils.DensityUtils;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.bean.PaintInfoBean;
import com.speektool.utils.MyColors;

/**
 * 绘画笔
 * 
 * @author Maple Shao
 * 
 */
public class DrawPaint {
	/** 默认笔迹宽度（dip） */
	private static final float DEFAULT_STROKE_WIDTH = 1.5f;

	private final Paint mDrawPaint = new Paint();
	private static PaintInfoBean globalPaintInfo;

	static {
		globalPaintInfo = new PaintInfoBean();
		globalPaintInfo.setColor(MyColors.BLACK);
		globalPaintInfo.setIconResId(R.drawable.black);
		globalPaintInfo.setIconResIdSelected(R.drawable.black_seleted);
		globalPaintInfo.setStrokeWidth(DensityUtils.dp2px(SpeekToolApp.app(), DEFAULT_STROKE_WIDTH));
	}

	public DrawPaint() {
		this(globalPaintInfo.getColor(), globalPaintInfo.getStrokeWidth());
	}

	public DrawPaint(int color, int strokeWidth) {
		super();

		initCommon();
		mDrawPaint.setColor(color);
		mDrawPaint.setStrokeWidth(strokeWidth);
	}

	/** 初始化通用属性 */
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
