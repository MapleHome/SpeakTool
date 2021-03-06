package com.speaktool.bean;

/**
 * 搜索范围（课程记录 类型）
 *
 * @author shaoshuai
 */
public class SearchCategoryBean {
    public static final int CID_BAIDU_SEARCH = 1;
    public static final int CID_PIC_URL = 2;
    public static final int CID_ALL = -11;
    public static final int CID_USER_DEFINE = 22;

    private String categoryName;
    private int categoryId = CID_USER_DEFINE;


    public SearchCategoryBean(String categoryName, int categoryId) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof SearchCategoryBean))
            return false;
        SearchCategoryBean input = (SearchCategoryBean) o;

        return input.getCategoryName().equals(this.getCategoryName()) && input.getCategoryId() == this.getCategoryId();
    }

    @Override
    public int hashCode() {
        return getCategoryName().hashCode() + getCategoryId();
    }
}
