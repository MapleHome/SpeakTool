package com.speaktool.ui.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.api.Page.Page_BG;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.ui.layouts.ItemViewChooseBackgroundPop;

/**
 * 左侧功能栏——更多功能——更换背景
 *
 * @author shaoshuai
 */
public class L_M_ChangeBgPoW extends BasePopupWindow implements OnClickListener {
    private ItemViewChooseBackgroundPop itemWhiteBg;
    private ItemViewChooseBackgroundPop itemGridBg;
    private ItemViewChooseBackgroundPop itemListBg;
    private ItemViewChooseBackgroundPop itemCoordinateBg;

    private Draw mDraw;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_change_bg, null);
    }

    public L_M_ChangeBgPoW(Context context, View anchor, Draw draw) {
        this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
    }

    public L_M_ChangeBgPoW(Context context, View anchor, int w, int h, Draw draw) {
        super(context, anchor, w, h);
        mDraw = draw;

        itemWhiteBg = (ItemViewChooseBackgroundPop) mRootView.findViewById(R.id.itemWhiteBg);
        itemGridBg = (ItemViewChooseBackgroundPop) mRootView.findViewById(R.id.itemGridBg);
        itemListBg = (ItemViewChooseBackgroundPop) mRootView.findViewById(R.id.itemListBg);
        itemCoordinateBg = (ItemViewChooseBackgroundPop) mRootView.findViewById(R.id.itemCoordinateBg);

        itemWhiteBg.setOnClickListener(this);
        itemGridBg.setOnClickListener(this);
        itemListBg.setOnClickListener(this);
        itemCoordinateBg.setOnClickListener(this);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Page_BG type = mDraw.getCurrentBoard().getBackgroundType();// 获取类型
        switch (type) {
            case White:
                itemWhiteBg.setCheckState(true);
                break;
            case Line:
                itemListBg.setCheckState(true);
                break;
            case Grid:
                itemGridBg.setCheckState(true);
                break;
            case Cor:
                itemCoordinateBg.setCheckState(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int pageId = mDraw.getCurrentBoard().getPageID();
        Page_BG bgType = Page_BG.White;

        switch (v.getId()) {
            case R.id.itemWhiteBg:// 白色背景
                bgType = Page_BG.White;
                break;
            case R.id.itemListBg:// 线条背景
                bgType = Page_BG.Line;
                break;
            case R.id.itemGridBg:// 网格背景
                bgType = Page_BG.Grid;
                break;
            case R.id.itemCoordinateBg:// 坐标背景
                bgType = Page_BG.Cor;
                break;
            default:
                break;
        }

        dismiss();
        mDraw.setPageBackgroundClick(pageId, bgType);
        ((ItemViewChooseBackgroundPop) v).setCheckState(true);
    }

}
