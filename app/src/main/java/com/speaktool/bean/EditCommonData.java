package com.speaktool.bean;

import com.speaktool.utils.FormatUtils;

public class EditCommonData extends TransformShapeData {

    private float alpha;
    private int rotation;
    private float scale;
    private String type = "text";// text,image,pen.

    private String text;
    private String color;
    private int fontSize;

    private PositionData positionData;

    public void bianHuan(float fx, float fy) {
        this.positionData.bianHuan(fx, fy);
        this.fontSize = (int) (Math.min(fx, fy) * fontSize);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = FormatUtils.formatFloat(alpha);
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
        this.scale = FormatUtils.formatFloat(scale);
    }

    public String getType() {
        return type;
    }

    // public void setType(String type) {
    // this.type = type;
    // }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PositionData getPosition() {
        return positionData;
    }

    public void setPosition(PositionData positionData) {
        this.positionData = positionData;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EditCommonData copy() {

        EditCommonData copy = new EditCommonData();
        copy.setAlpha(alpha);
        copy.setPosition(positionData);

        copy.setRotation(rotation);
        copy.setScale(scale);
        copy.setShapeID(getShapeID());
        copy.setType(type);
        //
        copy.setText(text);
        copy.setColor(color);
        copy.setFontSize(fontSize);

        return copy;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

}
