package com.speaktool.ui.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.bean.SearchCategoryBean;

/**
 * 搜索课程记录类型
 * 
 * @author shaoshuai
 * 
 */
public class AdapterSearchRecordTypes extends AbsAdapter<SearchCategoryBean> {

	public AdapterSearchRecordTypes(Context ctx, List<SearchCategoryBean> datas) {
		super(ctx, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_search_dropdowntypes, null);
		}

		String typename = ((SearchCategoryBean) getItem(position)).getCategoryName();
		if (typename == null)
			return convertView;
		TextView tv = (TextView) convertView.findViewById(R.id.tvType);
		tv.setText(typename);

		return convertView;
	}

}
