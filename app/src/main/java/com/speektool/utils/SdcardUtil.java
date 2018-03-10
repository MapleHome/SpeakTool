package com.speektool.utils;

import android.os.Environment;

public class SdcardUtil {

	public static boolean isSdcardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

}
