package com.speaktool.view.popupwindow;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.ui.draw.DrawActivity;
import com.speaktool.utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 顶部功能栏——手写笔
 *
 * @author shaoshuai
 */
public class L_HandPenPoW extends BasePopupWindow {
    @BindView(R.id.bt_search) Button bt_search;// 搜索
    @BindView(R.id.tv_cur_state) TextView tv_cur_state;// 当前状态
    @BindView(R.id.lv_hand_pen) ListView lv_hand_pen;// 笔列表

    private DrawActivity drawActivity;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_hand_pen, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public L_HandPenPoW(Context context, View anchor, DrawActivity drawAct) {
        super(context, anchor);
        drawActivity = drawAct;
        // mHandler = new Handler();
        // 蓝牙列表
        initBluetoothLEAdapter();
        initListener();
    }

    /**
     * 初始化蓝牙适配器并检查当前设备是否支持蓝牙
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
                // 重新扫描
            }
        });
        // 设备列表点击
        lv_hand_pen.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

}
