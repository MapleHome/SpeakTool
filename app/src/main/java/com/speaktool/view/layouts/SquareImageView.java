package com.speaktool.view.layouts;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 宽高相等的正方形iamgeview.
 *
 * @author wangcl
 */
public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sidelen = widthMeasureSpec > heightMeasureSpec ? heightMeasureSpec
                : widthMeasureSpec;
        super.onMeasure(sidelen, sidelen);
    }

}
