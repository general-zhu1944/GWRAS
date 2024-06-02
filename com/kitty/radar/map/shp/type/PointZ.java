package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;

import com.kitty.radar.map.shp.IShape;

public class PointZ implements IShape{

	private int shapeType;
	
	private double x;
	private double y;
	private double z;
	private double m;
	public int getShapeType() {
		return shapeType;
	}
	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getM() {
		return m;
	}
	public void setM(double m) {
		this.m = m;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	@Override
	public int parse(InputStream ins) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
