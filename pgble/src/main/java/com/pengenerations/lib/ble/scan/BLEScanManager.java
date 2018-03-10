package com.pengenerations.lib.ble.scan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BLEScanManager {
	final String TAG = BLEScanManager.class.getName();
	private Context m_Context = null;
	BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler = null;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;

	OnBLEScanListener m_OnBleScanListener = null;

	@SuppressLint("NewApi")
	public BLEScanManager(Context context, OnBLEScanListener onBleScanListener) {
		m_Context = context;
		m_OnBleScanListener = onBleScanListener;
		mHandler = new Handler();

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) m_Context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// if bluetooth adapter is not available, request enable.
		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
		}

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			return;
		}
	}

	public void Start() {
		scanLeDevice(true);
	}

	public void Stop() {
		scanLeDevice(false);
	}

	public void Destory() {
		try {
			m_OnBleScanListener = null;
			Stop();

			Thread.sleep(1000);
			mBluetoothAdapter = null;
		} catch (Exception e) {
		}
	}

	/**
	 * This function is scan handler.
	 * 
	 * @param enable
	 */
	@SuppressLint("NewApi")
	private void scanLeDevice(final boolean enable) {

		if (mBluetoothAdapter == null) {
			final BluetoothManager bluetoothManager = (BluetoothManager) m_Context
					.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}

		if (enable) {

			mHandler.postDelayed(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					try {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						mBluetoothAdapter.startLeScan(mLeScanCallback);
					} catch (Exception e) {
					}

				}
			}, SCAN_PERIOD);

			mBluetoothAdapter.startLeScan(mLeScanCallback);
			Log.d(TAG, "Enter StartScanLEDevice");
		} else {
			// m_TextView.setVisibility(View.INVISIBLE);
			// mBluetoothAdapter.cancelDiscovery();
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mBluetoothAdapter = null;
			Log.d(TAG, "Enter StopScanLEDevice");
		}
	}

	/**
	 * Callback Function of scanning Bluetooth LE device
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			try {
				// Log.d(TAG,"UUID : "+ device.getUuids().toString());
				Log.d(TAG, device.getAddress().toString() + device.getName());
				Log.d(TAG, "rssi = " + rssi);

				// notify event
				if (m_OnBleScanListener != null)
					m_OnBleScanListener.onDeviceFound(device);

			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
	};
}
