package com.speaktool.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.speaktool.R;
import com.speaktool.utils.DensityUtils;
import com.speaktool.utils.T;

/**
 * 通用的popupWindow
 */
public abstract class BasePopupWindow {
    protected PopupWindow mPopupWindow;
    protected View mContentView;// 注入视图
    protected View parentView;// 显示底层
    protected View mAnchorView;// 参照视图

    protected Context mContext;
    private WeiZhi mWz = WeiZhi.Bottom;// 默认显示在底部
    public boolean isShow = false;
    private int margin = 0;

    public BasePopupWindow(Context context, View anchor) {
        this(context, anchor, anchor);
    }

    public BasePopupWindow(Context context, View token, View anchor) {
        this(context, anchor, token, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public BasePopupWindow(Context context, View anchor, int width, int height) {
        this(context, anchor, anchor, width, height);
    }

    // 多了一个token 适用于PopupWindow 上弹出 popupWindow
    public BasePopupWindow(Context context, View token, View anchor, int width, int height) {
        mContext = context;
        parentView = token;
        mAnchorView = anchor;
        mContentView = getContentView(LayoutInflater.from(mContext));
        initView(width, height);
    }

    public abstract View getContentView(LayoutInflater inflater);

    private void initView(int width, int height) {
        // 创建PopupWindow
        mPopupWindow = new PopupWindow(mContentView, width, height);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());// 设置popupWindow弹出窗体的背景
        mPopupWindow.setAnimationStyle(0);// 无需动画
        mPopupWindow.setFocusable(true);// 是否具有获取焦点的能力
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);// 外部触摸
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        // content view
        mContentView.setFocusableInTouchMode(true);
        mContentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    if (isShow) {
                        mPopupWindow.dismiss();
                        // isShow = false;
                    } else {
                        isShow = true;
                    }
                    return true;
                }
                return false;
            }
        });
        setOnDismissListener(null);
    }

    public enum WeiZhi {
        Top, Bottom, Left, Right
    }

    public void showPopupWindow(WeiZhi wz) {
        showPopupWindow(wz, 2);
    }

    /**
     * @param wz         - top / button / left / right
     * @param marginSize - dp size
     */
    public void showPopupWindow(WeiZhi wz, int marginSize) {
        mWz = wz;
        margin = DensityUtils.dp2px(mContext, marginSize);
        toShowPopup(mAnchorView);
    }

    private void toShowPopup(View parent) {
        int[] location = new int[2];
        parent.getLocationOnScreen(location);// 得到v的位置
        // POW宽高
        View contentView = mPopupWindow.getContentView();
        if (contentView == null) {
            T.showShort(mContext, "contentView 为空！");
            return;
        }
        contentView.measure(0, 0);
        int powWidth = contentView.getMeasuredWidth();
        int powHeight = contentView.getMeasuredHeight();

        int x = 0;
        int y = 0;
        int anim = R.style.WeiZhi_bottom;
        switch (mWz) {
            case Top:// 上边
                anim = R.style.WeiZhi_top;
                x = location[0] + (parent.getWidth() - powWidth) / 2;// 水平居中
                y = location[1] - powHeight - margin;// 上偏margin px
                break;
            case Bottom:// 下边
                anim = R.style.WeiZhi_bottom;
                x = location[0] + (parent.getWidth() - powWidth) / 2;// 水平居中
                y = location[1] + parent.getHeight() + margin;// 下偏margin px
                break;
            case Left:// 左侧
                anim = R.style.WeiZhi_left;
                x = location[0] - powWidth - margin;// 左偏margin px
                y = location[1] + (parent.getHeight() - powHeight) / 2;// 竖直居中
                break;
            case Right:// 右侧
                anim = R.style.WeiZhi_right;
                x = location[0] + parent.getWidth() + margin;// 右偏margin px
                y = location[1] + (parent.getHeight() - powHeight) / 2;
                break;
            default:
                break;
        }
        mPopupWindow.setAnimationStyle(anim);// 设置动画
        // Gravity.NO_GRAVITY == Gravity.LEFT | Gravity.TOP
        mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, x, y);// 显示
        setAlpha(defAlpha);
    }


    // -----------------------------------------设置消失监听----------------------------------------------

    /**
     * 设置Pow消失监听
     */
    public BasePopupWindow setOnDismissListener(final PopupWindow.OnDismissListener listener) {
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onDismiss();
                }
                setAlpha(1f);
            }
        });
        return this;
    }

    public void dismiss() {
        mPopupWindow.dismiss();
        isShow = false;
        setAlpha(1f);
    }

    public boolean isShowing() {
        return isShow;
    }

    // -----------------------------------------透明度变化----------------------------------------------
    Window window;
    float defAlpha = 0.7f;

    public BasePopupWindow setAlphaStyle(Activity activity) {
        return setAlphaStyle(activity.getWindow());
    }

    public BasePopupWindow setAlphaStyle(Window window) {
        this.window = window;
        return this;
    }

//    public BasePopupWindow setAlphaStyle(Activity activity, float defAlpha) {
//        return setAlphaStyle(activity.getWindow(), defAlpha);
//    }
//
//    public BasePopupWindow setAlphaStyle(Window window, float defAlpha) {
//        this.window = window;
//        this.defAlpha = defAlpha;
//        return this;
//    }

    public void setAlpha(float alpha) {
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.alpha = alpha;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(params);
        }
    }
}
