package com.speaktool.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ThreadPoolWrapper {
    private static final int THREAD_SLEEP_TIME = 500;
    private volatile boolean isShutdown = false;
    private final int threadMounts;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();

    private ThreadPoolWrapper(int threadMounts) {
        this.threadMounts = threadMounts;
        startPollThread();
    }

    public static ThreadPoolWrapper newThreadPool(int threadMounts) {
        return new ThreadPoolWrapper(threadMounts);
    }

    private void startPollThread() {
        for (int i = 0; i < threadMounts; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isShutdown) {
                        try {
                            workQueue.take().run();// take is @ThreadSafe
                            Thread.sleep(THREAD_SLEEP_TIME);
                            // give cpu time to other thread.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public void cancelAllWaitingTask() {
        workQueue.clear();
    }

    public void shutdownNow() {
        isShutdown = true;
        workQueue.clear();
    }

    public void execute(Runnable r) {
        try {
            // will not block,because workQueue has large space.
            workQueue.put(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
