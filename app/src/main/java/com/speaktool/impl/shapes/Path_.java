package com.speaktool.impl.shapes;

import android.graphics.Path;

import com.speaktool.api.Page;

public class Path_ implements PenShape_ {

	private Path path;
	private int id;
	private int color;
	private int stokeWidth;
	private boolean isEraser = false;

	public Path_(Path path, int id, int color, int stokeWidth, boolean isEraser) {
		super();
		this.path = path;
		this.id = id;
		this.color = color;
		this.stokeWidth = stokeWidth;
		this.isEraser = isEraser;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
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
	public void deleteFromPage(Page page) {
		// board.view().undoImpl(this);
		page.unDraw(this);
	}

	@Override
	public void drawToPage(Page page) {
		page.drawOnBuffer(this);
	}

}
