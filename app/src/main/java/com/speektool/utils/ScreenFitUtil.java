package com.speektool.utils;

import android.graphics.Point;

import com.speektool.bean.ScreenInfoBean;

public class ScreenFitUtil {
	private static final String tag = ScreenFitUtil.class.getSimpleName();

	private static int inputScreenWidth = 1;
	private static int inputScreenHeight = 1;
	private static int inputScreenDensity = 1;
	//
	private static int currentScreenWidth = 1;
	private static int currentScreenHeight = 1;
	private static int currentScreenDensity = 1;

	public static void setInputDeviceInfo(ScreenInfoBean info) {
		inputScreenWidth = info.w;
		inputScreenHeight = info.h;
		inputScreenDensity = info.density;
	}

	public static ScreenInfoBean getInputDeviceInfo() {
		ScreenInfoBean info = new ScreenInfoBean();
		info.w = inputScreenWidth;
		info.h = inputScreenHeight;
		info.density = inputScreenDensity;
		return info;
	}

	public static void setCurrentDeviceInfo(ScreenInfoBean info) {
		currentScreenWidth = info.w;
		currentScreenHeight = info.h;
		currentScreenDensity = info.density;

	}

	public static ScreenInfoBean getCurrentDeviceInfo() {
		ScreenInfoBean info = new ScreenInfoBean();
		info.w = currentScreenWidth;
		info.h = currentScreenHeight;
		info.density = currentScreenDensity;
		return info;
	}

	public static int mapStokeWidthtoCurrentScreen(int inputStrokeWidth, ScreenInfoBean inputDevice) {
		float factor = ((float) currentScreenDensity) / inputDevice.density;
		int ret = (int) (inputStrokeWidth * factor);
		return inputStrokeWidth;
	}

	public static int mapXtoCurrentScreenSize(int x, ScreenInfoBean inputDevice) {
		float factorX = ((float) currentScreenWidth) / inputDevice.w;
		int ret = (int) (x * factorX);
		return ret;
	}

	public static int mapYtoCurrentScreenSize(int y, ScreenInfoBean inputDevice) {
		float factorY = ((float) currentScreenHeight) / inputDevice.h;
		int ret = (int) (y * factorY);
		return ret;
	}

	public static int mapTextSize(int size, ScreenInfoBean inputDevice) {
		float facw = ((float) currentScreenWidth / (float) inputDevice.w);
		float fach = ((float) currentScreenHeight / (float) inputDevice.h);
		float fac = Math.min(facw, fach);
		return (int) (fac * size);

	}

	public static float getFactorX(ScreenInfoBean inputDevice) {
		float factorX = ((float) currentScreenWidth) / inputDevice.w;
		return factorX;
	}

	public static float getFactorY(ScreenInfoBean inputDevice) {
		float factorY = ((float) currentScreenHeight) / inputDevice.h;
		return factorY;
	}

	public static int mapStokeWidthtoCurrentScreen(int inputStrokeWidth) {
		float factor = ((float) currentScreenDensity) / inputScreenDensity;
		int ret = (int) (inputStrokeWidth * factor);
		return ret;
	}

	public static int mapXtoCurrentScreenSize(int x) {
		float factorX = ((float) currentScreenWidth) / inputScreenWidth;
		int ret = (int) (x * factorX);
		return ret;
	}

	public static int mapYtoCurrentScreenSize(int y) {
		float factorY = ((float) currentScreenHeight) / inputScreenHeight;
		int ret = (int) (y * factorY);
		return ret;
	}

	public static int mapTextSize(int size) {
		float facw = ((float) currentScreenWidth / (float) inputScreenWidth);
		float fach = ((float) currentScreenHeight / (float) inputScreenHeight);
		float fac = Math.min(facw, fach);
		return (int) (fac * size);

	}

	public static float getFactorX() {
		float factorX = ((float) currentScreenWidth) / inputScreenWidth;
		return factorX;
	}

	public static float getFactorY() {
		float factorY = ((float) currentScreenHeight) / inputScreenHeight;
		return factorY;
	}

	public static Point getKeepRatioScaledSize(float ratioHW, int w, int h) {
		float neww, newh;
		if (w <= h) {
			neww = w;
			newh = neww * ratioHW;
			if (newh > h) {
				newh = h;
				neww = newh / ratioHW;
			}
		} else {// h<w
			newh = h;
			neww = newh / ratioHW;
			if (neww > w) {
				neww = w;
				newh = neww * ratioHW;
			}
		}
		return new Point((int) neww, (int) newh);
	}

	public static float getCurrentDeviceHeightWidthRatio() {
		return currentScreenHeight / (float) currentScreenWidth;
	}
}
