package com.pengenerations.lib.util;

import java.nio.ByteBuffer;

import android.util.Log;

public class PGUtils {
	final String TAG = "PGUtil";
	// Native Utilities
	static {
		System.loadLibrary("PGUtil");
	}

	public static native String pageAddrLong2String(long lowPageAddr, long highPageAddr);

	public String GetPageAddr(long ulPageAddr) {
		long lowPageAddr = ulPageAddr & 0xffffffff;
		long highPageAddr = (ulPageAddr >> 32) & 0xffffffff;
		String pageAddr = pageAddrLong2String(lowPageAddr, highPageAddr);
		Log.d(TAG, "Page Address : " + pageAddr);
		return pageAddr;
	}

	public static native int GetShelfIndex(long lowPageAddr, long highPageAddr);

	public static native int GetBookIndex(long lowPageAddr, long highPageAddr);

	public static native int GetPageIndex(long lowPageAddr, long highPageAddr);

	public int GetShelfNum(long ulPageAddr) {
		long lowPageAddr = ulPageAddr & 0xffffffff;
		long highPageAddr = (ulPageAddr >> 32) & 0xffffffff;
		int shelf = GetShelfIndex(lowPageAddr, highPageAddr);
		Log.d(TAG, "Shelf Index : " + shelf);
		return shelf;
	}

	public int GetBookNum(long ulPageAddr) {
		long lowPageAddr = ulPageAddr & 0xffffffff;
		long highPageAddr = (ulPageAddr >> 32) & 0xffffffff;
		int book = GetBookIndex(lowPageAddr, highPageAddr);
		Log.d(TAG, "Book Index : " + book);
		return book;
	}

	public int GetPageNum(long ulPageAddr) {
		long lowPageAddr = ulPageAddr & 0xffffffff;
		long highPageAddr = (ulPageAddr >> 32) & 0xffffffff;
		int page = GetPageIndex(lowPageAddr, highPageAddr);
		Log.d(TAG, "Page Index : " + page);
		return page;
	}

	// public static native String penIdlong2byte(long low,long high);
	public static native String penIdLong2String(byte[] penId);

	public String GetPenID(long penId) {
		final ByteBuffer sBuf = ByteBuffer.allocate(8);
		sBuf.position(0);
		sBuf.putLong(penId);

		String sPenId = penIdLong2String(sBuf.array());
		Log.d(TAG, "Pen ID : " + sPenId);
		return sPenId;
	}

	String HEXES = "0123456789ABCDEF";

	public String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));

			hex.append(' ');
		}
		return hex.toString();
	}

	public String getHex(byte[] raw, int size) {
		if (raw == null) {
			return null;
		}
		int cnt = 0;

		final StringBuilder hex = new StringBuilder(2 * size);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
			hex.append(' ');
			if (size == cnt++)
				break;
		}
		return hex.toString();
	}

	final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public String bytesToHex(byte bytes) {
		char[] hexChars = new char[2];
		int v = bytes & 0xFF;
		hexChars[0] = hexArray[v >>> 4];
		hexChars[1] = hexArray[v & 0x0F];
		return new String(hexChars);
	}

	public String nibbleToHex(byte bytes) {
		char[] hexChars = new char[1];
		int v = bytes & 0xFF;
		hexChars[0] = hexArray[v & 0x0F];
		return new String(hexChars);
	}

	public String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static native byte crc8(byte[] vptr, int len, byte crc);

	public static native int crc16(byte[] vptr, int len);

	public static native int crc16Update(byte[] vptr, int len, int crc);

	public static native int crc16Calc(byte[] vptr, int length);

	public byte GetCRC8(byte[] vptr, int len, byte crc) {
		return crc8(vptr, len, crc);
	}

	public int GetCRC16Update(byte[] vptr, int len, int crc) {
		return crc16Update(vptr, len, crc);
	}

	public String hexToASCII(String hexValue) {
		StringBuilder output = new StringBuilder("");
		for (int i = 0; i < hexValue.length(); i += 2) {
			String str = hexValue.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

	public String hexToASCII(byte[] hexValue) {
		StringBuilder output = new StringBuilder("");

		for (int i = 0; i < hexValue.length; i += 2) {
			String str = bytesToHex(hexValue[i]) + bytesToHex(hexValue[i + 1]);
			// output.append((char) Integer.parseInt(str, 16));
			// output.append(":");
			output.append(hexToASCII(str) + ":");
			i++;
		}
		return output.substring(0, 17);
	}

	public static native void uSleep(double us);

	public void Sleep(double us) {
		uSleep(us);
	}
}
