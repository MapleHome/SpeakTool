package com.speektool.busevents;

/**
 * 课程记录时间改变
 * 
 * @author shaoshuai
 * 
 */
public class RecordTimeChangedEvent {

	private final long now;

	private final long closeTime;

	public RecordTimeChangedEvent(long now, long closeTime) {
		super();
		this.now = now;
		this.closeTime = closeTime;
	}

	public long getNow() {
		return now;
	}

	public long getCloseTime() {

		return closeTime;
	}

}
