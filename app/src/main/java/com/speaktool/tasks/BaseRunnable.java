package com.speaktool.tasks;

import android.os.Handler;
import android.os.Looper;

/**
 * 基础线程类
 *
 * @param <Progress>
 * @param <Result>
 * @author shaoshuai
 */
public abstract class BaseRunnable<Progress, Result> implements Runnable, IRunnable<Progress, Result> {
    protected Handler uiHandler = new Handler();

    public BaseRunnable() {
        super();
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("BaseRunnable 必须在UI线程中创建。.");
        }
    }

    @Override
    public final void run() {
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                onPreExecute();
            }
        });

        final Result result = doBackground();

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute(result);
            }
        });
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onPostExecute(Result result) {
    }

    @Override
    public void onProgressUpdate(Progress... progress) {
    }

    @Override
    public final void publishProgress(final Progress... progress) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(progress);
            }
        });
    }

}
