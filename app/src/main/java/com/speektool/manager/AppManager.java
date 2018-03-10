package com.speektool.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.speektool.utils.MD5Util;

public class AppManager {

	// 当前APP和设备的类型
	public static final int IOS_PAD = 10;
	public static final int IOS_PHONE = 11;
	public static final int ANDROID_PAD = 20;
	public static final int ANDROID_PHONE = 21;

	/** 获取当前App版本号 */
	public static int getCurrentAppVersionCode(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 1;
		}
		return packageInfo.versionCode;
	}

	/** 获取当前App版本名称 */
	public static String getCurrentAppVersionName(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
		return packageInfo.versionName;
	}

	public static int getCurrentAppSystemCode() {
		return ANDROID_PAD;
	}

	public static final String getAppSignEncodedByMd5(Context context) {
		String backString = "";
		try {
			PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_SIGNATURES);
			byte[] arrayOfByte = mPackageInfo.signatures[0].toByteArray();
			backString = MD5Util.MD5(new String(arrayOfByte));
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return backString;
	}

}
