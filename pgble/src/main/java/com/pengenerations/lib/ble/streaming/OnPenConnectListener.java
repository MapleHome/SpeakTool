package com.pengenerations.lib.ble.streaming;

/**
 * �ʵ�����״̬������
 */
public abstract interface OnPenConnectListener {
	/**
	 * �ʵķ���׼��������
	 */
	abstract public void onPenServiceStarted();

	/**
	 * ������״̬
	 * 
	 * @param penType
	 *            �ʵ��������� (����, TDN-101 or PGD-601)
	 */
	abstract public void onConnected(int penType);

	/**
	 * USB���ӱ�ʧ�ܡ�
	 */
	abstract public void onConnectFailed(int reasonCode);
}
