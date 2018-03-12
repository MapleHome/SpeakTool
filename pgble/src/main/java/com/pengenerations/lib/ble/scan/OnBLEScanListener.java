package com.pengenerations.lib.ble.scan;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙扫描监听
 *
 * @author shaoshuai
 *
 */
public interface OnBLEScanListener {

	abstract public void onDeviceFound(final BluetoothDevice dev);

}
