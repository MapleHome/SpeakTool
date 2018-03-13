package com.speaktool.bean;

public class MusicBean {

	private String name;
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != this.getClass())
			return false;
		MusicBean input = (MusicBean) o;
		return input.getPath().equals(this.path);
	}
}
