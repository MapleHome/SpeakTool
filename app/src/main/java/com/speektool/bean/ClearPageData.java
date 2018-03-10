package com.speektool.bean;

/**
 * 清除页面数据
 * 
 * @author shaoshuai
 * 
 */
public class ClearPageData {
	/** 清除所有 */
	public static final String OPT_CLEAR_ALL = "all";
	/** 清除笔记 */
	public static final String OPT_CLEAR_NOTES = "notes";

	/** 页面ID */
	private int pageId;
	/** 清除类型 */
	private String option;

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

}
