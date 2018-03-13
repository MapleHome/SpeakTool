package com.speaktool.ui.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.bean.LocalPhotoDirBean;
import com.speaktool.ui.layouts.ItemViewLocalPhotoDirs;

public class AdapterPhotoDirs extends AbsAdapter<LocalPhotoDirBean> {

	public AdapterPhotoDirs(Context ctx, List<LocalPhotoDirBean> datas) {
		super(ctx, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ItemViewLocalPhotoDirs(mContext);
		}
		final ItemViewLocalPhotoDirs item = (ItemViewLocalPhotoDirs) convertView;
		LocalPhotoDirBean bean = (LocalPhotoDirBean) getItem(position);
		Bitmap initBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
		item.setDirIcon(initBmp);
		if (bean == null)
			return item;
		// Bitmap
		// bmp=BitmapScaleUtil.decodeSampledBitmapFromPath(bean.getDirIconPath(),
		// Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
		// if(bmp==null)
		// bmp=BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.error);
		// item.setDirIcon(bmp);
		item.setTag(bean.getDirIconPath());
		item.setDirName(bean.getDirName());
		item.setDirIncludeCounts(bean.getIncludeImageCounts() + "");
		return item;
	}

}
