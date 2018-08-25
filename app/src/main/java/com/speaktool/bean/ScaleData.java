package com.speaktool.bean;

import com.speaktool.utils.FormatUtils;

public class ScaleData {

    private long t;
    private float s;// x10000.
    private int r;
    private int x;
    private int y;

    public ScaleData(long t, float s, int r, int x, int y) {
        super();
        this.t = t;
        this.s = formatFloat(s);
        this.r = r;
        this.x = x;
        this.y = y;
    }

    private static float formatFloat(float f) {
        return FormatUtils.formatFloat(f);

    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = formatFloat(s);
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
