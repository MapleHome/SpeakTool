package com.speaktool.handpen;

import android.bluetooth.BluetoothDevice;

import com.google.gson.Gson;
import com.speaktool.utils.SPUtils;

/**
 * 笔的辅助类
 */
public class PenHelper {

    public static final String PREF_PREVIOUS_PEN = "pref_previous_pen";
    public static final int PAPER_WIDTH = 7485;// 页面宽
    public static final int PAPER_HEIGHT = 4118;// 页面高
    public static final int CONNECT_TIMEOUT_MILLS = 20000;// 连接超时时间

    /**
     * 获取连接成功的笔的蓝牙信息
     */
    public static BluetoothDevice getConnectSuccessPen() {
        String previousPenJson = new SPUtils().getString(PREF_PREVIOUS_PEN,"");
        return new Gson().fromJson(previousPenJson, BluetoothDevice.class);
    }

    /**
     * 保存连接成功的笔到SP
     */
    public static void saveConnectSuccessPen(BluetoothDevice pen) {
        if (pen == null) {
            return;
        }
        new SPUtils().put(PREF_PREVIOUS_PEN, new Gson().toJson(pen));
    }

    /**
     * 是否是智能笔
     */
    public static boolean isDigitalPen(BluetoothDevice bleItem) {
        // 开始判断是否是数码笔
        boolean isPen = (bleItem.getAddress().contains("B0:1F:81:")) || (bleItem.getName().contains("Pen"));
        return isPen;
    }

    /**
     * 是否是点阵笔
     */
    public static boolean isIBISPen(BluetoothDevice bleItem) {
        boolean isIBIS = (bleItem.getAddress().contains("B4:39:34:")) || (bleItem.getName().contains("PG MODEM"));
        return isIBIS;
    }
}
