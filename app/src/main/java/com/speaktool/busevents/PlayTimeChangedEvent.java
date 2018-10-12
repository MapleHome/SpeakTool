package com.speaktool.busevents;

/**
 * 播放时间改变事件
 *
 * @author shaoshuai
 */
public class PlayTimeChangedEvent {
    private final long now;
    private final long closeTime;

    public PlayTimeChangedEvent(long now, long closeTime) {
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
