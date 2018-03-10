package com.speektool.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.speektool.SpeekToolApp;

public class SPUtils {
	private static Context sContext = SpeekToolApp.app();
	private static final String PREF_NAME = "prefer.xml";

	public static void putInt(String key, int value) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putInt(key, value);
		ed.commit();
	}

	public static int getInt(String key, int defValue) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getInt(key, defValue);
	}

	public static void putString(String key, String value) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putString(key, value);
		ed.commit();
	}

	public static String getString(String key, String defValue) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getString(key, defValue);
	}

	public static void putBool(String key, boolean value) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	public static boolean getBool(String key, boolean defValue) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(key, defValue);
	}

	public static void putLong(String key, long value) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putLong(key, value);
		ed.commit();
	}

	public static long getLong(String key, long defValue) {
		SharedPreferences pref = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getLong(key, defValue);
	}
}
