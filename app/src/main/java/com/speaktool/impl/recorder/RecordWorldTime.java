package com.speaktool.impl.recorder;

import java.util.Timer;
import java.util.TimerTask;

import com.speaktool.busevents.PlayTimeChangedEvent;
import com.speaktool.busevents.RecordTimeChangedEvent;
import com.speaktool.impl.cmd.ICmd;

import de.greenrobot.event.EventBus;

public class RecordWorldTime {
	private long now = ICmd.TIME_DELETE_FLAG;// millsec.

	private boolean isTicking = false;

	private Timer mTimer;

	private boolean isNeedSendTimeChangedEvent = false;

	private long closeTime = Long.MAX_VALUE;

	private boolean isMake = true;

	public RecordWorldTime(boolean isNeedSendTimeChangedEvent, boolean isMake) {
		super();
		this.isNeedSendTimeChangedEvent = isNeedSendTimeChangedEvent;
		this.isMake = isMake;
	}

	public RecordWorldTime(boolean isNeedSendTimeChangedEvent, long closeTime, boolean isMake) {
		super();
		this.isNeedSendTimeChangedEvent = isNeedSendTimeChangedEvent;
		this.closeTime = closeTime;
		this.isMake = isMake;
	}

	public void setNowTime(long now) {
		this.now = now;
	}

	public long getCloseTime() {
		return closeTime;
	}

	public void pause() {
		if (!isTicking)
			return;
		mTimer.cancel();
		mTimer = null;
		isTicking = false;
	}

	public void stop() {
		pause();
		now = 0;
		isBooted = false;
	}

	public void goOn() {
		if (isTicking)
			return;
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				now += 10;
				if (now % 1000 == 0)
					postEvent();

			}
		}, 100, 10);
		isTicking = true;
	}

	private boolean isBooted = false;

	public void boot(long nowInit) {
		if (isBooted)
			return;
		isBooted = true;
		now = nowInit;
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				now += 10;
				if (now % 1000 == 0)
					postEvent();
			}
		}, 100, 10);
		isTicking = true;

	}

	public long now() {
		return now;
	}

	public boolean isBooted() {
		return isBooted;
	}

	public boolean isTicking() {
		return isTicking;
	}

	private void postEvent() {
		if (now > closeTime) {
			stop();
			return;
		}
		if (isNeedSendTimeChangedEvent) {
			// 通过EventBus订阅者发送消息
			if (isMake) {
				EventBus.getDefault().post(new RecordTimeChangedEvent(now, closeTime));
			} else {
				EventBus.getDefault().post(new PlayTimeChangedEvent(now, closeTime));
			}
		}
	}

	public long totalTimeNow() {
		return now;
	}
}
