package com.speaktool.ui.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.bean.PaintInfoBean;

public class AdapterColors extends AbsAdapter<PaintInfoBean> {

	public AdapterColors(Context ctx, List<PaintInfoBean> datas) {
		super(ctx, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageView(mContext);
			((ImageView) convertView).setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
		}
		final ImageView item = (ImageView) convertView;
		PaintInfoBean bean = (PaintInfoBean) getItem(position);
		if (bean == null)
			return item;
		item.setImageResource(bean.getIconResId());
		return item;
	}

}
