package com.kitty.radar.business.area;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.kitty.radar.RadarBase;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

/**
 * 矩形区域。
 */
public class RectangleArea extends Area {

    private double width;

    private double height;

    private double lx;

    private double ly;

    public RectangleArea(double lx, double ly, double width, double height, RadarBase radarBase) {
    	super(radarBase);
        this.lx = lx;
        this.ly = ly;
        this.width = width;
        this.height = height;
    }

    public double getLx() {
        return lx;
    }

    public double getLy() {
        return ly;
    }

    public String getAreaName() {
        return AreaInputDialog.AREA_RECTANGLE;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public String toString() {
        return "经度:" + CommonUtils.format(lx, 6) + "°; 纬度:" + CommonUtils.format(ly, 6) + "°; 长度:"
                + CommonUtils.format(width, 1) + RadarUtils.getDistanceUnitLabel() + "; 宽度:"
                + CommonUtils.format(height, 1) + RadarUtils.getDistanceUnitLabel();
    }

    @Override
    public void display(Graphics2D g) {
        if (this.visible) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2));
            XYCoord c = PositionUtils.toXYCoord2(lx, ly, this.getRadarBase());
            int w = PositionUtils.toLength(width, this.getRadarBase().getScale_X());
            int h = PositionUtils.toLength(height, this.getRadarBase().getScale_X());
            g.drawRect(c.x - w / 2, c.y - h / 2, w, h);
        }
    }

    @Override
    public void deSerialize(String value) {
        String[] v = value.split(",");
        this.lx = Double.parseDouble(v[1]);
        this.ly = Double.parseDouble(v[2]);
        this.width = Double.parseDouble(v[3]);
        this.height = Double.parseDouble(v[4]);
        this.visible = Boolean.parseBoolean(v[5]);
    }

    @Override
    public String serialize() {
        return "R," + lx + "," + ly + "," + width + "," + height + "," + visible;
    }

    @Override
    public Shape toShape() {
        XYCoord c = PositionUtils.toXYCoord2(lx, ly, this.getRadarBase());
        int w = PositionUtils.toLength(width, this.getRadarBase().getScale_X());
        int h = PositionUtils.toLength(height, this.getRadarBase().getScale_X());
        return new Rectangle2D.Double(c.x - w / 2, c.y - h / 2, w, h);
    }

}
