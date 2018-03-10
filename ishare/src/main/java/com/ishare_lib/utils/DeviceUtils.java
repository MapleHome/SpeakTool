package com.ishare_lib.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 设备相关的辅助类
 * 
 * @author shao
 * 
 */
public class DeviceUtils {
	private DeviceUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/** 获得屏幕宽度 */
	public static int getScreenWidth(Context context) {
		return getScreenInfo(context).widthPixels;
	}

	/** 获得屏幕高度 */
	public static int getScreenHeight(Context context) {
		return getScreenInfo(context).heightPixels;
	}

	// ===================================Pad==============================================
	// 系统型号: MI PAD ---1.33333
	// DisplayMetrics{density=2.0, width=1536, height=2048, scaledDensity=2.0,
	// xdpi=325.12, ydpi=325.12}

	// 系统型号: EBEN T7-TD ---1.27
	// DisplayMetrics{density=1.0, width=768, height=976, scaledDensity=1.0,
	// xdpi=159.568, ydpi=159.895}
	// ===================================手机==============================================
	// 系统型号: NX511J ---1.777
	// DisplayMetrics{density=3.0, width=1080, height=1920, scaledDensity=3.0,
	// xdpi=464.949, ydpi=468.923}

	/** 是否是平板 */
	public static boolean isPad(Context context) {
		DisplayMetrics dm = getScreenInfo(context);
		float kgb = 1.5f;
		if (isHengPing(context)) {// 横屏
			kgb = dm.widthPixels * 1.0f / dm.heightPixels;
		} else {// 竖屏
			kgb = dm.heightPixels * 1.0f / dm.widthPixels;
		}
		if (kgb < 1.5f) {
			return true;
		}
		return false;
	}

	/** 获得屏幕信息 */
	public static DisplayMetrics getScreenInfo(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics;
	}

	/** 当前是否是横屏 */
	public static boolean isHengPing(Context context) {
		int curOrientation = context.getResources().getConfiguration().orientation; // 获取屏幕方向
		if (curOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;// 横屏
		} else if (curOrientation == Configuration.ORIENTATION_PORTRAIT) {
			return false;// 竖屏
		}
		return false;
	}

	/** 获得状态栏的高度 */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
}
