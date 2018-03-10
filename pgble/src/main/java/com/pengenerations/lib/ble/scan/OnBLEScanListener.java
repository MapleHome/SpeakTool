package com.pengenerations.lib.ble.scan;

import android.bluetooth.BluetoothDevice;

/**
 * ¿∂—¿…®√Ëº‡Ã˝
 * 
 * @author shaoshuai
 * 
 */
public interface OnBLEScanListener {

	abstract public void onDeviceFound(final BluetoothDevice dev);

}
