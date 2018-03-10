package com.speektool.impl.handpen;

/**
 * 手写笔连接状态事件
 * 
 * @author shaoshuai
 * 
 */
public class HandpenStateEvent {
	/** 连接状态 */
	public static final int STATE_CONNECTED = 1;
	/** 未连接状态 */
	public static final int STATE_DISCONNECTED = 2;

	public int state;

	public HandpenStateEvent(int state) {
		this.state = state;
	}
}
