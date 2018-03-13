package com.speaktool.bean;

import java.util.List;

public class LocalPhotoDirBean {

	private String dirIconPath;
	private String dirName;
	private int includeImageCounts;
	private List<String> imagePathList;

	public LocalPhotoDirBean(String dirIconPath, String dirName, int includeImageCounts, List<String> imagePathList) {
		super();
		this.dirIconPath = dirIconPath;
		this.dirName = dirName;
		this.includeImageCounts = includeImageCounts;
		this.imagePathList = imagePathList;
	}

	public String getDirIconPath() {
		return dirIconPath;
	}

	public void setDirIconPath(String dirIconPath) {
		this.dirIconPath = dirIconPath;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public int getIncludeImageCounts() {
		return includeImageCounts;
	}

	public void setIncludeImageCounts(int includeImageCounts) {
		this.includeImageCounts = includeImageCounts;
	}

	public List<String> getImagePathList() {
		return imagePathList;
	}

	public void setImagePathList(List<String> imagePathList) {
		this.imagePathList = imagePathList;
	}

}
