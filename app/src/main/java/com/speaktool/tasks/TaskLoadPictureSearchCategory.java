package com.speaktool.tasks;

import java.util.List;

import android.content.Context;

import com.google.common.collect.Lists;
import com.speaktool.bean.SearchCategoryBean;

public class TaskLoadPictureSearchCategory {

	public static List<SearchCategoryBean> getCategories(Context ctx) {

		List<SearchCategoryBean> ret = Lists.newArrayList();
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
