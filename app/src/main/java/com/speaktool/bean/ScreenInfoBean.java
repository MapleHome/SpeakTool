package com.speaktool.bean;

import java.io.Serializable;

/**
 * 屏幕信息
 *
 * @author shaoshuai
 */
public class ScreenInfoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public int w;
    public int h;
    public int density;

    public ScreenInfoBean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ScreenInfoBean(int w, int h, int density) {
        super();
        this.w = w;
        this.h = h;
        this.density = density;
    }


    @Override
    public String toString() {
        return "ScreenInfoBean{" +
                "w=" + w +
                ", h=" + h +
                ", density=" + density +
                '}';
    }
}
