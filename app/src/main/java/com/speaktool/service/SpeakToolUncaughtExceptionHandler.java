package com.speaktool.service;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Intent;

import com.speaktool.SpeakToolApp;

public class SpeakToolUncaughtExceptionHandler implements UncaughtExceptionHandler {
	// private final static String tag = GLogger.DEBUG ?
	// "SpeakToolUncaughtExceptionHandler"
	// : SpeakToolUncaughtExceptionHandler.class.getSimpleName();
	private UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

	public SpeakToolUncaughtExceptionHandler() {
		super();
		mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		Intent it = new Intent(SpeakToolApp.app(), ErrorService.class);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(ErrorService.EXTRA_EXCEPTION_OBJ, ex);
		SpeakToolApp.app().startService(it);
		/**
		 * handle exception to defaultHandler.
		 */
		String msgex = ex.getMessage();
		if (msgex != null && msgex.contains("recycled bitmap")) {
			ex.printStackTrace();

		} else {
			mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
		}

	}

}
