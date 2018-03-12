package com.pengenerations.lib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 文件管理
 *
 * @author shaoshuai
 *
 */
public class FileManager {
	final String TAG = "FileManager";
	Context m_Context = null;
	private File m_FileHandler = null;
	PGUtils m_Utils = null;

	public FileManager(Context context) {
		m_Context = context;
		m_Utils = new PGUtils();
		m_FileHandler = OpenFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+ "/IBISDevList.dat");
	}

	private File OpenFile(String filePath) {
		boolean isSuccess = false;
		File file = new File(filePath);
		if (file != null && !file.exists()) {
			Log.e(TAG, "!file.exists");
			try {
				isSuccess = file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Log.e(TAG, "File Create Result : " + isSuccess);
			}
		} else {
			Log.e(TAG, "file.exists");
		}
		return file;
	}

	/**
	 * 获取保存的蓝牙地址
	 *
	 * @return
	 */
	public String GetStoredBLEAddress() {
		String btAddr = "";
		byte[] buffer = new byte[17];

		FileInputStream fis;
		try {
			fis = new FileInputStream(m_FileHandler);
			if (fis.read(buffer, 0, 17) > 0) {
				btAddr = m_Utils.hexToASCII(buffer);
			}
			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return btAddr;
	}

	/**
	 * 保存蓝牙地址
	 *
	 * @param btAddr
	 * @return
	 */
	public boolean SetStoredBLEAddress(String btAddr) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(m_FileHandler);
			fos.write(btAddr.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
