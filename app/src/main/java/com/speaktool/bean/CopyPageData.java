package com.speaktool.bean;

/**
 * 复制页面数据
 *
 * @author shaoshuai
 */
public class CopyPageData {
    public static final String OPT_COPY_ALL = "all";
    public static final String OPT_COPY_VIEWS = "views";

    private int srcPageId;// 源页面ID
    private int destPageId;// 目标页面ID
    private String option;// 操作类型

    public CopyPageData(int srcPageId, int destPageId, String option) {
        this.srcPageId = srcPageId;
        this.destPageId = destPageId;
        this.option = option;
    }

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
