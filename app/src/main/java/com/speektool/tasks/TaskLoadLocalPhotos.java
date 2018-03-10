package com.speektool.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.speektool.SpeekToolApp;
import com.speektool.bean.LocalPhotoDirBean;
import com.speektool.utils.FileUtil;

public class TaskLoadLocalPhotos extends BaseRunnable<Integer, Void> {

	public static interface LoadLocalPhotosCallback {
		void onFinish(List<LocalPhotoDirBean> dirs);
	}

	private static final String tag = TaskLoadLocalPhotos.class.getSimpleName();
	private final WeakReference<LoadLocalPhotosCallback> mListener;

	public TaskLoadLocalPhotos(LoadLocalPhotosCallback listener) {
		mListener = new WeakReference<LoadLocalPhotosCallback>(listener);
	}

	@Override
	public Void doBackground() {
		getPhotoDirs(SpeekToolApp.app());
		return null;
	}

	private static Map<String, List<String>> getPhotoInfos(Context ctx) {
		String columns[] = new String[] { Media.DATA };
		Cursor cursor = ctx.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null, "_id desc");
		if (cursor == null)
			return null;
		if (cursor.getCount() < 1) {
			cursor.close();
			return null;
		}

		Map<String, List<String>> map = Maps.newHashMap();

		while (cursor.moveToNext()) {
			String imgpath = cursor.getString(0);
			if (TextUtils.isEmpty(imgpath)) {
				continue;

			}
			File f = new File(imgpath);
			if (!f.exists()) {
				continue;
			}
			String type = FileUtil.getMimeType(imgpath);
			if (type == null || !type.startsWith("image/")) {
				continue;
			}

			int lastSepIndex = imgpath.lastIndexOf(File.separator);
			String dir = imgpath.substring(0, lastSepIndex);
			lastSepIndex = dir.lastIndexOf(File.separator);
			dir = dir.substring(lastSepIndex + 1);

			List<String> list = map.get(dir);
			if (list != null)
				list.add(imgpath);
			else {
				List<String> newlist = Lists.newArrayList();
				newlist.add(imgpath);
				map.put(dir, newlist);
			}

		}
		cursor.close();
		return map;

	}

	private synchronized void getPhotoDirs(Context ctx) {
		if (sCachedPhotoDirs != null) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					LoadLocalPhotosCallback listener = mListener.get();
					if (null != listener) {
						listener.onFinish(sCachedPhotoDirs);
					}
				}
			});
			return;
		}

		Map<String, List<String>> map = getPhotoInfos(ctx);
		if (map == null || map.isEmpty()) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					LoadLocalPhotosCallback listener = mListener.get();
					if (null != listener) {
						listener.onFinish(null);
					}
				}
			});
			return;
		}
		Set<String> dirNames = map.keySet();
		Iterator<String> dirIte = dirNames.iterator();
		List<LocalPhotoDirBean> dirs = Lists.newArrayList();
		while (dirIte.hasNext()) {
			String dirName = dirIte.next();
			String dirIconPath = map.get(dirName).get(0);
			int includeImageCounts = map.get(dirName).size();
			LocalPhotoDirBean bean = new LocalPhotoDirBean(dirIconPath, dirName, includeImageCounts, map.get(dirName));
			dirs.add(bean);
		}
		sCachedPhotoDirs = dirs;
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				LoadLocalPhotosCallback listener = mListener.get();
				if (null != listener) {
					listener.onFinish(sCachedPhotoDirs);
				}
			}
		});
	}

	private static List<LocalPhotoDirBean> sCachedPhotoDirs;

}
