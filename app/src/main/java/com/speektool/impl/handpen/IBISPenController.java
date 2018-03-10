package com.speektool.impl.handpen;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import com.pengenerations.lib.ble.scan.BLEScanManager;
import com.pengenerations.lib.ble.scan.OnBLEScanListener;
import com.pengenerations.lib.ble.streaming.OnPenConnectListener;
import com.pengenerations.lib.ble.streaming.OnPenStreamListener;
import com.pengenerations.lib.ble.streaming.PGBLEManager;
import com.pengenerations.lib.util.FileManager;
import com.speektool.activity.DrawActivity;
import com.speektool.bean.ScreenInfoBean;
import com.speektool.impl.modes.StrokeHandler;
import com.speektool.paint.DrawPaint;
import com.speektool.ui.dialogs.LoadingDialogHelper;
import com.speektool.utils.DisplayUtil;
import com.speektool.utils.ScreenFitUtil;
import com.speektool.utils.T;
import com.speektool.utils.UiHandler;

import de.greenrobot.event.EventBus;

/**
 * 点阵笔控制器
 */
public class IBISPenController {
	private final StrokeHandler mStrokeHandler;
	private final BLEScanManager m_BleScanManager;
	private final LoadingDialogHelper mLoadingDialogHelper;
	private String m_StoredBTAddress;
	private DrawActivity draw;
	private ScreenInfoBean inputDevice;// 屏幕信息
	private FileManager m_FileManager = null;
	private PGBLEManager m_PGBLEMng;
	private BluetoothDevice pen;
	private String mDeviceAddress;// 设备地址
	private boolean isConnected = false;// 是否连接
	private boolean isUserRequestDestroy = false;// 是否用户请求设备

	public IBISPenController(DrawActivity draw) {
		this.draw = draw;
		mStrokeHandler = new StrokeHandler(draw);
		mLoadingDialogHelper = new LoadingDialogHelper(draw);
		m_PGBLEMng = new PGBLEManager(draw.getApplicationContext(), true);
		m_PGBLEMng.RegisterOnPenStreamListener(m_OnPenStreamListener);
		m_PGBLEMng.RegisterOnPenConnectListener(m_OnPenConnectListener);

		m_FileManager = new FileManager(draw.getApplicationContext());
		m_BleScanManager = new BLEScanManager(draw.getApplicationContext(), m_OnBLEScanListener);
		enableBluetoothLEAdapter();
		m_StoredBTAddress = m_FileManager.GetStoredBLEAddress();
	}

	private void showDialog() {
		Dialog d = mLoadingDialogHelper.showLoading();
		d.setCanceledOnTouchOutside(false);
		d.setCancelable(true);
	}

	private void dismissDialog() {
		mLoadingDialogHelper.dismissLoading();
	}

	/** 连接手写笔 */
	public void connectHandPen(BluetoothDevice pen) {
		Log.e("点阵笔", "连接手写笔");
		showDialog();
		this.pen = pen;
		mDeviceAddress = pen.getAddress();
		Point size = ScreenFitUtil.getKeepRatioScaledSize(ScreenFitUtil.getCurrentDeviceHeightWidthRatio(),
				PenHelper.PAPER_WIDTH, PenHelper.PAPER_HEIGHT);
		inputDevice = new ScreenInfoBean(size.x, size.y, DisplayUtil.getScreenDensity(draw.context()));
		isUserRequestDestroy = false;
		connectBoothDevice(mDeviceAddress);
		checkTimeOut();
	}

	// 超时监听
	private void checkTimeOut() {
		UiHandler.postDelay(new Runnable() {
			@Override
			public void run() {
				if (!isConnected()) {
					// draw.getBlueToothController().startScan();
				}
			}
		}, PenHelper.CONNECT_TIMEOUT_MILLS);
	}

	/** 断开连接 */
	public void destroy() {
		isUserRequestDestroy = true;
		isConnected = false;
		stopBLEScan();
		m_PGBLEMng.Destroy();// can re init.
	}

	private void autoReconnect() {
		if (isUserRequestDestroy) {
			return;
		}
		connectBoothDevice(mDeviceAddress);
	}

	/** 蓝牙扫描监听 */
	private OnBLEScanListener m_OnBLEScanListener = new OnBLEScanListener() {
		@Override
		public void onDeviceFound(final BluetoothDevice dev) {
			if (!TextUtils.isEmpty(m_StoredBTAddress) && m_StoredBTAddress.contains(dev.getAddress())) {
				stopBLEScan();
				mDeviceAddress = dev.getAddress();
				connectBoothDevice(mDeviceAddress);
			} else {
				if (dev.getName().contains("PGMODEM")) {
					stopBLEScan();
					mDeviceAddress = dev.getAddress();
					connectBoothDevice(mDeviceAddress);
				}
			}
		}
	};

