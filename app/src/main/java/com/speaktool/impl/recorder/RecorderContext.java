package com.speaktool.impl.recorder;

import com.speaktool.busevents.RecordRunEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * 记录器上下文
 *
 * @author shaoshuai
 */
public class RecorderContext {
    /**
     * 是否正在运行
     */
    private boolean isRunning = false;
    /**
     * 是否启动
     */
    private boolean isBooted = false;

    /**
     * 是否启动
     */
    public boolean isBooted() {
        return isBooted;
    }

    /**
     * 是否运行
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 启动-记录器
     */
    public void boot() {
        if (isBooted)
            return;
        isRunning = true;
        isBooted = true;
        // 通过EventBus订阅者发送消息
        EventBus.getDefault().post(new RecordRunEvent(true));
    }

    /**
     * 暂停-记录器
     */
    public void pause() {
        if (!isRunning)
            return;
        isRunning = false;
        // 通过EventBus订阅者发送消息
        EventBus.getDefault().post(new RecordRunEvent(false));
    }

    /**
     * 继续-记录器
     */
    public void continuing() {
        if (isRunning)
            return;
        isRunning = true;
        // 通过EventBus订阅者发送消息
        EventBus.getDefault().post(new RecordRunEvent(true));

    }

    /**
     * 停止-记录器
     */
    public void stop() {
        if (!isBooted)
            return;
        isRunning = false;
        isBooted = false;
        // 通过EventBus订阅者发送消息
        EventBus.getDefault().post(new RecordRunEvent(false));
    }

}
