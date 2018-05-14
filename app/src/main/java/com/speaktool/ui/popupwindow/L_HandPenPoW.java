package com.speaktool.ui.popupwindow;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.ui.activity.DrawActivity;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.utils.T;

/**
 * 顶部功能栏——手写笔
 *
 * @author shaoshuai
 */
public class L_HandPenPoW extends BasePopupWindow {
    private Button bt_search;// 搜索
    private TextView tv_cur_state;// 当前状态
    private ListView lv_hand_pen;// 笔列表

    private DrawActivity drawActivity;
    private BluetoothAdapter mBluetoothAdapter;

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

        initBluetoothLEAdapter();
        initListener();
    }


    public L_HandPenPoW(Context context, View anchor, int w, int h) {
        super(context, anchor, w, h);
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


    @Override
    public void dismiss() {
        super.dismiss();
    }

}
