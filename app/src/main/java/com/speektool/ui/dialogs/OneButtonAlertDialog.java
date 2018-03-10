package com.speektool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.utils.DensityUtils;
import com.speektool.utils.DisplayUtil;

public class OneButtonAlertDialog extends Dialog implements View.OnClickListener {

	private Button btnOk;
	private TextView tvMsg;
	private String mMessage;

	public OneButtonAlertDialog(Context context, String msg) {
		this(context, R.style.dialogTheme, msg, false);
	}

	public OneButtonAlertDialog(Context context, String msg, boolean isShowAsFloat) {
		this(context, R.style.dialogTheme, msg, isShowAsFloat);
	}

	public OneButtonAlertDialog(Context context, int theme, String msg, boolean isShowAsFloat) {
		super(context, theme);
		if (!isShowAsFloat) {
			Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
		} else {
			getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		}
		mMessage = msg;
		init();
	}

	private void init() {
		this.setCanceledOnTouchOutside(false);
	}

	private static final int DIALOG__WIDTH = DensityUtils.dp2px(SpeekToolApp.app(), 400);// pix
	private static final int DIALOG_HEIGHT = DensityUtils.dp2px(SpeekToolApp.app(), 250);// pixv

	private static Point getOneButtonDialogSize(Context context) {

		Point screen = DisplayUtil.getScreenSize(context);
		int stH = DisplayUtil.getStatusbarHeightPix(context);
		int w = screen.x > DIALOG__WIDTH ? DIALOG__WIDTH : screen.x - stH;
		int h = screen.y > DIALOG_HEIGHT ? DIALOG_HEIGHT : screen.y - stH;
		return new Point(w, h);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_one_button);
		View rootLay = findViewById(R.id.rootLay);
		// 重置dialog大小
		Point sz = getOneButtonDialogSize(getContext());
		int w = sz.x;
		int h = sz.y;
		ViewGroup.LayoutParams lp1 = rootLay.getLayoutParams();
		lp1.height = h;
		lp1.width = w;
		rootLay.setLayoutParams(lp1);
		//
		btnOk = (Button) findViewById(R.id.btnOk);
		if (buttonCLickListener == null)
			btnOk.setOnClickListener(this);
		else
			btnOk.setOnClickListener(buttonCLickListener);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		tvMsg.setText(mMessage);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		if (mBackPressListener == null)
			this.dismiss();
		else
			mBackPressListener.onBackPressed();
	}

	private View.OnClickListener buttonCLickListener;

	public void setButtonClickListener(View.OnClickListener lsn) {
		buttonCLickListener = lsn;
	}

	private BackPressListener mBackPressListener;

	public void setBackpressListener(BackPressListener lsn) {
		mBackPressListener = lsn;
	}

	@Override
	public void onClick(View v) {
		if (v == btnOk)
			this.dismiss();
	}

	public static interface BackPressListener {
		void onBackPressed();
	}
}
