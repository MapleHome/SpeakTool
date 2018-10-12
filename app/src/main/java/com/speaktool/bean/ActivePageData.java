package com.speaktool.bean;

/**
 * 当前互动纸张属性Item
 *
 * @author shaoshuai
 */
public class ActivePageData {
    private int pageID;

    public ActivePageData() {
        super();
    }

    /**
     * 当前互动纸张属性Item
     *
     * @param pageID
     */
    public ActivePageData(int pageID) {
        super();
        this.pageID = pageID;
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

}
