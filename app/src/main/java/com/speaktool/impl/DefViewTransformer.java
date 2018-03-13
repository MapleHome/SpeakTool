package com.speaktool.impl;

import android.view.View;
import android.widget.AbsoluteLayout.LayoutParams;

import com.speaktool.api.ViewTransformer;

/**
 * 默认视图 转换器
 * 
 * @author shaoshuai
 * 
 */
@SuppressWarnings("deprecation")
public class DefViewTransformer implements ViewTransformer {

	public DefViewTransformer() {
		super();
	}

	@Override
	public void scaleBy(float factorX, float factorY, View view) {
		float preScaleX = view.getScaleX();
		float preScaleY = view.getScaleY();

		view.setScaleX(factorX * preScaleX);
		view.setScaleY(factorY * preScaleY);
	}

	@Override
	public void rotateBy(float degree, View view) {
		float preRotation = view.getRotation();
		view.setRotation(degree + preRotation);
	}

	@Override
	public void rotateTo(float degree, View view) {
		view.setRotation(degree);
	}

	@Override
	public void scaleTo(float factorX, float factorY, View view) {
		view.setScaleX(factorX);
		view.setScaleY(factorY);
	}

	/**
	 * this impl just use to AbsoluteLayout.
	 */
	@Override
	public void MoveTo(int x, int y, View view) {
		LayoutParams lp = (LayoutParams) view.getLayoutParams();
		if (lp == null)
			lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, x, y);
		else {
			lp.x = x;
			lp.y = y;
		}
		view.setLayoutParams(lp);
	}

}
