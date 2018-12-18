package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.speaktool.R;
import com.speaktool.impl.api.Draw;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;
import com.speaktool.impl.DrawModeManager;
import com.speaktool.impl.modes.DrawModeWord;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 顶部功能栏——更多功能窗体
 *
 * @author shaoshuai
 */
public class L_MorePoW extends BasePopupWindow {
    private Draw mDraw;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_more_operation, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public L_MorePoW(Context context, View anchor, Draw draw) {
        super(context, anchor);
        mDraw = draw;
    }


    // 添加文字
    @OnClick(R.id.iv_add_text)
    void addText() {
        dismiss();
        mDraw.pauseRecord();// 暂停记录
        DrawModeManager.getIns().setDrawMode(new DrawModeWord());
    }

    // 添加图片
    @OnClick(R.id.iv_add_img)
    void addImage() {
        dismiss();
        mDraw.pauseRecord();// 暂停记录
        new L_M_AddImgPoW(mContext, parentView, mDraw, (PickPhotoCallback) mDraw)
                .showPopupWindow(WeiZhi.Right);
    }

    // 更改背景
    @OnClick(R.id.iv_change_bg)
    void changePageBg() {
        dismiss();
        new L_M_ChangeBgPoW(mContext, parentView, mDraw)
                .showPopupWindow(WeiZhi.Right);
    }


}
