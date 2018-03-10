package com.speektool.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class MiuiManager {

	private static final String tag = MiuiManager.class.getSimpleName();
	private static final String SETTINGS_PACKAGE_NAME = "com.android.settings";

	/** 打开MIUI权限管理界面(MIUI v5, v6) */
	public static void openMiuiPermissionActivity(Context context) {
		try {
			Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
			String versionName = getMiuiVersionName();
			if (TextUtils.isEmpty(versionName)) {
				return;
			}
			int vcode = Integer.valueOf(versionName.substring(1));
			if (vcode < 5) {
				// ignore,not limit floatwindow.
			} else if (vcode == 5) {
				PackageInfo pInfo = null;
				try {
					pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				intent.setClassName(SETTINGS_PACKAGE_NAME, "com.miui.securitycenter.permission.AppPermissionsEditor");
				intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);

			} else {// >5.
				intent.setClassName("com.miui.securitycenter",
						"com.miui.permcenter.permissions.AppPermissionsEditorActivity");
				intent.putExtra("extra_pkgname", context.getPackageName());
			}

			if (isIntentAvailable(context, intent)) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else {
				Log.e(tag, "Intent is not available!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(tag, "Intent is not available!!");
		}
	}

	/**
	 * 判断是否有可以接受的Activity
	 * 
	 * @param context
	 * @param action
	 * @return
	 */
	private static boolean isIntentAvailable(Context context, Intent intent) {
		if (intent == null)
			return false;
		return context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES).size() > 0;
	}

	private static String getMiuiVersionName() {
		String line = null;
		BufferedReader reader = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
			line = reader.readLine();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "V-1";
	}

}
