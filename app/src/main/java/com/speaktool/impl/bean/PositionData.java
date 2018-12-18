package com.speaktool.impl.bean;

public class PositionData {
    private int x;
    private int y;

    public PositionData(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void bianHuan(float mx, float my) {
        this.x = (int) (this.x * mx);
        this.y = (int) (this.y * my);
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

    public void setY(int y) {
        this.y = y;
    }

}
