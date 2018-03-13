package com.speaktool.bean;

public class PositionData {
	private int x;
	private int y;

	public void setY(int y) {
		this.y = y;
	}

	public PositionData(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

}
