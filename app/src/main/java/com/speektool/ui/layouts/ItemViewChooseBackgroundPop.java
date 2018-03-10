package com.speektool.ui.layouts;

import roboguice.inject.InjectView;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speektool.R;
import com.speektool.injectmodules.IInject;
import com.speektool.injectmodules.Layout;
import com.speektool.injectmodules.ViewInjectUtils;

/**
 * 更换背景 Item
 * 
 * @author shaoshuai
 * 
 */
@Layout(R.layout.item_choosebackground_pop)
public class ItemViewChooseBackgroundPop extends FrameLayout implements IInject {

	@InjectView(R.id.ivIcon)
	private ImageView ivIcon;// 图片
	@InjectView(R.id.tvNote)
	private TextView tvNote;// 说明
	@InjectView(R.id.ivChooseState)
	private ImageView ivChooseState;// 是否选中

	private String note = "";
	private int iconResid;
	private boolean isCheck = false;

	public ItemViewChooseBackgroundPop(Context context) {
		super(context);
		init();
	}

	public ItemViewChooseBackgroundPop(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.itemSelectBg);
		note = a.getString(R.styleable.itemSelectBg_itemSelectBg_note);
		iconResid = a.getResourceId(R.styleable.itemSelectBg_itemSelectBg_icon, -1);
		isCheck = a.getBoolean(R.styleable.itemSelectBg_itemSelectBg_isCheck, false);
		a.recycle();
		init();
	}

	public ItemViewChooseBackgroundPop(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.itemSelectBg);
		note = a.getString(R.styleable.itemSelectBg_itemSelectBg_note);
		iconResid = a.getResourceId(R.styleable.itemSelectBg_itemSelectBg_icon, -1);
		isCheck = a.getBoolean(R.styleable.itemSelectBg_itemSelectBg_isCheck, false);
		a.recycle();
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
		if (iconResid != -1)
			ivIcon.setImageResource(iconResid);
		if (!TextUtils.isEmpty(note))
			tvNote.setText(note);
		setCheckState(isCheck);

	}

	public void setCheckState(boolean isChecked) {
		if (isChecked)
			ivChooseState.setVisibility(View.VISIBLE);
		else
			ivChooseState.setVisibility(View.INVISIBLE);
	}
}
