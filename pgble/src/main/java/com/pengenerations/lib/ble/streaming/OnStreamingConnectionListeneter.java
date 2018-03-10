package com.pengenerations.lib.ble.streaming;

/**
 * Abstract interface to listen to pen event sent from pen. Caller should
 * implement the body of this interface
 */
public abstract interface OnStreamingConnectionListeneter {
	/**
	 * ±Ê¶Ï¿ª. No reason code of disconnection is not existed.
	 * 
	 * @return
	 */
	public abstract int onDisconnected();

	/**
	 * ±ÊÁ¬½Ó
	 * 
	 * @return
	 */
	public abstract void onConnected(int penType);

}
