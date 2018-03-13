package com.speektool.impl.handpen;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smart.pen.core.common.Listeners;
import com.smart.pen.core.model.DeviceObject;
import com.smart.pen.core.model.PointObject;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.services.SmartPenService;
import com.smart.pen.core.symbol.BatteryState;
import com.smart.pen.core.symbol.ConnectState;
import com.smart.pen.core.symbol.Keys;
import com.smart.pen.core.symbol.SceneType;
import com.speektool.SpeekToolApp;
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
 * 数字笔控制器
 */
public class DigitalPenController {
	public static final int CONNECT_TYPE_BLUETOOTH = 1;// 蓝牙连接
	public static final int CONNECT_TYPE_USB = 2;// USB连接

	public static final int MSG_CHECK_BIND_SERVICE = 1;// 检查绑定服务
	public static final int BIND_SERVICE_DELAY = 1;// 绑定服务延迟
	private static final int REQUEST_SETTING_SIZE = 2;// 请求设置大小

	private final LoadingDialogHelper mLoadingDialogHelper;
	private DrawActivity draw;
	private MyHandler mHandler;
	private int connectType;
	private final StrokeHandler mStrokeHandler;
	private boolean isConnected = false;
	private BluetoothDevice pen;
	private ScreenInfoBean inputDevice; 

