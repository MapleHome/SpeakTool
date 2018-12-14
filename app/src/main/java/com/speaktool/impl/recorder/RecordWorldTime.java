package com.speaktool.impl.recorder;

import com.speaktool.busevents.RecordTimeChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 时间记录器
 * @author maple
 * @time 2018/12/10
 */
public class RecordWorldTime {
    private long now = 0;// millsec.
    private boolean isNeedSendTimeChangedEvent = false;
    private boolean isRunning = false;

    private Timer mTimer;

    public RecordWorldTime(long nowInit, boolean isChangedUI) {
        now = nowInit;
        this.isNeedSendTimeChangedEvent = isChangedUI;
    }

    public void goRun() {
        if (isRunning)
            return;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                now += 10;
                if (now % 1000 == 0) { // 每秒更新一次UI
                    if (isNeedSendTimeChangedEvent) {
                        EventBus.getDefault().post(new RecordTimeChangedEvent(now));
                    }
                }
            }
        }, 100, 10);
        isRunning = true;
    }

    public void pause() {
        if (!isRunning)
            return;
        mTimer.cancel();
        mTimer = null;
        isRunning = false;
    }

    public void stop() {
        pause();
        now = 0;
    }

    // 获取当前时间
    public long now() {
        return now;
    }

//    // 是否计时
//    public boolean isRunning() {
//        return isRunning;
//    }

//    public void boot(long nowInit) {
//        if (isBooted)
//            return;
//        isBooted = true;
//    }

//    // 是否启动
//    public boolean isBooted() {
//        return isBooted;
//    }

//	public RecordWorldTime(boolean isNeedSendTimeChangedEvent, long closeTime, boolean isMake) {
//		super();
//		this.isNeedSendTimeChangedEvent = isNeedSendTimeChangedEvent;
//		this.closeTime = closeTime;
//		this.isMake = isMake;
//	}

//    public void setNowTime(long now) {
//        this.now = now;
//    }

//	public long getCloseTime() {
//		return closeTime;
//	}

//    private void postEvent() {
//        long closeTime = Long.MAX_VALUE;
//        if (now > closeTime) {
//            stop();
//        } else {
//        if (isNeedSendTimeChangedEvent) {
//            EventBus.getDefault().post(new RecordTimeChangedEvent(now));
//        }
//        }
//    }
}
