package com.speektool.ui.layouts;

import roboguice.inject.InjectView;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.speektool.R;
import com.speektool.injectmodules.IInject;
import com.speektool.injectmodules.Layout;
import com.speektool.injectmodules.ViewInjectUtils;

/**
 * 记录类型
 * 
 * @author Maple Shao
 * 
 */
@Layout(R.layout.item_record_type)
public class ItemViewRecordType extends FrameLayout implements IInject {

	@InjectView(R.id.tvTypeName)
	private TextView tvTypeName;// 类型名称

	public ItemViewRecordType(Context context) {
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

	public void setTypeName(String typeName) {
		tvTypeName.setText(typeName);
	}
}