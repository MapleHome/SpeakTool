package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;

/**
 * 进度对话框
 * 
 * @author Maple Shao
 * 
 */
public class ProgressDialogOffer {
	/**
	 * if you can use this,do not use
	 * {@link ProgressDialogOffer#offerDialogAsFloat}
	 * 
	 * @param activityContext
	 * @param msg
	 * @return
	 */
	public static Dialog offerDialogAsActivity(Context activityContext, String msg) {
		return new LoadingDialog(activityContext, false, msg);
	}

	/**
	 * we recommand use {@link ProgressDialogOffer#offerDialogAsActivity} if you
	 * can.
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog offerDialogAsFloat(Context context, String msg) {
		return new LoadingDialog(context, true, msg);
	}

}
