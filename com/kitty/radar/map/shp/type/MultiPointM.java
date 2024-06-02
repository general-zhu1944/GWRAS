package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.kitty.radar.map.shp.IShape;

public class MultiPointM implements IShape{

	private int shapeType;
	
	private Box box;
	
	private int numPoints;
	
	private List<Point> points;
	
	private double mmin;
	
	private double mmax;
	
	private double[] marray;
	
	public int getShapeType() {
		return shapeType;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public int getNumPoints() {
		return numPoints;
	}

	public void setNumPoints(int numPoints) {
		this.numPoints = numPoints;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public double getMmin() {
		return mmin;
	}

	public void setMmin(double mmin) {
		this.mmin = mmin;
	}

	public double getMmax() {
		return mmax;
	}

	public void setMmax(double mmax) {
		this.mmax = mmax;
	}

	public double[] getMarray() {
		return marray;
	}

	public void setMarray(double[] marray) {
		this.marray = marray;
	}

	@Override
	public int parse(InputStream ins) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
