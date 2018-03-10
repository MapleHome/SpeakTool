package com.speektool.ui.popupwindow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pengenerations.lib.ble.scan.BLEDeviceItemAdapter;
import com.pengenerations.lib.ble.scan.BLEScanManager;
import com.pengenerations.lib.ble.scan.OnBLEScanListener;
import com.speektool.R;
import com.speektool.activity.DrawActivity;
import com.speektool.base.BasePopupWindow;
import com.speektool.impl.handpen.PenHelper;
import com.speektool.utils.T;

/**
 * 顶部功能栏——手写笔
 * 
 * @author shaoshuai
 * 
 */
public class L_HandPenPoW extends BasePopupWindow {
	private Button bt_search;// 搜索
	private TextView tv_cur_state;// 当前状态
	private ListView lv_hand_pen;// 笔列表

	private DrawActivity drawActivity;
	private BluetoothAdapter mBluetoothAdapter;
	private BLEDeviceItemAdapter m_BLEDevAdapter = null;
	private BLEScanManager m_BleScanManager = null;// ble扫描管理

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_hand_pen, null);
	}

	public L_HandPenPoW(Context context, View anchor, DrawActivity drawAct) {
		this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, drawAct);
	}

	public L_HandPenPoW(Context context, View anchor, int w, int h, DrawActivity drawAct) {
		super(context, anchor, w, h);
		drawActivity = drawAct;
		// mHandler = new Handler();
		bt_search = (Button) mRootView.findViewById(R.id.bt_search);
		tv_cur_state = (TextView) mRootView.findViewById(R.id.tv_cur_state);
		lv_hand_pen = (ListView) mRootView.findViewById(R.id.lv_hand_pen);
		// 蓝牙列表
		m_BLEDevAdapter = new BLEDeviceItemAdapter(mContext, R.layout.devicelist_item);
		lv_hand_pen.setEmptyView(tv_cur_state);
		lv_hand_pen.setAdapter(m_BLEDevAdapter);
		// 蓝牙扫描管理
		m_BleScanManager = new BLEScanManager(mContext, m_OnBLEScanListener);
		initBluetoothLEAdapter();
		initListener();
		StartBLEScan();
	}

	OnBLEScanListener m_OnBLEScanListener = new OnBLEScanListener() {
		@Override
		public void onDeviceFound(final BluetoothDevice dev) {
			Log.d("", "找到设备： 地址 : " + dev.getAddress() + ", 名称 : " + dev.getName());
			if (dev.getName() != null)
				m_BLEDevAdapter.addItem(dev);
		}
	};

	/** 初始化蓝牙适配器并检查当前设备是否支持蓝牙 */
	void initBluetoothLEAdapter() {
		// 检查当前设备是否支持蓝牙,然后你可以选择性地禁用BLE的相关特征。
		if (!drawActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			T.showShort(mContext, "设备不支持低功耗蓝牙");
			dismiss();
		}
		// 初始化蓝牙适配器. API级别18以上,通过BluetoothManager获得参考BluetoothAdapter
		BluetoothManager bluetoothManager = (BluetoothManager) drawActivity.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 如果蓝牙适配器不可用, 请求启用
		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
		}
		// 检查设备是否支持蓝牙。
		if (mBluetoothAdapter == null) {
			T.showShort(mContext, "设备不支持低功耗蓝牙");
			dismiss();
			return;
		}
	}

	private void initListener() {
		// 重新搜索
		bt_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reStartBLEScan();// 重新扫描
			}
		});
		// 设备列表点击
		lv_hand_pen.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StopBLEScan();// 停止扫描
				BluetoothDevice bleItem = m_BLEDevAdapter.getItem(position);
				if (bleItem != null) {
					Log.e("条目点击", bleItem.getName() + "------" + bleItem.getAddress());
					connectPen(bleItem);
					dismiss();
				}
			}
		});
	}

	/** 连接笔 */
	public void connectPen(BluetoothDevice bleItem) {

		Log.e("条目点击", bleItem.getName() + "------" + bleItem.getAddress());

		if (PenHelper.isIBISPen(bleItem)) {// 点阵笔
			drawActivity.getIBISPenController().connectHandPen(bleItem);
		} else if (PenHelper.isDigitalPen(bleItem)) {// 数码笔
			drawActivity.getDigitalPenController().connect(bleItem);
		} else {// USB
			T.showShort(mContext, "警告：未知类型手写笔！");
			Log.e("错误", "其它类型手写笔");
		}
	}

	/** 开始BLE扫描 */
	void StartBLEScan() {
		m_BleScanManager.Start();
		// bt_search.setText("扫描ing...");
		// bt_search.setEnabled(false);// 设置不可用
		// tv_cur_state.setText("正在刷新列表，请稍后...");
	}

	/** 重新BLE扫描 */
	void reStartBLEScan() {
		m_BleScanManager.Stop();
		m_BLEDevAdapter.clear();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m_BleScanManager.Start();
	}

	/** 停止扫描 */
	void StopBLEScan() {
		m_BleScanManager.Stop();
		m_BLEDevAdapter.clear();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dismiss() {
		m_BleScanManager.Destory();
		// if (mBluetoothAdapter.isEnabled())
		// mBluetoothAdapter.disable();
		super.dismiss();
	}

}