	/** 停止蓝牙扫描 */
	private void stopBLEScan() {
		m_BleScanManager.Stop();
		dismissDialog();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** 是否连接 */
	public boolean isConnected() {
		return isConnected;
	}

	@SuppressLint("NewApi")
	private void enableBluetoothLEAdapter() {
		if (!draw.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			T.showShort(draw, "设备不支持蓝牙！");
			return;
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) draw.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			T.showShort(draw, "设备不支持蓝牙！");
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}
	}

	/** 连接蓝牙设备 */
	private void connectBoothDevice(String deviceAddress) {
		Log.e("点阵笔连接", "设备地址：" + deviceAddress);
		// initialize bluetooth le device and try to connect IBIS Pen with this
		// bluetooth address
		m_PGBLEMng.initialize(deviceAddress);
	}

	// 点阵笔连接状态监听
	private final OnPenConnectListener m_OnPenConnectListener = new OnPenConnectListener() {
		@Override
		public void onPenServiceStarted() {
			Log.e("点阵笔连接监听", "服务开启");
		}

		@Override
		public void onConnected(int penType) {
			dismissDialog();
			isConnected = true;
			EventBus.getDefault().post(new HandpenStateEvent(HandpenStateEvent.STATE_CONNECTED));
			// request pen info
			byte[] payload = new byte[1];
			payload[0] = (byte) PGBLEManager.ResponseHandlerCommand.RHS_HOST_REQ_PEN_INFO.ordinal();
			m_PGBLEMng.SendHostCommand(payload, (byte) 1);
			// save connection bluetooth address
			if (!TextUtils.isEmpty(mDeviceAddress)) {
				m_FileManager.SetStoredBLEAddress(mDeviceAddress);
				PenHelper.saveConnectSuccessPen(pen);
			}
		}

		@Override
		public void onConnectFailed(int reasonCode) {
			dismissDialog();
			isConnected = false;
			EventBus.getDefault().post(new HandpenStateEvent(HandpenStateEvent.STATE_DISCONNECTED));
			// autoReconnect();
			// draw.getBlueToothController().startScan();

		}
	};

	// 点阵笔流监听
	private final OnPenStreamListener m_OnPenStreamListener = new OnPenStreamListener() {

		@Override
		public int onSoundStatus(byte allSound, byte sleepSound) {
			return 0;
		}

		@Override
		public int onPenup() {
			UiHandler.post(new Runnable() {
				@Override
				public void run() {
					mStrokeHandler.up();
				}
			});
			return 0;
		}

		@Override
		public int onPendown() {
			UiHandler.post(new Runnable() {
				@Override
				public void run() {
					mStrokeHandler.down(DrawPaint.getGlobalPaintInfo().getColor(), DrawPaint.getGlobalPaintInfo()
							.getStrokeWidth(), false);
				}
			});
			return 0;
		}

		@Override
		public int onNoCoord(int m_nEventType) {
			return 0;
		}

		@Override
		public int onNewSession(long nTimeStamp, int nVid, int nPid, long penSerial, int swVer) {
			byte[] payload = new byte[1];
			// request battery info
			payload[0] = (byte) PGBLEManager.ResponseHandlerCommand.RHS_HOST_REQ_BAT_INFO.ordinal();
			m_PGBLEMng.SendHostCommand(payload, (byte) 1);
			return 0;
		}

		@Override
		public int onMemoryFillLevel(int percent) {
			return 0;
		}

		@Override
		public int onRemainBattery(int percent) {
			byte[] payload = new byte[1];
			// request memory fill info
			payload[0] = (byte) PGBLEManager.ResponseHandlerCommand.RHS_HOST_REQ_MEM_INFO.ordinal();
			m_PGBLEMng.SendHostCommand(payload, (byte) 1);
			return 0;
		}

		@Override
		public int onDisconnected() {
			dismissDialog();
			isConnected = false;
			// ToastUtils.systemToast("手写笔断开连接！");
			EventBus.getDefault().post(new HandpenStateEvent(HandpenStateEvent.STATE_DISCONNECTED));
			autoReconnect();
			return 0;
		}

		@Override
		public int onCoord(long nTimeStamp, long ullPageAddress, final short nX, final short nY, byte nForce) {
			UiHandler.post(new Runnable() {
				@Override
				public void run() {
					mStrokeHandler.move(ScreenFitUtil.mapXtoCurrentScreenSize(nX, inputDevice),
							ScreenFitUtil.mapYtoCurrentScreenSize(nY, inputDevice));
				}
			});
			return 0;
		}
	};
}
