package com.speaktool.ui.layouts;

import roboguice.inject.InjectView;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.injectmodules.IInject;
import com.speaktool.injectmodules.Layout;
import com.speaktool.injectmodules.ViewInjectUtils;

@Layout(R.layout.item_thirdparty)
public class ItemViewThirdparty extends FrameLayout implements IInject {
	@InjectView(R.id.ivLogo)
	private ImageView ivLogo;
	@InjectView(R.id.tvName)
	private TextView tvName;

	public ItemViewThirdparty(Context context) {
		super(context);
		init();
	}

	private void init() {
		startInject();
		afterInject();
	}

	@Override
	public void startInject() {
		ViewInjectUtils.injectViews(this);
	}

	@Override
	public void afterInject() {
	}

	public void setLogo(Bitmap bmp) {
		ivLogo.setImageBitmap(bmp);
	}

	public void setName(String name) {
		tvName.setText(name);
	}

}
