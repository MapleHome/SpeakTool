package com.speektool.utils;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ishare_lib.utils.DensityUtils;

/**
 * 显示
 * 
 * @author shaoshuai
 * 
 */
public class DisplayUtil {
	/**
	 * 获取屏幕大小
	 * 
	 * @param context
	 * @return
	 */
	public static Point getScreenSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		Point p = new Point(dm.widthPixels, dm.heightPixels);
		return p;
	}

	/**
	 * 获取屏幕密度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenDensity(Context context) {
		return (int) context.getResources().getDisplayMetrics().densityDpi;
	}

	/**
	 * 获取屏幕大小dp
	 * 
	 * @param context
	 * @return
	 */
	public static Point getScreenSizeDip(Context context) {

		Point p = getScreenSize(context);
		p.x = (int) DensityUtils.px2dp(context, p.x);
		p.y = (int) DensityUtils.px2dp(context, p.y);
		return p;

	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusbarHeightPix(Context context) {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			int sbar = context.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e1) {

			e1.printStackTrace();
			return 0;
		}
	}
}
