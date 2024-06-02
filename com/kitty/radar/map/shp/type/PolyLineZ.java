package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.kitty.radar.map.shp.IShape;

public class PolyLineZ implements IShape{

	private int shapeType;
	
	private Box box;
	
	private int numParts;
	
	private int numPoints;
	
	private int[] parts;
	
	private List<Point> points;
	
	private double zmin;
	
	private double zmax;
	
	private double[] zarray;
	
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

	public int getNumParts() {
		return numParts;
	}

	public void setNumParts(int numParts) {
		this.numParts = numParts;
	}

	public int[] getParts() {
		return parts;
	}

	public void setParts(int[] parts) {
		this.parts = parts;
	}

	public double getZmin() {
		return zmin;
	}

	public void setZmin(double zmin) {
		this.zmin = zmin;
	}

	public double getZmax() {
		return zmax;
	}

	public void setZmax(double zmax) {
		this.zmax = zmax;
	}

	public double[] getZarray() {
		return zarray;
	}

	public void setZarray(double[] zarray) {
		this.zarray = zarray;
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
		return 0;
	}

}
