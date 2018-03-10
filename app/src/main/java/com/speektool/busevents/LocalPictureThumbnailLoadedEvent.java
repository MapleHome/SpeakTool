package com.speektool.busevents;

import android.graphics.drawable.Drawable;

/**
 * 本地图片缩略图加载事件
 * 
 * @author shaoshuai
 * 
 */
public class LocalPictureThumbnailLoadedEvent {
	private String key;
	private Drawable icon;
	private boolean isError = false;

	public LocalPictureThumbnailLoadedEvent(String key, Drawable icon,
			boolean isError) {
		super();
		this.key = key;
		this.icon = icon;
		this.isError = isError;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public boolean isError() {
		return isError;
	}

}
