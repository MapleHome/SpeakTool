package com.speaktool.api;

/**
 * 播放模式
 *
 * @author maple
 * @time 2018/12/15
 */
public interface Play extends BaseDraw {

    void resetAllViews();

    void onPlayComplete();

    void onPlayStart();

    void removeAllHandlerTasks();

}
