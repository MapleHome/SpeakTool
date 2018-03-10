package com.speektool.utils;

import android.os.Handler;
import android.os.Looper;

public class UiHandler {

	private static Handler sHandler = new Handler(Looper.getMainLooper());

	public static void post(Runnable r) {
		sHandler.post(r);
	}

	public static void postDelay(Runnable r, long delayMills) {

		sHandler.postDelayed(r, delayMills);
	}
}
