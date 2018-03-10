package com.speektool.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.speektool.R;
import com.speektool.base.AbsAdapter;
import com.speektool.bean.ThirdParty;
import com.speektool.ui.layouts.ItemViewThirdparty;
import com.speektool.utils.BitmapScaleUtil;

/**
 * 第三方平台列表
 * 
 * @author shaoshuai
 * 
 */
public class AdapterThirdpartyList extends AbsAdapter<ThirdParty> {
	private Bitmap defbmp;

	public AdapterThirdpartyList(Context ctx, List<ThirdParty> datas) {
		super(ctx, datas);
		defbmp = BitmapScaleUtil.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.defalut_icon,
				1024 * 1024);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = new ItemViewThirdparty(mContext);
		}
		ThirdParty bean = (ThirdParty) getItem(position);
		if (bean == null) {
			return convertView;
		}
		ItemViewThirdparty item = (ItemViewThirdparty) convertView;
		item.setLogo(defbmp);
		item.setName(bean.getName());
		// set tag.
		if (bean.getIconType() == ThirdParty.ICON_TYPE_RES) {
			item.setTag(bean.getIconResId());
		} else if (bean.getIconType() == ThirdParty.ICON_TYPE_NET) {
			item.setTag(bean.getIconUrl());
		}

		return item;
	}

	public Bitmap getDefBmp() {
		return defbmp;
	}
}
