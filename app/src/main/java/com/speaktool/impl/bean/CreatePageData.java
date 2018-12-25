package com.speaktool.impl.bean;

import com.speaktool.impl.api.Page.Page_BG;

/**
 * 画板纸张属性
 *
 * @author shaoshuai
 */
public class CreatePageData {
    private int pageID;// 纸张ID
    private int position;// 纸张索引
    private Page_BG backgroundType; // 纸张背景


    /**
     * 画板纸张属性
     *
     * @param pageID         - 纸张ID
     * @param position       - 在画册中的索引
     * @param backgroundType - 纸张背景
     */
    public CreatePageData(int pageID, int position, Page_BG backgroundType) {
        this.pageID = pageID;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
