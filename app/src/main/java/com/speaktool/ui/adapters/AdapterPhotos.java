package com.speaktool.ui.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.google.common.collect.Lists;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.ui.layouts.MulticheckableView;

public class AdapterPhotos extends AbsAdapter<String> {
	private List<Integer> mCheckedItemIndexs = Lists.newArrayList();

	public AdapterPhotos(Context ctx, List<String> datas) {
		super(ctx, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new MulticheckableView(mContext);
			int pw = parent.getRootView().getWidth();
			int h = pw / 4;
			((MulticheckableView) convertView).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					h));
		}
		final MulticheckableView item = (MulticheckableView) convertView;
		String imagePath = (String) getItem(position);
		if (imagePath == null)
			return item;

		item.setTag(imagePath);
		item.setLoading();
		//
		if (mCheckedItemIndexs.contains(position)) {
			item.check();
		} else {
			item.uncheck();
		}
		return item;
	}

	public List<Integer> getCheckedList() {
		return mCheckedItemIndexs;
	}
}
