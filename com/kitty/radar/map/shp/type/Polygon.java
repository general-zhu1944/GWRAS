package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.kitty.radar.map.shp.IShape;

public class Polygon implements IShape{

	//形状类型
	private int shapeType;
	
	//边界BOX
	private Box box;
	
	//Part个数
	private int numParts;
	
	//点的数量
	private int numPoints;
	
	//每个Part的起始点在points中偏移位置
	private int[] parts;
	
	//所有点
	private Point[] points;
	
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

	public Point[] getPoints() {
		return points;
	}

	public void setPoints(Point[] points) {
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

	@Override
	public int parse(InputStream ins) throws IOException {
		int readBytes = 0;
		byte[] bytebuf = new byte[40];
		int readLen = ins.read(bytebuf);
		if(40 != readLen) {
			throw new IOException();
		}
		readBytes += readLen;
		ByteBuffer buf = ByteBuffer.wrap(bytebuf);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		this.box = new Box();
		this.box.setXmin(buf.getDouble());
		this.box.setYmin(buf.getDouble());
		this.box.setXmax(buf.getDouble());
		this.box.setYmax(buf.getDouble());
		this.numParts = buf.getInt();
		this.numPoints = buf.getInt();

		this.parts = new int[this.numParts];
		bytebuf = new byte[this.numParts * 4];
		ins.read(bytebuf);

		readBytes += this.numParts * 4;
		buf = ByteBuffer.wrap(bytebuf);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < parts.length; i++) {
			this.parts[i] = buf.getInt();
		}
		
		this.points = new Point[this.numPoints];
		for(int j = 0; j< this.numPoints; j++ ) {
			Point point = new Point();
			readBytes += point.parse(ins);
			this.points[j] = point;
		}
		return readBytes;
	}
	
	public Point[] getPart(int partId) {
		int startPos = this.parts[partId];
		int endPos = 0;
		if(partId < this.numParts-1) {
			endPos = this.parts[partId+1];
		} else {
			endPos = this.points.length;
		}
		return Arrays.copyOfRange(this.points, startPos, endPos);
	}
}
