package com.speaktool.bean;

public class Html5ImageInfoBean {

	private String path;
	private String resourceID;

	public Html5ImageInfoBean(String path, String resourceID) {
		this.path = path;
		this.resourceID = resourceID;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

}
