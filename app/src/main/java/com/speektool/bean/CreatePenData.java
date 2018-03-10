package com.speektool.bean;

import java.util.List;

import com.google.common.collect.Lists;
import com.speektool.utils.FormatUtils;

public class CreatePenData {

	private int shapeID;
	private float alpha;
	private PositionData maxXY;
	private PositionData minXY;
	private String type = "pen";// eraser.

	private String strokeColor;
	private int strokeWidth;
	private List<MoveData> points;

	//

	private static float formatFloat(float f) {
		return FormatUtils.formatFloat(f);

	}

	public int getShapeID() {
		return shapeID;
	}

	public void setShapeID(int shapeID) {
		this.shapeID = shapeID;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = formatFloat(alpha);
	}

	public PositionData getMaxXY() {
		return maxXY;
	}

	public void setMaxXY(PositionData maxXY) {
		this.maxXY = maxXY;
	}

	public PositionData getMinXY() {
		return minXY;
	}

	public void setMinXY(PositionData minXY) {
		this.minXY = minXY;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public List<MoveData> getPoints() {
		return points;
	}

	public void setPoints(List<MoveData> points) {
		this.points = points;
	}

	public CreatePenData copy() {
		CreatePenData copy = new CreatePenData();
		copy.setAlpha(getAlpha());
		copy.setMaxXY(getMaxXY());
		copy.setMinXY(getMinXY());
		copy.setShapeID(getShapeID());
		copy.setStrokeColor(getStrokeColor());
		copy.setType(getType());
		copy.setStrokeWidth(getStrokeWidth());

		if (points == null)
			return copy;

		List<MoveData> pointsNew = Lists.newArrayList();
		for (int i = 0; i < points.size(); i++) {
			MoveData p = points.get(i);
			pointsNew.add(new MoveData(0, p.getX(), p.getY()));
			// note:dont use getscaledx,because will scale.
		}
		copy.setPoints(pointsNew);
		return copy;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

}