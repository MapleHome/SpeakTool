package com.speaktool.busevents;

/**
 * @author maple
 * @time 2018/5/18.
 */
public class UndoEvent {

    public boolean enable;

    public UndoEvent(boolean eventType) {
        this.enable = eventType;
    }

}
