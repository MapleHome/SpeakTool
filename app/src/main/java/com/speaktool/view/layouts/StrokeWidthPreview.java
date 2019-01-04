package com.speaktool.view.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.speaktool.utils.DensityUtils;

public class StrokeWidthPreview extends View {

    public StrokeWidthPreview(Context context) {
        super(context);
        init();
    }

    public StrokeWidthPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StrokeWidthPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawPath(mPath, mPreviewPaint);
    }

    private static final int PATH_PADDING_LEFT = 10;
    private static final int PATH_PADDING_RIGHT = 20;

    private Path mPath;
    private Paint mPreviewPaint;

    private void init() {
        mPreviewPaint = new Paint();
        mPreviewPaint.setStyle(Paint.Style.STROKE);
        mPreviewPaint.setStrokeJoin(Paint.Join.ROUND);
        mPreviewPaint.setStrokeCap(Paint.Cap.ROUND);

        mPreviewPaint.setFlags(Paint.DITHER_FLAG);
        mPreviewPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPreviewPaint.setAntiAlias(true);
        mPreviewPaint.setDither(true);

        // init path.
        mPath = new Path();
        int previewWidth = DensityUtils.dp2px(getContext(), 200);
        int previewHeight = DensityUtils.dp2px(getContext(), 50);

        int t = previewWidth;
        float befx = PATH_PADDING_LEFT;
        float w = (float) (2 * Math.PI / t);
        float a = previewHeight / 5;// zheng fu.[A]
        float fi = previewHeight / 2;// xiang wei.

        float befy = getYfromX(befx, a, w, fi);
        mPath.moveTo(befx, befy);// y=sinx.
        int maxX = previewWidth - PATH_PADDING_RIGHT;
        for (float x = befx; x < maxX; x++) {
            float y = getYfromX(x, a, w, fi);
            mPath.quadTo(befx, befy, (befx + x) / 2, (befy + y) / 2);
            befx = x;
            befy = y;

        }

    }

    private static float getYfromX(float x, float a, float w, float fi) {
        float y = (float) ((a * Math.sin(w * x)) + fi);
        return y;
    }

    public void preview(int strokeWidthPix, int color) {

        mPreviewPaint.setStrokeWidth(strokeWidthPix);
        mPreviewPaint.setColor(color);
        invalidate();

    }

}
