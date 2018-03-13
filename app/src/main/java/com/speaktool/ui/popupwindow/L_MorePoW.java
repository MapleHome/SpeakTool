package com.speaktool.ui.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.impl.modes.DrawModeWord;
import com.speaktool.manager.DrawModeManager;

/**
 * 顶部功能栏——更多功能窗体
 * 
 * @author shaoshuai
 * 
 */
public class L_MorePoW extends BasePopupWindow implements OnClickListener {

	private Draw mDraw;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_more_operation, null);
	}

	public L_MorePoW(Context context, View anchor, Draw draw) {
		this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
	}

	public L_MorePoW(Context context, View anchor, int w, int h, Draw draw) {
		super(context, anchor, w, h);
		mDraw = draw;

		mRootView.findViewById(R.id.iv_add_text).setOnClickListener(this);
		mRootView.findViewById(R.id.iv_add_img).setOnClickListener(this);
		mRootView.findViewById(R.id.iv_change_bg).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		dismiss();// Pow隐藏
		
		switch (v.getId()) {
		case R.id.iv_add_text:// 添加文字
			mDraw.pauseRecord();// 暂停记录
			DrawModeManager.getIns().setDrawMode(new DrawModeWord());
			break;
		case R.id.iv_add_img:// 添加图片
			mDraw.pauseRecord();// 暂停记录
			L_M_AddImgPoW addImgPow = new L_M_AddImgPoW(mContext, mAnchorView, mDraw, (PickPhotoCallback) mDraw);
			addImgPow.showPopupWindow(WeiZhi.Right);
			break;
		case R.id.iv_change_bg:// 更改背景
			L_M_ChangeBgPoW changeBgPow = new L_M_ChangeBgPoW(mContext, mAnchorView, mDraw);
			changeBgPow.showPopupWindow(WeiZhi.Right);
			break;
		default:
			break;
		}
	}

}
