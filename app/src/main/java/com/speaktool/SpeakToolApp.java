package com.speaktool;

import android.app.Application;
import android.os.Handler;

import com.speaktool.bean.LocalPhotoDirBean;
import com.speaktool.bean.UserBean;
import com.speaktool.dao.UserDatabase;
import com.speaktool.service.SpeakToolUncaughtExceptionHandler;
import com.speaktool.tasks.TaskLoadLocalPhotos;
import com.speaktool.tasks.TaskLoadLocalPhotos.LoadLocalPhotosCallback;

import java.util.List;

/**
 * 讲讲APP
 *
 * @author shaoshuai
 */
public class SpeakToolApp extends Application implements LoadLocalPhotosCallback {

    private static SpeakToolApp app;
    private static Handler sHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Thread.setDefaultUncaughtExceptionHandler(new SpeakToolUncaughtExceptionHandler());
        initLocalPhoto();

    }

    private void initLocalPhoto() {
        new Thread(new TaskLoadLocalPhotos(this)).start();
    }

    public static SpeakToolApp app() {
        return app;
    }

    public static String getUid() {
        String uid = "";
        UserBean session = UserDatabase.getUserLocalSession(app);
        if (session != null && session.getLoginState() == UserBean.STATE_IN) {
            uid = session.getId();
        }
        return uid;
    }

    public static Handler getUiHandler() {
        return sHandler;
    }

    @Override
    public void onFinish(List<LocalPhotoDirBean> dirs) {
        // ignore,just use to preload.

    }

}
