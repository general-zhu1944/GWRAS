package com.kitty.radar.business.area;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.kitty.radar.RadarBase;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

/**
 * 圆形区域。
 */
public class CircleArea extends Area {

    private double r;

    private double lx;

    private double ly;
    

    public CircleArea(double lx, double ly, double r,RadarBase radarBase) {
    	super(radarBase);
        this.lx = lx;
        this.ly = ly;
        this.r = r;
    }

    public double getLx() {
        return lx;
    }

    public double getLy() {
        return ly;
    }

    public double getR() {
        return r;
    }

    public String getAreaName() {
        return AreaInputDialog.AREA_CIRCLE;
    }

    public String toString() {
        return "经度:" + CommonUtils.format(lx, 6) + "°; 纬度:" + CommonUtils.format(ly, 6) + "°; 半径:"
                + CommonUtils.format(r, 1) + RadarUtils.getDistanceUnitLabel();
    }

    @Override
    public void display(Graphics2D g) {
        if (this.visible) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2));
            XYCoord c = PositionUtils.toXYCoord2(lx, ly, this.getRadarBase());
            int cr = PositionUtils.toLength(r,this.getRadarBase().getScale_X());
            g.drawOval(c.x - cr, c.y - cr, 2 * cr, 2 * cr);
        }
    }

    @Override
    public void deSerialize(String value) {
        String[] v = value.split(",");
        this.lx = Double.parseDouble(v[1]);
        this.ly = Double.parseDouble(v[2]);
        this.r = Double.parseDouble(v[3]);
        this.visible = Boolean.parseBoolean(v[4]);
    }

    @Override
    public String serialize() {
        return "C," + lx + "," + ly + "," + r + "," + visible;
    }

    @Override
    public Shape toShape() {
        XYCoord c = PositionUtils.toXYCoord2(lx, ly, this.getRadarBase());
        int cr = PositionUtils.toLength(r,this.getRadarBase().getScale_X());
        return new Ellipse2D.Double(c.x - cr, c.y - cr, 2 * cr, 2 * cr);
    }

}
