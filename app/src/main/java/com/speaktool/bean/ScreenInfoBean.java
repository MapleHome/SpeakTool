package com.speaktool.bean;

import java.io.Serializable;

/**
 * 屏幕信息
 *
 * @author shaoshuai
 */
public class ScreenInfoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public int width;
    public int height;
    public int density;

    public ScreenInfoBean(int width, int height, int density) {
        this.width = width;
        this.height = height;
        this.density = density;
    }


    @Override
    public String toString() {
        return "ScreenInfoBean{" +
                "width=" + width + ", height=" + height +
                ", density=" + density +
                '}';
    }
}
