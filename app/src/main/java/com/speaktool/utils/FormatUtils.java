package com.speaktool.utils;

import java.text.DecimalFormat;

public class FormatUtils {

	public static float formatFloat(float f) {
		DecimalFormat formater = new DecimalFormat("0.00");
		return Float.valueOf(formater.format(f));
	}

	/** 返回：00:00格式，单位秒。 */
	public static final String getFormatTimeSimple(long timeMillSecs) {
		long sec = timeMillSecs / 1000;
		long min = sec / 60;// 分钟
		long s = sec % 60;// 秒

		String ret = String.format("%1$02d%2$s%3$02d", min, ":", s);

		return ret;
	}

}
