package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.kitty.radar.map.shp.IShape;

public class MultiPoint implements IShape{

	private int shapeType;
	
	private Box box;
	
	private int numPoints;
	
	private List<Point> points;
	
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

	@Override
	public int parse(InputStream ins) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
}
