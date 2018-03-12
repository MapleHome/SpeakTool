package com.pengenerations.lib.ble.streaming;

/**
 * 笔的连接状态监听器
 */
public abstract interface OnPenConnectListener {
	/**
	 * 笔的服务准备就绪。
	 */
	abstract public void onPenServiceStarted();

	/**
	 * 笔连接状态
	 *
	 * @param penType
	 *            笔的连接类型 (蓝牙, TDN-101 or PGD-601)
	 */
	abstract public void onConnected(int penType);

	/**
	 * USB连接笔失败。
	 */
	abstract public void onConnectFailed(int reasonCode);
}
