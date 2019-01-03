package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.speaktool.R;
import com.speaktool.impl.api.Draw;
import com.speaktool.impl.api.Page.Page_BG;
import com.speaktool.view.layouts.ItemViewChooseBackgroundPop;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 左侧功能栏——更多功能——更换背景
 *
 * @author shaoshuai
 */
public class L_M_ChangeBgPoW extends BasePopupWindow implements OnClickListener {
    @BindView(R.id.itemWhiteBg) ItemViewChooseBackgroundPop itemWhiteBg;
    @BindView(R.id.itemGridBg) ItemViewChooseBackgroundPop itemGridBg;
    @BindView(R.id.itemListBg) ItemViewChooseBackgroundPop itemListBg;
    @BindView(R.id.itemCoordinateBg) ItemViewChooseBackgroundPop itemCoordinateBg;

    private Draw mDraw;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_change_bg, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public L_M_ChangeBgPoW(Context context, View anchor, Draw draw) {
        super(context, anchor);
        mDraw = draw;

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

        changePageBg((ItemViewChooseBackgroundPop) v, bgType);
    }

    private void changePageBg(ItemViewChooseBackgroundPop v, Page_BG bgType) {
        dismiss();
        int pageId = mDraw.getCurrentBoard().getPageID();
        mDraw.setPageBackgroundClick(pageId, bgType);
        v.setCheckState(true);
    }

}
