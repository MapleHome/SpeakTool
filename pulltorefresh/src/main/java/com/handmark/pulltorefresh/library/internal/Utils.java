package com.handmark.pulltorefresh.library.internal;

import android.util.Log;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Log.w(LOG_TAG, "你用过时的 " + depreacted + " 属性，请切换到 " + replacement);
	}

}
