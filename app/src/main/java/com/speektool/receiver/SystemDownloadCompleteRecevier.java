package com.speektool.receiver;

import java.io.File;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.speektool.manager.AppUpdateManager;
import com.speektool.utils.SPUtils;

/**
 * 系统下载完成-广播接受者
 * 
 * @author Maple Shao
 * 
 */
public class SystemDownloadCompleteRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获得完整的下载标识
		long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		long appDownloadId = SPUtils.getLong(AppUpdateManager.PREF_APP_DOWNLOAD_ID, 0);
		if (completeDownloadId != appDownloadId) {
			return;
		}
		// to do here
		DownloadManager.Query query = new DownloadManager.Query().setFilterById(completeDownloadId);
		DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		Cursor c = downloadManager.query(query);
		if (c == null) {
			Toast.makeText(context, "讲讲更新下载失败！", 0).show();
			return;
		}
		if (c.getCount() <= 0) {
			c.close();
			Toast.makeText(context, "讲讲更新下载失败！", 0).show();
			return;
		}
		c.moveToFirst();
		String filename = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME));
		String mimetype = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
		c.close();

		if (TextUtils.isEmpty(filename)) {
			Toast.makeText(context, "讲讲更新下载失败！", 0).show();
			return;

		}
		Uri path = Uri.parse(filename);
		// If there is no scheme, then it must be a file
		if (path.getScheme() == null) {
			path = Uri.fromFile(new File(filename));
		}
		Intent activityIntent = new Intent(Intent.ACTION_VIEW);

		activityIntent.setDataAndType(path, mimetype);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(activityIntent);
		} catch (ActivityNotFoundException ex) {
			Log.d("lich", "no activity for " + mimetype, ex);
		}

	}
};
