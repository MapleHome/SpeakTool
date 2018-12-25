package com.speaktool.impl.bean;

import com.speaktool.impl.api.Page.Page_BG;

public class PageBackgroundData {

    private Page_BG backgroundType;
    private int pageID;

    public PageBackgroundData(int pageID, Page_BG backgroundType) {
        this.pageID = pageID;
        this.backgroundType = backgroundType;
    }


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
