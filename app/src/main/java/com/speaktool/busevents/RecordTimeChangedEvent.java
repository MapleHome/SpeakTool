package com.speaktool.busevents;

/**
 * 课程记录时间改变
 * 
 * @author shaoshuai
 * 
 */
public class RecordTimeChangedEvent {

	private final long now;

	public RecordTimeChangedEvent(long now) {
		super();
		this.now = now;
	}

	public long getNow() {
		return now;
	}

}