	public DigitalPenController(final DrawActivity draw) {
		this.draw = draw;
		mHandler = new MyHandler(this);
		mLoadingDialogHelper = new LoadingDialogHelper(draw);
		mStrokeHandler = new StrokeHandler(draw);

		draw.setOnActivityResultListener(new DrawActivity.OnActivityResultListener() {
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				if (resultCode == Activity.RESULT_OK) {
					if (requestCode == REQUEST_SETTING_SIZE) {
						PenService service = SpeekToolApp.app().getPenService();
						inputDevice = new ScreenInfoBean(service.getSceneWidth(), service.getSceneHeight(), DisplayUtil
								.getScreenDensity(draw.context()));
						service.setSceneType(SceneType.CUSTOM, inputDevice.w, inputDevice.h);
						initPage();
					}
				}
			}
		});
	}

	private void showDialog() {
		Dialog d = mLoadingDialogHelper.showLoading();
		d.setCanceledOnTouchOutside(false);
		d.setCancelable(true);
		d.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mHandler.removeMessages(MSG_CHECK_BIND_SERVICE);
			}
		});
	}

	private void dismissDialog() {
		mLoadingDialogHelper.dismissLoading();
	}

	/** 是否连接 */
	public boolean isConnected() {
		return isConnected;
	}

	/** 连接数码笔 */
	public void connect(BluetoothDevice pen) {
//		draw.getIBISPenController().destroy();
		// draw.getDigitalPenController().destroy();// usb.

		this.pen = pen;
		if (pen == null) {
			this.connectType = CONNECT_TYPE_USB;
		} else {
			this.connectType = CONNECT_TYPE_BLUETOOTH;
		}

		switch (connectType) {
		case CONNECT_TYPE_BLUETOOTH:// 蓝牙
			showDialog();
			SpeekToolApp.app().unBindPenService();
			SpeekToolApp.app().bindPenService(Keys.APP_PEN_SERVICE_NAME);
			break;
		case CONNECT_TYPE_USB:// USB
			showDialog();
			SpeekToolApp.app().unBindPenService();
			SpeekToolApp.app().bindPenService(Keys.APP_USB_SERVICE_NAME);
			break;
		default:
			return;
		}
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_CHECK_BIND_SERVICE), BIND_SERVICE_DELAY);
	}

	/** 断开连接disconnectDevice */
	public void destroy() {
		PenService service = SpeekToolApp.app().getPenService();
		if (service != null) {
			service.setOnPointChangeListener(null);
			service.disconnectDevice();// 断开连接https://github.com/shaoshuai904
		}
	}

	private static class MyHandler extends Handler {

		private WeakReference<DigitalPenController> weakReference;

		MyHandler(DigitalPenController c) {
			weakReference = new WeakReference<>(c);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_CHECK_BIND_SERVICE) {
				if (SpeekToolApp.app().getPenService() == null) {
					sendMessageDelayed(obtainMessage(MSG_CHECK_BIND_SERVICE), BIND_SERVICE_DELAY);
				} else {
					final DigitalPenController c = weakReference.get();
					if (c != null) {
						c.doBindSuccess();
					}
				}
			}
		}
	}

	/** 绑定成功 */
	private void doBindSuccess() {
		PenService service = SpeekToolApp.app().getPenService();
		if (service == null) {
			return;
		}
		switch (connectType) {
		case CONNECT_TYPE_BLUETOOTH:// 蓝牙
			service.scanDevice(onScanDeviceListener);
			break;
		case CONNECT_TYPE_USB:// USB
			initSceneType();
			break;
		}
	}

	private Listeners.OnScanDeviceListener onScanDeviceListener = new Listeners.OnScanDeviceListener() {
		@Override
		public void find(DeviceObject device) {
			Log.e("发现设备", " 名称:" + device.name);
			if (device.address.equals(pen.getAddress())) {
				PenService service = SpeekToolApp.app().getPenService();
				if (service != null) {
					service.stopScanDevice();
					ConnectState state = ((SmartPenService) service).connectDevice(onConnectStateListener,
							device.address);
					if (state != ConnectState.CONNECTING) {
						T.showShort(draw, "连接失败!");
						dismissDialog();
					} else {
						showDialog();
					}
				}
			}
		}

		@Override
		public void complete(HashMap<String, DeviceObject> map) {
			if (!isFinded(map)) {
				dismissDialog();
				T.showShort(draw, "连接失败，未发现设备！");
				// draw.getBlueToothController().startScan();
			}
		}
	};

	private boolean isFinded(HashMap<String, DeviceObject> map) {
		if (map == null || map.isEmpty()) {
			return false;
		}
		Collection<DeviceObject> vals = map.values();
		for (DeviceObject e : vals) {
			if (e.address.equals(pen.getAddress())) {
				return true;
			}
		}
		return false;
	}

	// 连接设备监听
	private Listeners.OnConnectStateListener onConnectStateListener = new Listeners.OnConnectStateListener() {
		@Override
		public void stateChange(String address, ConnectState state) {
			switch (state) {
			case PEN_READY:// 笔准备完成
				break;
			case PEN_INIT_COMPLETE:// 初始化 完成
				dismissDialog();
				isConnected = true;
				initSceneType();
				break;
			case CONNECTED:// 连接成功
				isConnected = true;
				PenHelper.saveConnectSuccessPen(pen);
				EventBus.getDefault().post(new HandpenStateEvent(HandpenStateEvent.STATE_CONNECTED));
				break;
			case SERVICES_FAIL:// 发现服务失败
				dismissDialog();
				T.showShort(draw, "The pen service discovery failed.");
				// draw.getBlueToothController().startScan();
				break;
			case CONNECT_FAIL:// 连接错误
				dismissDialog();
				T.showShort(draw, "The pen service connection failure.");
				// draw.getBlueToothController().startScan();
				break;
			case DISCONNECTED:// 断开
				dismissDialog();
				EventBus.getDefault().post(new HandpenStateEvent(HandpenStateEvent.STATE_DISCONNECTED));
				isConnected = false;
				break;
			default:
				break;
			}
		}
	};

	/** 初始化纸张尺寸 */
	private void initSceneType() {
		final PenService service = SpeekToolApp.app().getPenService();
		final Point size = ScreenFitUtil.getKeepRatioScaledSize(ScreenFitUtil.getCurrentDeviceHeightWidthRatio(),
				PenHelper.PAPER_WIDTH, PenHelper.PAPER_HEIGHT);
		service.setSceneType(SceneType.CUSTOM, size.x, size.y);
		initPage();

	}

	private void initPage() {
		final PenService service = SpeekToolApp.app().getPenService();
		inputDevice = new ScreenInfoBean(service.getSceneWidth(), service.getSceneHeight(),
				DisplayUtil.getScreenDensity(draw.context()));
		service.setOnPointChangeListener(onPointChangeListener);
	}

	// 手写笔坐标更改监听
	private Listeners.OnPointChangeListener onPointChangeListener = new Listeners.OnPointChangeListener() {
		boolean isRoutePrevious = false;

		@Override
		public void change(final PointObject point) {
			if (point.isRoute && !isRoutePrevious) {
				UiHandler.post(new Runnable() {
					@Override
					public void run() {
						mStrokeHandler.down(DrawPaint.getGlobalPaintInfo().getColor(), DrawPaint.getGlobalPaintInfo()
								.getStrokeWidth(), false);
					}
				});
			} else if (point.isRoute && isRoutePrevious) {
				final int x = ScreenFitUtil.mapXtoCurrentScreenSize(point.originalX, inputDevice);
				final int y = ScreenFitUtil.mapYtoCurrentScreenSize(point.originalY, inputDevice);
				UiHandler.post(new Runnable() {
					@Override
					public void run() {
						mStrokeHandler.move(x, y);
					}
				});
			} else if (!point.isRoute && isRoutePrevious) {
				UiHandler.post(new Runnable() {
					@Override
					public void run() {
						mStrokeHandler.up();
					}
				});
			}
			isRoutePrevious = point.isRoute;
			if (point.battery == BatteryState.LOW) {
				T.showShort(draw, "智能笔电量低!");
			}

		}
	};
}
