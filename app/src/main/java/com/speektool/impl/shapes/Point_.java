package com.speektool.impl.shapes;

import com.speektool.api.Page;

public class Point_ implements PenShape_ {

	private float mX;
	private float mY;
	private int id;

	private int color;
	private int stokeWidth;

	private boolean isEraser = false;

	public Point_(float mX, float mY, int id, int color, int stokeWidth, boolean isEraser) {
		super();
		this.mX = mX;
		this.mY = mY;
		this.id = id;
		this.color = color;
		this.stokeWidth = stokeWidth;
		this.isEraser = isEraser;
	}

	public float getmX() {
		return mX;
	}

	public void setmX(float mX) {
		this.mX = mX;
	}

	public float getmY() {
		return mY;
	}

	public void setmY(float mY) {
		this.mY = mY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getStokeWidth() {
		return stokeWidth;
	}

	public void setStokeWidth(int stokeWidth) {
		this.stokeWidth = stokeWidth;
	}

	public boolean isEraser() {
		return isEraser;
	}

	public void setEraser(boolean isEraser) {
		this.isEraser = isEraser;
	}

	@Override
	public int getShapeID() {
		return id;
	}

	@Override
	public void drawToPage(Page page) {
		page.drawOnBuffer(this);
	}

	@Override
	public void deleteFromPage(Page page) {
		page.unDraw(this);
	}

}
