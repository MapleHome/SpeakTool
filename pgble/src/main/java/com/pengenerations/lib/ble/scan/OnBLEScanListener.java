package com.pengenerations.lib.ble.scan;

import android.bluetooth.BluetoothDevice;

/**
 * ����ɨ�����
 * 
 * @author shaoshuai
 * 
 */
public interface OnBLEScanListener {

	abstract public void onDeviceFound(final BluetoothDevice dev);

}
