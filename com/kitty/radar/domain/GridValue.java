package com.kitty.radar.domain;

import java.awt.Color;

public class GridValue {

    public int x;

    public int y;

    private Color color;
    
    private short shortValue;
    
    private double doubleValue;
    
    private ARCoord arc;

    public GridValue(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public short getShortValue() {
        return shortValue;
    }

    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

	public ARCoord getARCoord() {
		return arc;
	}

	public void setARCoord(ARCoord arc) {
		this.arc = arc;
	}

}
