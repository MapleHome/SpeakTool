package com.speektool.busevents;

/**
 * 音乐状态改变事件
 * 
 * @author shaoshuai
 * 
 */
public class MusicStateChangedEvent {

	private final String key;

	private final int state;
	private final int progress;
	public final static int STATE_ON = 1;
	public final static int STATE_OFF = 2;
	public final static int STATE_PROGRESS = 3;

	public MusicStateChangedEvent(String key, int state, int progress) {
		super();
		this.key = key;
		this.state = state;
		this.progress = progress;
	}

	public MusicStateChangedEvent(String key, int state) {
		this(key, state, 0);
	}

	public String getKey() {
		return key;
	}

	public int getState() {
		return state;
	}

	public int getProgress() {
		return progress;
	}

}
