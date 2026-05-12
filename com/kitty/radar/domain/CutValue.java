package com.kitty.radar.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * CutValue、RadialValue、BinValue用于保存雷达数据，如CAPPI。
 * 目前的用例：
 * 1、保存CAPPI图像数据，用于鼠标点击查找
 * 2、导出CAPPI数据
 * 3、RadarData.readFile返回的文件数据
 */
public class CutValue {

    private int cutNum;

    private double elevation;

    private List radialValues = new ArrayList();

    public CutValue(int cutNum) {
        this.cutNum = cutNum;
    }

    public CutValue(int cutNum, double elevation) {
        this.cutNum = cutNum;
        this.elevation = elevation;
    }

    public int getCutNum() {
        return cutNum;
    }

    public void setCutNum(int cutNum) {
        this.cutNum = cutNum;
    }

    public List getRadialValues() {
        return radialValues;
    }

    public void addRadialValue(RadialValue rv) {
        this.radialValues.add(rv);
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

}
