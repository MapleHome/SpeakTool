package com.speektool.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.lidroid.xutils.ViewUtils;

/**
 * FrameLayout基类
 * 
 * @author shaoshuai
 * 
 */
public abstract class BaseFrameLayout extends FrameLayout {
	public View view;

	public BaseFrameLayout(Context context) {
		super(context);
		init();
	}

	public BaseFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BaseFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		view = View.inflate(getContext(), getContentViewResId(), this);
		ViewUtils.inject(this, view);
		initView();
	}

	protected abstract void initView();

	protected abstract int getContentViewResId();

}
