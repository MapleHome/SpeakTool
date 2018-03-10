package com.speektool.utils;

import android.os.Build;

/**
 * 检查手机系统工具类
 */
public class PhoneOSUtils {

	private PhoneOSUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断是否是小米系统
	 */
	public static boolean isXiaomiOS() {
		String display = Build.DISPLAY;// 显示器- JLS36C
		String manufacturer = Build.MANUFACTURER;// 制造商- LENOVO

		if ((display != null) && (display.toLowerCase().indexOf("miui") >= 0))
			return true;
		if ((manufacturer != null) && (manufacturer.toLowerCase().indexOf("xiaomi") >= 0))
			return true;
		return false;
	}

}
