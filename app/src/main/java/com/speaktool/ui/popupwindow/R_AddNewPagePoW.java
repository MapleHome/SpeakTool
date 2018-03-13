package com.speaktool.ui.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.bean.CopyPageData;

/**
 * 右侧功能栏——添加新页面
 * 
 * @author shaoshuai
 * 
 */
public class R_AddNewPagePoW extends BasePopupWindow implements OnClickListener {

	private Draw mDraw;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_new_page, null);
	}

	public R_AddNewPagePoW(Context context, View anchor, Draw draw) {
		this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
	}

	public R_AddNewPagePoW(Context context, View anchor, int w, int h, Draw draw) {
		super(context, anchor, w, h);
		ViewUtils.inject(this, mRootView);

		mDraw = draw;
		
		mRootView.findViewById(R.id.tvNewEmptyPage).setOnClickListener(this);
		mRootView.findViewById(R.id.tvCopyPageJustViews).setOnClickListener(this);
		mRootView.findViewById(R.id.tvCopyPageAll).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvNewEmptyPage:// 新建空白页
			dismiss();
			mDraw.preChangePage(new Runnable() {
				@Override
				public void run() {
					mDraw.newEmptyBoardClick();
				}
			});
			break;
		case R.id.tvCopyPageJustViews:// 复制本页不含笔记
			dismiss();
			mDraw.preChangePage(new Runnable() {
				@Override
				public void run() {
					mDraw.copyPageClick(CopyPageData.OPT_COPY_VIEWS);
				}
			});
			break;
		case R.id.tvCopyPageAll:// 复制本页包含笔记
			dismiss();
			mDraw.preChangePage(new Runnable() {
				@Override
				public void run() {
					mDraw.copyPageClick(CopyPageData.OPT_COPY_ALL);
				}
			});
			break;
		}
	}
}
