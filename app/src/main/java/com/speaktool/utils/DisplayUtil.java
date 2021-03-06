package com.speaktool.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 显示
 *
 * @author shaoshuai
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
        return context.getResources().getDisplayMetrics().densityDpi;
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
