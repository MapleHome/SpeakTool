package com.speektool.busevents;

import android.graphics.Bitmap;

public class ThirdpartyLoadEvent {
	private final Object key;
	private final Bitmap val;

	public ThirdpartyLoadEvent(Object key, Bitmap val) {
		super();
		this.key = key;
		this.val = val;
	}

	public Object getKey() {
		return key;
	}

	public Bitmap getVal() {
		return val;
	}

}
