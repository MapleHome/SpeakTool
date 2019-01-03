package com.speaktool.view.layouts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @author maple
 * @time 2018/4/15.
 */
public class FloatDragLayout extends FrameLayout {
    private final int OFFSET_ALLOW_DISTANCE = 10;

    private boolean isNearScreenEdge = true;// 是否自动贴边
    private boolean isDragAction;
    PointF startPosition = new PointF();
    PointF lastTouchPoint = new PointF();

    public FloatDragLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatDragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatDragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // nothing
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // set default location point
        // View parent = (View) getParent();
        // setLocation(parent.getWidth(), parent.getHeight() / 2);
    }

    public void setLocation(float x, float y) {
        this.setX(x);
        this.setY(y);
        nearScreenEdge(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);

                startPosition.x = getX() - event.getRawX();
                startPosition.y = getY() - event.getRawY();
                // save last touch point
                lastTouchPoint.x = event.getRawX();
                lastTouchPoint.y = event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = event.getRawX() - lastTouchPoint.x;
                float distanceY = event.getRawY() - lastTouchPoint.y;

                if (Math.sqrt(distanceX * distanceX + distanceY * distanceY) > OFFSET_ALLOW_DISTANCE) {
                    isDragAction = true;
                    setX(event.getRawX() + startPosition.x);
                    setY(event.getRawY() + startPosition.y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDragAction) {
                    setPressed(false);
                    // save last touch point
                    lastTouchPoint.x = event.getRawX();
                    lastTouchPoint.y = event.getRawY();

                    if (isNearScreenEdge) {
                        nearScreenEdge(300);
                    } else {
                        updateLayoutParams();
                    }
                    isDragAction = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isDragAction) {
                    isDragAction = false;
                }
                break;
            default:
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

    private PointF getNearPoint() {
        View parent = (View) getParent();
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();
        int bottomY = parentHeight - getHeight();

        float rightDistance = parentWidth - getX();
        float bottomDistance = parentHeight - getY();

        float xMinDistance = getX() <= rightDistance ? getX() : rightDistance;
        float yMinDistance = getY() <= bottomDistance ? getY() : bottomDistance;

        float xValue = 0;
        float yValue = 0;
        if (xMinDistance <= yMinDistance) {
            yValue = getY();
            if (getX() > parentWidth / 2) {
                xValue = parentWidth - getWidth();
            }
        } else {
            xValue = getX();
            if (getY() > parentHeight / 2) {
                yValue = bottomY;
            }
        }
        // [ minValue , maxValue ]
        float maxX = parentWidth - getWidth();
        xValue = xValue < 0 ? 0 : xValue;
        xValue = xValue > maxX ? maxX : xValue;
        float maxY = parentHeight - getHeight();
        yValue = yValue < 0 ? 0 : yValue;
        yValue = yValue > maxY ? maxY : yValue;

        return new PointF(xValue, yValue);
    }

    private void nearScreenEdge(long duration) {
        PointF pointF = getNearPoint();
        // save last position
        // defLocationPoint = pointF;
        // add animator
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "x", getX(), pointF.x),
                ObjectAnimator.ofFloat(this, "y", getY(), pointF.y)
        );
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateLayoutParams();
            }
        });
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    private void updateLayoutParams() {
//        LayoutParams layoutParams = new LayoutParams(getWidth(), getHeight());
//        layoutParams.setMargins(left, top, right, bottom);
//        setLayoutParams(layoutParams);
    }

    public void setNearScreenEdge(boolean nearScreenEdge) {
        isNearScreenEdge = nearScreenEdge;
    }

    //------------------------------ view state save ------------------------------

    @Override
    protected Parcelable onSaveInstanceState() {
        FloatDragLayout.SavedViewState state = new FloatDragLayout.SavedViewState(super.onSaveInstanceState());
        state.lastPoint.x = lastTouchPoint.x;
        state.lastPoint.y = lastTouchPoint.y;
        state.isDragAction = isDragAction;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof FloatDragLayout.SavedViewState) {
            FloatDragLayout.SavedViewState ss = (FloatDragLayout.SavedViewState) state;
            lastTouchPoint.x = ss.lastPoint.x;
            lastTouchPoint.y = ss.lastPoint.y;
            isDragAction = ss.isDragAction;
        }
    }

    static class SavedViewState extends BaseSavedState {
        PointF lastPoint = new PointF();
        boolean isDragAction;

        SavedViewState(Parcelable superState) {
            super(superState);
        }

        private SavedViewState(Parcel source) {
            super(source);
            lastPoint.x = source.readFloat();
            lastPoint.y = source.readFloat();
            isDragAction = source.readByte() == (byte) 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(lastPoint.x);
            out.writeFloat(lastPoint.y);
            out.writeByte(isDragAction ? (byte) 1 : (byte) 0);
        }

        public static final Creator<SavedViewState> CREATOR = new Creator<SavedViewState>() {
            @Override
            public FloatDragLayout.SavedViewState createFromParcel(Parcel source) {
                return new FloatDragLayout.SavedViewState(source);
            }

            @Override
            public FloatDragLayout.SavedViewState[] newArray(int size) {
                return new FloatDragLayout.SavedViewState[size];
            }
        };

    }

}
