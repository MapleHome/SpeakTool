package com.speektool.ui.popupwindow;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow.OnDismissListener;

import com.ishare_lib.ui.dialog.AlertDialog;
import com.speektool.R;
import com.speektool.api.Draw;
import com.speektool.base.BasePopupWindow;
import com.speektool.impl.player.PlayProcess;
import com.speektool.service.PlayService;
import com.speektool.ui.dialogs.ProgressDialogOffer;
import com.speektool.utils.ScreenFitUtil;
import com.speektool.utils.T;

/**
 * 右侧功能栏——预览界面
 * 
 * @author shaoshuai
 * 
 */
public class R_PreviwPoW extends BasePopupWindow implements OnClickListener, OnDismissListener {
	private Draw mDraw;
	private Dialog mLoadingDialog;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_previewclick, null);
	}

	public R_PreviwPoW(Context context, View anchor, Draw draw) {
		this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
	}

	public R_PreviwPoW(Context context, View anchor, int w, int h, Draw draw) {
		super(context, anchor, w, h);

		this.setOnDismissListener(this);
		mDraw = draw;
		mMakeReleaseScriptResultReceiver = new MakeReleaseScriptResultReceiver();
		IntentFilter filter = new IntentFilter(PlayProcess.ACTION_PREVIEW_RESULT);
		mContext.registerReceiver(mMakeReleaseScriptResultReceiver, filter);
		//
		mRootView.findViewById(R.id.tvPreviewPage).setOnClickListener(this);
		mRootView.findViewById(R.id.tvPreviewAll).setOnClickListener(this);

		mDraw.pauseRecord();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tvPreviewPage:// 预览本页
		{
			final int pageid = mDraw.getCurrentBoard().getPageID();
			if (!mDraw.getPageRecorder().isHaveRecordForPage(pageid)) {
				T.showShort(mContext, "本页还没有录像！");
				return;
			}
			//
			final String dirPath = mDraw.getPageRecorder().getRecordDir();
			showLoading();
			Intent it = new Intent(mContext, PlayService.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			it.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_PREVIEW);
			it.putExtra(PlayProcess.EXTRA_RECORD_DIR, dirPath);
			it.putExtra(PlayProcess.EXTRA_PREVIEW_PAGE_ID, pageid);
			it.putExtra(PlayProcess.EXTRA_SCREEN_INFO, ScreenFitUtil.getCurrentDeviceInfo());
			mContext.startService(it);
		}
			break;
		case R.id.tvPreviewAll:// 预览全部
			if (!mDraw.getPageRecorder().isHaveRecordForAll()) {
				T.showShort(mContext, "还没有录像！");
				return;
			}
			final String dirPath = mDraw.getPageRecorder().getRecordDir();
			showLoading();
			Intent it = new Intent(mContext, PlayService.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			it.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_PREVIEW);
			it.putExtra(PlayProcess.EXTRA_RECORD_DIR, dirPath);
			it.putExtra(PlayProcess.EXTRA_SCREEN_INFO, ScreenFitUtil.getCurrentDeviceInfo());
			mContext.startService(it);
			break;
		}
	}

	@Override
	public void onDismiss() {
		if (mMakeReleaseScriptResultReceiver != null)
			mContext.unregisterReceiver(mMakeReleaseScriptResultReceiver);
	}

	private void showLoading() {
		mLoadingDialog = ProgressDialogOffer.offerDialogAsActivity(mDraw.context(), "正在加载");
		mLoadingDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					showCancelMakeReleaseRecordDialog();
					return true;
				}
				return false;
			}
		});
		mLoadingDialog.show();
	}

	private void showCancelMakeReleaseRecordDialog() {
		new AlertDialog(mDraw.context()).builder().setTitle("提示").setMsg("您确定要放弃合成录像吗？")
				.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						PlayService.killServiceProcess(mContext);
						dismissLoading();
					}
				}).show();
	}

	private void dismissLoading() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}

	private MakeReleaseScriptResultReceiver mMakeReleaseScriptResultReceiver;

	private class MakeReleaseScriptResultReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			dismissLoading();
		}
	}
}
