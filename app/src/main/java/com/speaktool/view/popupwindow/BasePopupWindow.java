package com.speaktool.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.speaktool.R;
import com.speaktool.utils.DensityUtils;
import com.speaktool.utils.T;

import butterknife.ButterKnife;

/**
 * 通用的popupWindow
 */
public abstract class BasePopupWindow {
    protected PopupWindow mPopupWindow;
    protected View mRootView;// 注入视图
    protected View mToken;// 显示底层
    protected View mAnchorView;// 参照视图

    protected Context mContext;
    private WeiZhi mWz = WeiZhi.Bottom;// 默认显示在底部
    public boolean isShow = false;

    public BasePopupWindow(Context context, View anchor, int w, int h) {
        this(context, anchor, anchor, w, h);
    }

    // 多了一个token 适用于PopupWindow 上弹出 popupWindow
    public BasePopupWindow(Context context, View token, View anchor, int w, int h) {
        mContext = context;
        mToken = token;
        mAnchorView = anchor;
        // 内容视图
        mRootView = getContentView();
        // w 为窗口宽、h 为窗口高
        initView(w, h);
    }

    public abstract View getContentView();

    private void initView(int w, int h) {
        // 创建PopupWindow
        mPopupWindow = new PopupWindow(mRootView, w, h);// 默认占满全屏
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
        mRootView.setFocusableInTouchMode(true);
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    if (isShow) {
                        mPopupWindow.dismiss();
                        isShow = false;
                    } else {
                        isShow = true;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public boolean isShowing() {
        return isShow;
    }

    public enum WeiZhi {
        Top, Bottom, Left, Right;
    }

    public void showPopupWindow(WeiZhi wz) {
        mWz = wz;
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
                y = location[1] - powHeight - DensityUtils.dp2px(mContext, 2);// 上偏2dp
                break;
            case Bottom:// 下边
                anim = R.style.WeiZhi_bottom;
                x = location[0] + (parent.getWidth() - powWidth) / 2;// 水平居中
                y = location[1] + parent.getHeight() + DensityUtils.dp2px(mContext, 2);// 下偏2dp
                break;
            case Left:// 左侧
                anim = R.style.WeiZhi_left;
                x = location[0] - powWidth - DensityUtils.dp2px(mContext, 2);// 左偏2dp
                y = location[1] + (parent.getHeight() - powHeight) / 2;// 竖直居中
                break;
            case Right:// 右侧
                anim = R.style.WeiZhi_right;
                x = location[0] + parent.getWidth() + DensityUtils.dp2px(mContext, 2);// 右偏2dp
                y = location[1] + (parent.getHeight() - powHeight) / 2;
                break;
            default:
                break;
        }
        mPopupWindow.setAnimationStyle(anim);// 设置动画
        // Gravity.NO_GRAVITY == Gravity.LEFT | Gravity.TOP
        mPopupWindow.showAtLocation(mToken, Gravity.NO_GRAVITY, x, y);// 显示

    }

    public void dismiss() {
        mPopupWindow.dismiss();
        // mPopupWindow = null;
    }

    // -----------------------------------------设置消失监听----------------------------------------------

    /**
     * 设置Pow消失监听
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.mPopupWindow.setOnDismissListener(listener);
    }
}
