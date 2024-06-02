package com.kitty.radar.map.shp;

import java.io.IOException;
import java.io.InputStream;

public interface IShape {
	public int getShapeType();

	public int parse(InputStream ins) throws IOException;
}
