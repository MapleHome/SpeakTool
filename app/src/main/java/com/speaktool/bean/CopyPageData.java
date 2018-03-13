package com.speaktool.bean;

/**
 * 复制页面数据
 * 
 * @author shaoshuai
 * 
 */
public class CopyPageData {

	public static final String OPT_COPY_ALL = "all";
	public static final String OPT_COPY_VIEWS = "views";
	/** 源页面ID */
	private int srcPageId;
	/** 目标页面ID */
	private int destPageId;
	/** 操作类型 */
	private String option;

	public int getSrcPageId() {
		return srcPageId;
	}

	public void setSrcPageId(int srcPageId) {
		this.srcPageId = srcPageId;
	}

	public int getDestPageId() {
		return destPageId;
	}

	public void setDestPageId(int destPageId) {
		this.destPageId = destPageId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

}
