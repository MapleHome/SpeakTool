package com.handmark.pulltorefresh.library;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public interface ILoadingLayout {

	/**
	 * 设置在主标签下拉时最后更新的文本
	 * 
	 * @param label
	 *            - 设置标签
	 */
	public void setLastUpdatedLabel(CharSequence label);

	/**
	 * 设置可用于装载布局。这是相同的调用 <code>setLoadingDrawable(drawable, Mode.BOTH)</code>
	 * 
	 * @param drawable
	 *            - Drawable to display
	 */
	public void setLoadingDrawable(Drawable drawable);

	/**
	 * 设置窗口小部件显示时的文本 <code>setPullLabel(releaseLabel, Mode.BOTH)</code>
	 * 
	 * @param pullLabel
	 *            - 字符序列显示
	 */
	public void setPullLabel(CharSequence pullLabel);

	/**
	 * 设置为刷新窗口时显示的文本 <code>setRefreshingLabel(releaseLabel, Mode.BOTH)</code>
	 * 
	 * @param refreshingLabel
	 *            - CharSequence to display
	 */
	public void setRefreshingLabel(CharSequence refreshingLabel);

	/**
	 * 设置文本显示时，小部件被拉，并将刷新时释放。这是相同的调用
	 * <code>setReleaseLabel(releaseLabel, Mode.BOTH)</code>
	 * 
	 * @param releaseLabel
	 *            - CharSequence to display
	 */
	public void setReleaseLabel(CharSequence releaseLabel);

	/**
	 * Set's the Sets the typeface and style in which the text should be
	 * displayed. Please see
	 * {@link android.widget.TextView#setTypeface(Typeface)
	 * TextView#setTypeface(Typeface)}.
	 */
	public void setTextTypeface(Typeface tf);

}
