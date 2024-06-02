package com.kitty.radar.map.shp.type;

import java.io.IOException;
import java.io.InputStream;

import com.kitty.radar.map.shp.IShape;

public class NullShap implements IShape{

	private int shapeType;

	public int getShapeType() {
		return shapeType;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}

	@Override
	public int parse(InputStream ins) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
}
