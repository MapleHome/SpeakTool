package com.speektool.bean;

import com.speektool.api.Page.Page_BG;

public class PageBackgroundData {

	private Page_BG backgroundType;
	private int pageID;

	public Page_BG getBackgroundType() {
		return backgroundType;
	}

	public void setBackgroundType(Page_BG backgroundType) {
		this.backgroundType = backgroundType;
	}

	public int getPageID() {
		return pageID;
	}

	public void setPageID(int pageID) {
		this.pageID = pageID;
	}

}
