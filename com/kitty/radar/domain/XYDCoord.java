package com.kitty.radar.domain;

/**
 * 以雷达中心为原点的X、Y坐标，单位：km
 * X坐标向右为正，Y坐标向上为正
 */
public class XYDCoord {

    public double x;

    public double y;

    public XYDCoord(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
