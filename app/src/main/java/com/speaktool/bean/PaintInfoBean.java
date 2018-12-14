package com.speaktool.bean;

/**
 * 画笔
 *
 * @author shaoshuai
 */
public class PaintInfoBean {
    private int color;// 颜色
    private int iconResId;// 默认背景
    private int iconResIdSelected;// 选中背景
    private int strokeWidth;// 笔迹粗细 px

    public PaintInfoBean() {
    }

    public PaintInfoBean(int color, int iconResId, int iconResIdSelected) {
        this(color, iconResId, iconResIdSelected, 5);
    }

    public PaintInfoBean(int color, int iconResId, int iconResIdSelected, int strokeWidth) {
        this.color = color;
        this.iconResId = iconResId;
        this.iconResIdSelected = iconResIdSelected;
        this.strokeWidth = strokeWidth;
    }

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

    /**
     * 获取笔迹宽度pix
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置笔迹宽度pix
     */
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
