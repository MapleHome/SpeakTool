package com.speaktool.ui.player;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.speaktool.impl.player.PlayProcess;

/**
 * 播放服务
 *
 * @author Maple Shao
 */
public class PlayService extends Service {
    private static final int PLAY_NOTIFICATION_ID = 99;// 播放通知ID

    public static void killServiceProcess(Context context) {
        Intent it = new Intent(context, PlayService.class);
        context.stopService(it);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startForeground();
        super.onCreate();
    }

    private void startForeground() {
        try {
            Notification notification = new Notification();
            startForeground(PLAY_NOTIFICATION_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private void stopForeground() {
        try {
            stopForeground(true);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PlayProcess.doLogic(getApplicationContext(), intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
