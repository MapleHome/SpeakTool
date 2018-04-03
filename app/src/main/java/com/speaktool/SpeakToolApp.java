package com.speaktool;

import android.app.Application;
import android.os.Handler;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


@ReportsCrashes(
        mode = ReportingInteractionMode.DIALOG,
        mailTo = "maple.shao@everbridge.com",
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = R.drawable.ic_launcher,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        // resDialogTheme = R.style.AppTheme_Dialog,
        resDialogOkToast = R.string.crash_dialog_ok_toast
)
public class SpeakToolApp extends Application {
    private static SpeakToolApp app;
    private static Handler sHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
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
