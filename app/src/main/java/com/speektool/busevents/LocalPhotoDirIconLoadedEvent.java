package com.speektool.busevents;

import android.graphics.Bitmap;

/**
 * 本地照片目录 图标加载事件
 * 
 * @author shaoshuai
 * 
 */
public class LocalPhotoDirIconLoadedEvent {
	private String key;
	private Bitmap icon;

	public LocalPhotoDirIconLoadedEvent(String key, Bitmap icon) {
		super();
		this.key = key;
		this.icon = icon;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

}
