package com.speaktool.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class KeepAliveService extends Service {
	private static final int NOTIFICATION_ID = 88;

	public static void stop(Context context) {
		Intent it = new Intent(context, KeepAliveService.class);
		context.stopService(it);

	}

	public static void start(Context context) {
		Intent it = new Intent(context, KeepAliveService.class);
		context.startService(it);
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
			final Notification notification = new Notification();
			startForeground(NOTIFICATION_ID, notification);
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
		Log.e("lich", "KeepAliveService 开启>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		stopForeground();
		Log.e("lich", "KeepAliveService 销毁>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		super.onDestroy();

	}

}
