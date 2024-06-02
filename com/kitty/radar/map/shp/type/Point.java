package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.kitty.radar.map.shp.IShape;

public class Point implements IShape{

	private int shapeType;
	
	private double x;
	private double y;
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
	@Override
	public int parse(InputStream ins) throws IOException {
		byte[] bytebuf = new byte[16];
		int readLen = ins.read(bytebuf);
		if(readLen != 16) {
			throw new IOException();
		}
		ByteBuffer buf = ByteBuffer.wrap(bytebuf);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		this.x = buf.getDouble();
		this.y = buf.getDouble();
		return readLen;
	}
	
}
