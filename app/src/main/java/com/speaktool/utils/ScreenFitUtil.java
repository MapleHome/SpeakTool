package com.speaktool.utils;

import android.graphics.Point;

import com.speaktool.bean.ScreenInfoBean;

public class ScreenFitUtil {
    private static int inputScreenWidth = 1;
    private static int inputScreenHeight = 1;
    private static int inputScreenDensity = 1;
    //
    private static int currentScreenWidth = 1;
    private static int currentScreenHeight = 1;
    private static int currentScreenDensity = 1;

    public static void setInputDeviceInfo(ScreenInfoBean info) {
        inputScreenWidth = info.width;
        inputScreenHeight = info.height;
        inputScreenDensity = info.density;
    }

    public static void setCurrentDeviceInfo(ScreenInfoBean info) {
        currentScreenWidth = info.width;
        currentScreenHeight = info.height;
        currentScreenDensity = info.density;
    }

    public static ScreenInfoBean getCurrentDeviceInfo() {
        return new ScreenInfoBean(currentScreenWidth, currentScreenHeight, currentScreenDensity);
    }

    public static float getFactorDensity() {
        return ((float) currentScreenDensity) / inputScreenDensity;
    }

    public static float getFactorX() {
        return ((float) currentScreenWidth) / inputScreenWidth;
    }

    public static float getFactorY() {
        return ((float) currentScreenHeight) / inputScreenHeight;
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
        } else {// height<width
            newh = h;
            neww = newh / ratioHW;
            if (neww > w) {
                neww = w;
                newh = neww * ratioHW;
            }
        }
        return new Point((int) neww, (int) newh);
    }


//    public static int mapTextSize(int size) {
//        float fac = Math.min(getFactorX(), getFactorY());
//        return (int) (fac * size);
//    }
//    public static int mapStokeWidthtoCurrentScreen(int inputStrokeWidth) {
//        return (int) (inputStrokeWidth * getFactorDensity());
//    }
//
//    public static int mapXtoCurrentScreenSize(int x) {
//        return (int) (x * getFactorX());
//    }
//
//    public static int mapYtoCurrentScreenSize(int y) {
//        return (int) (y * getFactorY());
//    }

//	public static ScreenInfoBean getInputDeviceInfo() {
//		ScreenInfoBean info = new ScreenInfoBean();
//		info.width = inputScreenWidth;
//		info.height = inputScreenHeight;
//		info.density = inputScreenDensity;
//		return info;
//	}

//	public static int mapStokeWidthtoCurrentScreen(int inputStrokeWidth, ScreenInfoBean inputDevice) {
//		float factor = ((float) currentScreenDensity) / inputDevice.density;
//		int ret = (int) (inputStrokeWidth * factor);
//		return inputStrokeWidth;
//	}
//
//	public static int mapXtoCurrentScreenSize(int x, ScreenInfoBean inputDevice) {
//		float factorX = ((float) currentScreenWidth) / inputDevice.width;
//		int ret = (int) (x * factorX);
//		return ret;
//	}
//
//	public static int mapYtoCurrentScreenSize(int y, ScreenInfoBean inputDevice) {
//		float factorY = ((float) currentScreenHeight) / inputDevice.height;
//		int ret = (int) (y * factorY);
//		return ret;
//	}
//
//	public static int mapTextSize(int size, ScreenInfoBean inputDevice) {
//		float facw = ((float) currentScreenWidth / (float) inputDevice.width);
//		float fach = ((float) currentScreenHeight / (float) inputDevice.height);
//		float fac = Math.min(facw, fach);
//		return (int) (fac * size);
//
//	}
//
//	public static float getFactorX(ScreenInfoBean inputDevice) {
//		float factorX = ((float) currentScreenWidth) / inputDevice.width;
//		return factorX;
//	}
//
//	public static float getFactorY(ScreenInfoBean inputDevice) {
//		float factorY = ((float) currentScreenHeight) / inputDevice.height;
//		return factorY;
//	}

//	public static float getCurrentDeviceHeightWidthRatio() {
//		return currentScreenHeight / (float) currentScreenWidth;
//	}
}
