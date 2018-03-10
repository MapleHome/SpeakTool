package com.speektool.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.speektool.base.AbsAdapter;
import com.speektool.bean.SearchCategoryBean;
import com.speektool.ui.layouts.ItemViewRecordType;

/**
 * 课程记录类型
 * 
 * @author shaoshuai
 * 
 */
public class AdapterRecordTypes extends AbsAdapter<SearchCategoryBean> {

	public AdapterRecordTypes(Context ctx, List<SearchCategoryBean> datas) {
		super(ctx, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ItemViewRecordType(mContext);
		}
		ItemViewRecordType itemview = (ItemViewRecordType) convertView;
		String typename = ((SearchCategoryBean) getItem(position)).getCategoryName();
		if (typename == null)
			return itemview;
		itemview.setTypeName(typename);

		return itemview;
	}

}
