package com.speaktool.tasks;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {

    private String mThreadName;

    public MyThreadFactory(String threadName) {
        mThreadName = threadName;
    }

    public MyThreadFactory() {
        this(null);
    }

    @Override
    public Thread newThread(final Runnable r) {
        if (null != mThreadName) {
            Thread t = new Thread(r, mThreadName);
            t.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            return t;
        } else {
            Thread t = new Thread(r);
            t.setPriority(7);
            return t;
        }
    }
}