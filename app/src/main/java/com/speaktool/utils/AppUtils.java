package com.speaktool.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * APP管理类
 * 
 * @author shaoshuai
 * 
 */
public class AppUtils {

	private AppUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/** APP版本名 */
	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	/** APP版本号 */
	public static int getVersionCode(Context context) {
		return getPackageInfo(context).versionCode;
	}

	/** 获取APP包信息 */
	private static PackageInfo getPackageInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			// PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
			// PackageManager.GET_CONFIGURATIONS);
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}