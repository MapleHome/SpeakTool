package com.speaktool.busevents;

import android.graphics.Bitmap;

/**
 * 加载课程记录缩略图
 * 
 * @author shaoshuai
 * 
 */
public class CourseThumbnailLoadedEvent {
	private String key;
	private Bitmap icon;

	public CourseThumbnailLoadedEvent(String key, Bitmap icon) {
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
