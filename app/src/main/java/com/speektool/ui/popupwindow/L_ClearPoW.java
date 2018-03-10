package com.speektool.ui.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speektool.R;
import com.speektool.activity.DrawActivity;
import com.speektool.api.Draw;
import com.speektool.base.BasePopupWindow;
import com.speektool.bean.ClearPageData;
import com.speektool.utils.T;

/**
 * 顶部功能栏——更多功能——清除本页内容
 * 
 * @author shaoshuai
 * 
 */
public class L_ClearPoW extends BasePopupWindow implements OnClickListener {
	private DrawActivity drawActivity;
	private Draw mDraw;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_clearclick, null);
	}

	public L_ClearPoW(Context context, View anchor, Draw draw, DrawActivity drawAct) {
		this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw, drawAct);
	}

	public L_ClearPoW(Context context, View anchor, int w, int h, Draw draw, DrawActivity drawAct) {
		super(context, anchor, w, h);
		mDraw = draw;
		drawActivity = drawAct;

		mRootView.findViewById(R.id.tvClearPagePen).setOnClickListener(this);
		mRootView.findViewById(R.id.tvClearPagePenAndContents).setOnClickListener(this);
		mRootView.findViewById(R.id.tvClearPageRecords).setOnClickListener(this);
		mRootView.findViewById(R.id.tvDelPage).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int pageId = mDraw.getCurrentBoard().getPageID();// 画纸界面ID

		switch (v.getId()) {
		case R.id.tvDelPage:// 删除界面
			dismiss();
			drawActivity.deletePager();
			break;
		case R.id.tvClearPagePenAndContents:// 清除本页所有内容
			dismiss();
			mDraw.clearPageClick(pageId, ClearPageData.OPT_CLEAR_ALL);
			break;
		case R.id.tvClearPagePen:// 清除本页绘画笔记
			dismiss();
			mDraw.clearPageClick(pageId, ClearPageData.OPT_CLEAR_NOTES);
			break;
		case R.id.tvClearPageRecords:// 清除本页录音
			dismiss();
			if (mDraw.getPageRecorder().isHaveRecordForPage(pageId)) {
				mDraw.preChangePage(new Runnable() {
					@Override
					public void run() {
						mDraw.getPageRecorder().deletePageRecord(pageId);
					}
				});
			} else {
				T.showShort(mContext, "本页还没有录像！");
			}
			break;
		default:
			break;
		}
	}

}
