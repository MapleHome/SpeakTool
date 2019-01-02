package com.speaktool.view.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 这里我们只处理了padding的状态 没有处理margin的状态，子view的margin 对measure和layout的影响就留给读者自己完成了
 *
 * @author maple
 * @time 2018/12/27
 */
public class CustomHorizontalLayout extends ViewGroup {
    //设置默认的控件最小是多少 这里不提供自定义属性了 写死在代码里 你们可以自行拓展
    final int minHeight = 0;
    final int minWidth = 0;

    public CustomHorizontalLayout(Context context) {
        super(context);
    }

    public CustomHorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomHorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = 0;
        int measureHeight = 0;
        final int childCount = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        final View childView = getChildAt(0);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        //没有子控件 时 我们的宽高要作特殊处理
        if (childCount == 0) {
            //当没有子控件时，如果长宽有一个为wrap 那么就让这个控件以最小的形式展现
            //这里我们最小设置为0
            if (widthSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(minWidth, minHeight);
            } else {
                //否则根据我们的layout属性来
                setMeasuredDimension(getLayoutParams().width, getLayoutParams().height);
            }
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            measureWidth = childView.getMeasuredWidth() * childCount;
            measureHeight = childView.getMeasuredHeight();
            setMeasuredDimension(
                    paddingLeft + measureWidth + paddingRight,
                    paddingTop + measureHeight + paddingBottom
            );
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            measureHeight = childView.getMeasuredHeight();
            setMeasuredDimension(
                    paddingLeft + paddingRight + widthSpecSize,
                    paddingTop + paddingBottom + measureHeight
            );
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            measureWidth = childView.getMeasuredWidth() * childCount;
            setMeasuredDimension(
                    paddingLeft + paddingRight + measureWidth,
                    paddingTop + paddingBottom + heightSpecSize
            );
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        //左边初始位置为0
        int childLeft = 0 + paddingLeft;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0 + paddingTop, childLeft + childWidth,
                        paddingTop + childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

}
