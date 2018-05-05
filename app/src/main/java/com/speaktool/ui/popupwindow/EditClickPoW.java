package com.speaktool.ui.popupwindow;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.FocusedView;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.busevents.CloseEditPopupWindowEvent;
import com.speaktool.impl.shapes.EditWidget;

import de.greenrobot.event.EventBus;

/**
 * 文本编辑框
 * 
 * @author shaoshuai
 * 
 */
public class EditClickPoW extends BasePopupWindow implements OnClickListener {
	private EditWidget mWordEdit;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_edit_bar, null);
	}

	public EditClickPoW(Context context, EditWidget edit) {
		this(context, edit, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	public EditClickPoW(Context context, EditWidget edit, int w, int h) {
		super(context, edit.getPage().view(), w, h);
		mWordEdit = edit;
		EventBus.getDefault().register(this);
		//
		mRootView.findViewById(R.id.imgtv_delete).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_copy).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_edit).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_scaleBig).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_scaleSmall).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_color).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_lock).setOnClickListener(this);

		mPopupWindow.setFocusable(false);// 是否具有获取焦点的能力
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(false);// 外部触摸

		mRootView.setFocusable(true);
		mRootView.setFocusableInTouchMode(true);
		mRootView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					if (isShow) {
						onDestory();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgtv_delete:
			mWordEdit.delete();
			onDestory();
			break;
		case R.id.imgtv_copy:
			onDestory();
			mWordEdit.copy();
			break;
		case R.id.imgtv_edit:
			onDestory();
			mWordEdit.intoEdit(false);
			break;
		case R.id.imgtv_scaleBig:
			mWordEdit.scaleBig();
			break;
		case R.id.imgtv_scaleSmall:
			mWordEdit.scaleSmall();
			break;
		case R.id.imgtv_color:
			PickFontColorsPoW popupWindow = new PickFontColorsPoW(mContext, mToken, v, mWordEdit);
			popupWindow.showPopupWindow(WeiZhi.Top);
			break;
		case R.id.imgtv_lock:
			mWordEdit.switchLock();
			onDestory();
			break;
		}
	}

	public void onEventMainThread(CloseEditPopupWindowEvent event) {
		onDestory();
	}

	private void onDestory() {
		((FocusedView) mWordEdit).exitFocus();
		EventBus.getDefault().unregister(this);
		dismiss();
	}

}
