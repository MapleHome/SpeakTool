package com.speaktool.bean;

/**
 * 清除页面数据
 *
 * @author shaoshuai
 */
public class ClearPageData {
    public static final String OPT_CLEAR_ALL = "all";// 清除所有
    public static final String OPT_CLEAR_NOTES = "notes";// 清除笔记

    private int pageId;// 页面ID
    private String option;// 清除类型

    public ClearPageData(int pageId, String option) {
        this.pageId = pageId;
        this.option = option;
    }

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
