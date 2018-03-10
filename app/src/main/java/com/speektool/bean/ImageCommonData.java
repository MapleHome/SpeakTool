package com.speektool.bean;

import com.speektool.utils.FormatUtils;

public class ImageCommonData extends TransformShapeData {
	public static final int ALPHA_FACTOR = 10000;
	public static final int SCALE_FACTOR = 10000;
	private float alpha;
	private int rotation;
	private float scale;
	private String type = "image";// text,image,pen.

	private PositionData positionData;
	//

	private String resourceID;

	//

	private static float formatFloat(float f) {
		return FormatUtils.formatFloat(f);

	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = formatFloat(alpha);
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = formatFloat(scale);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PositionData getPosition() {
		return positionData;
	}

	public void setPosition(PositionData positionData) {
		this.positionData = positionData;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public ImageCommonData copy() {

		ImageCommonData copy = new ImageCommonData();
		copy.setAlpha(alpha);
		copy.setPosition(positionData);
		copy.setResourceID(resourceID);
		copy.setRotation(rotation);
		copy.setScale(scale);
		copy.setShapeID(getShapeID());
		copy.setType(type);

		return copy;
	}

}
