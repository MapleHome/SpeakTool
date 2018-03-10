package com.speektool.ui.layouts;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.speektool.R;

public class CheckBoxImageView extends ImageView implements OnTouchListener, OnClickListener {

	public CheckBoxImageView(Context context) {
		super(context);
		init();
	}

	public CheckBoxImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CheckBoxImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.setImageResource(R.drawable.choose_s);
		this.setOnTouchListener(this);
		this.setOnClickListener(this);
	}

	private boolean isChecked = true;

	public void check() {
		if (isChecked)
			return;
		isChecked = true;
		this.setImageResource(R.drawable.choose_s);
	}

	public void uncheck() {
		if (!isChecked)
			return;
		isChecked = false;
		this.setImageResource(R.drawable.choose_n);

	}

	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.setColorFilter(Color.GRAY);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			this.setColorFilter(null);
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (isChecked())
			uncheck();
		else
			check();
	}
}
