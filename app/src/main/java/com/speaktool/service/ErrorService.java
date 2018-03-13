package com.speaktool.service;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.util.Arrays;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.speaktool.Const;
import com.speaktool.SpeakToolApp;
import com.speaktool.tasks.TaskUploadException;
import com.speaktool.tasks.TaskUploadException.UploadExceptionCallback;
import com.speaktool.utils.ZipUtils;

public class ErrorService extends Service {
	private static final int NOTIFICATION_ID = 10;
	public static final String EXTRA_EXCEPTION_OBJ = "extra_exception_obj";

	private static final String RECENT_EXCEPTION_FILE_NAME = "recentException.txt";

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

	private UploadExceptionCallback mUploadExceptionCallback = new UploadExceptionCallback() {

		@Override
		public void onUploadSuccess() {
			deleteOldExceptionFiles();
			stopSelf();
		}

		@Override
		public void onUploadFail() {
			stopSelf();
		}

		@Override
		public void onConnectFail() {
			stopSelf();
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final Throwable ex = (Throwable) intent.getSerializableExtra(EXTRA_EXCEPTION_OBJ);
		new Thread(new Runnable() {

			@Override
			public void run() {
				savaRecentException(ex, RECENT_EXCEPTION_FILE_NAME);
				// if (false) {
				// stopSelf();
				// return;
				// }
				addExceptionFile(ex);
				final File zip = zipException();
				if (zip != null) {
					SpeakToolApp.getUiHandler().post(new Runnable() {

						@Override
						public void run() {
							final TaskUploadException lTaskUploadException = new TaskUploadException(
									mUploadExceptionCallback, zip);
							new Thread(new Runnable() {

								@Override
								public void run() {
									lTaskUploadException.run();
								}
							});
						}
					});
				} else {
					stopSelf();
				}
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		stopForeground();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();

	}

	private static File zipException() {
		try {
			File zip = new File(Const.ERR_DIR, System.currentTimeMillis() + ".zip");
			if (zip.exists()) {
				zip.delete();
			}
			File[] expfiles = getUploadFiles(Const.ERR_DIR);
			if (expfiles == null || expfiles.length == 0)
				return null;

			/**
			 * must after listfiles,otherwise the zip will be zipped.
			 */
			zip.createNewFile();//
			ZipUtils.zipFiles(Arrays.asList(expfiles), zip);
			/**
			 * TODO upload zip to server,should delete all old files when upload
			 * success.
			 */
			return zip;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static File[] getUploadFiles(String spklExpDir) {
		File dir = new File(spklExpDir);
		File[] uploadFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().contains(".zip") || pathname.getName().equals(RECENT_EXCEPTION_FILE_NAME))
					return false;
				else
					return true;
			}
		});
		return uploadFiles;
	}

	private static void addExceptionFile(final Throwable ex) {
		savaRecentException(ex, System.currentTimeMillis() + "");
	}

	private static void savaRecentException(final Throwable ex, String expFileName) {

		try {
			File dir = new File(Const.ERR_DIR);
			if (!dir.exists())
				dir.mkdirs();
			File tempf = new File(dir, expFileName);

			if (!tempf.exists())
				tempf.createNewFile();

			PrintWriter pw = new PrintWriter(tempf);
			pw.write("COMPANY:" + Build.MANUFACTURER + "\n");
			pw.write("MODEL:" + Build.MODEL + "\n");

			pw.write("SDK_INT:" + Build.VERSION.SDK + "\n");
			pw.write("SDK_NAME:" + Build.VERSION.RELEASE + "\n");
			ex.printStackTrace(pw);
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				File tempf = new File(SpeakToolApp.app().getApplicationInfo().dataDir, expFileName);
				if (!tempf.exists())
					tempf.createNewFile();
				PrintWriter pw = new PrintWriter(tempf);
				ex.printStackTrace(pw);
				pw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private static void deleteOldExceptionFiles() {
		File dir = new File(Const.ERR_DIR);
		File[] uploadFiles = dir.listFiles();
		if (uploadFiles == null || uploadFiles.length < 1)
			return;
		for (File f : uploadFiles) {
			if (!f.getName().equals(RECENT_EXCEPTION_FILE_NAME))
				f.delete();
		}
	}

}
