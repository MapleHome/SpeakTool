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
import com.speaktool.impl.shapes.ImageWidget;

import de.greenrobot.event.EventBus;

/**
 * 图片编辑框
 * 
 * @author shaoshuai
 * 
 */
public class ImageClickPopupWindow extends BasePopupWindow implements OnClickListener {

	private ImageWidget mImage;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_imageclick, null);
	}

	public ImageClickPopupWindow(Context context, ImageWidget edit) {
		this(context, edit, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	public ImageClickPopupWindow(Context context, ImageWidget edit, int w, int h) {
		super(context, edit.getPage().view(), w, h);
		mImage = edit;
		EventBus.getDefault().register(this);

		mRootView.findViewById(R.id.imgtv_delete).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_copy).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_rotate).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_widthAutoFit).setOnClickListener(this);
		mRootView.findViewById(R.id.imgtv_heightAutoFit).setOnClickListener(this);
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
			mImage.delete();
			onDestory();
			break;
		case R.id.imgtv_copy:
			onDestory();
			mImage.copy();
			break;
		case R.id.imgtv_rotate:
			mImage.rotate();
			break;
		case R.id.imgtv_widthAutoFit:
			mImage.widthAutoFit();
			break;
		case R.id.imgtv_heightAutoFit:
			mImage.heightAutoFit();
			break;
		case R.id.imgtv_lock:
			mImage.switchLock();
			onDestory();
			break;
		}
	}

	public void onEventMainThread(CloseEditPopupWindowEvent event) {
		onDestory();
	}

	private void onDestory() {
		((FocusedView) mImage).exitFocus();
		EventBus.getDefault().unregister(this);
		dismiss();
	}

}
