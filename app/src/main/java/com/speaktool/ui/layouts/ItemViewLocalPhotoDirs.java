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

@Layout(R.layout.item_photodir)
public class ItemViewLocalPhotoDirs extends FrameLayout implements IInject {
	@InjectView(R.id.ivDirIcon)
	private ImageView ivDirIcon;
	@InjectView(R.id.tvDirName)
	private TextView tvDirName;
	@InjectView(R.id.tvDirIncludeCounts)
	private TextView tvDirIncludeCounts;

	public ItemViewLocalPhotoDirs(Context context) {
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

	public void setDirIcon(Bitmap bmp) {

		ivDirIcon.setImageBitmap(bmp);
	}

	public void setDirName(String dirName) {
		tvDirName.setText(dirName);

	}

	public void setDirIncludeCounts(String includeCounts) {
		tvDirIncludeCounts.setText(includeCounts);

	}

}
