package com.speektool;

import android.os.Handler;

import com.smart.pen.core.PenApplication;
import com.speektool.bean.LocalPhotoDirBean;
import com.speektool.bean.UserBean;
import com.speektool.dao.UserDatabase;
import com.speektool.exception.SpeakToolUncaughtExceptionHandler;
import com.speektool.tasks.TaskLoadLocalPhotos;
import com.speektool.tasks.TaskLoadLocalPhotos.LoadLocalPhotosCallback;

import java.util.List;

/**
 * 讲讲APP
 *
 * @author shaoshuai
 */
public class SpeekToolApp extends PenApplication implements LoadLocalPhotosCallback {

    private static SpeekToolApp app;
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

    public static SpeekToolApp app() {
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
