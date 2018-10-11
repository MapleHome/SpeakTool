package com.speaktool.tasks;

import android.content.Context;

import com.speaktool.bean.SearchCategoryBean;

import java.util.ArrayList;
import java.util.List;


public class TaskLoadPictureSearchCategory {

    public static List<SearchCategoryBean> getCategories(Context ctx) {

        List<SearchCategoryBean> ret = new ArrayList<>();
        SearchCategoryBean type = new SearchCategoryBean();
        type.setCategoryId(SearchCategoryBean.CID_BAIDU_SEARCH);
        type.setCategoryName("网络搜索");
        ret.add(type);
        //
        type = new SearchCategoryBean();
        type.setCategoryId(SearchCategoryBean.CID_PIC_URL);
        type.setCategoryName("网址搜索");
        ret.add(type);
        //
        return ret;

    }

    public static SearchCategoryBean getDefCategory(Context ctx) {
        SearchCategoryBean type = new SearchCategoryBean();
        type.setCategoryId(SearchCategoryBean.CID_BAIDU_SEARCH);
        type.setCategoryName("网络搜索");
        return type;

    }

}
