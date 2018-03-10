package com.speektool.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class LocalFileUtils {

	public static byte[] loadFile(String path) {
		try {
			FileInputStream ins = new FileInputStream(path);
			byte[] buffer = new byte[10240];
			int len = -1;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((len = ins.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			ins.close();
			bos.flush();
			byte[] ret = bos.toByteArray();
			bos.close();
			return ret;
		} catch (Error e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
