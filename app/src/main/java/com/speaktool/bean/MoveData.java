package com.speaktool.bean;

public class MoveData {

    private long t;
    private int x;
    private int y;

    public MoveData(long t, int x, int y) {
        super();
        this.t = t;
        this.x = x;
        this.y = y;
    }

    public void bianHuanXY(float fx, float fy) {
        this.x = (int) (this.x * fx);
        this.y = (int) (this.y * fy);
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
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
