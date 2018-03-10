package com.speektool.bean;

/**
 * 画笔
 * 
 * @author shaoshuai
 * 
 */
public class PaintInfoBean {

	private int color;// 颜色
	private int iconResId;// 默认背景
	private int iconResIdSelected;// 选中背景
	private int strokeWidth;// 笔迹粗细 px

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	/** 获取笔迹宽度pix */
	public int getStrokeWidth() {
		return strokeWidth;
	}

	/** 设置笔迹宽度pix */
	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getIconResIdSelected() {
		return iconResIdSelected;
	}

	public void setIconResIdSelected(int iconResIdSelected) {
		this.iconResIdSelected = iconResIdSelected;
	}

}
