package com.speaktool;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

/**
 * 讲讲APP
 *
 * @author shaoshuai
 */
public class SpeakToolApp extends Application {
    private static SpeakToolApp app;
    private static Handler sHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initPath();
    }

    public static SpeakToolApp app() {
        return app;
    }

    public static Handler getUiHandler() {
        return sHandler;
    }

    public void initPath() {

    }

}
