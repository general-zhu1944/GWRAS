package com.kitty.radar.map.shp;

import java.util.HashMap;
import java.util.Map;

import com.kitty.radar.map.shp.type.MultiPatch;
import com.kitty.radar.map.shp.type.MultiPoint;
import com.kitty.radar.map.shp.type.MultiPointM;
import com.kitty.radar.map.shp.type.MultiPointZ;
import com.kitty.radar.map.shp.type.NullShap;
import com.kitty.radar.map.shp.type.Point;
import com.kitty.radar.map.shp.type.PointM;
import com.kitty.radar.map.shp.type.PointZ;
import com.kitty.radar.map.shp.type.PolyLine;
import com.kitty.radar.map.shp.type.PolyLineM;
import com.kitty.radar.map.shp.type.Polygon;
import com.kitty.radar.map.shp.type.PolygonM;

/*
 * ShapeType
 */
public class ShapeTypeConst {
	public static final int NullShapType = 0;
	public static final int PointType = 1;
	public static final int PolyLineType = 3;
	public static final int PolygonType = 5;
	public static final int MultiPointType = 8;
	public static final int PointZType = 11;
	public static final int PolyLineZType = 13;
	public static final int PolygonZType = 15;
	public static final int MultiPointZType = 18;
	public static final int PointMType = 21;
	public static final int PolyLineMType = 23;
	public static final int PolygonMType = 25;
	public static final int MultiPointMType = 28;
	public static final int MultiPatchType = 31;

	
	public static Class<? extends IShape> getShapeTypeClass(int shapeType) {
		switch (shapeType) {
		case NullShapType:
			return NullShap.class;
		case PointType:
			return Point.class;
		case PolyLineType:
			return PolyLine.class;
		case PolygonType:
			return Polygon.class;
		case MultiPointType:
			return MultiPoint.class;
		case PointZType:
			return PointZ.class;
		case MultiPointZType:
			return MultiPointZ.class;
		case PointMType:
			return PointM.class;
		case PolyLineMType:
			return PolyLineM.class;
		case PolygonMType:
			return PolygonM.class;
		case MultiPointMType:
			return MultiPointM.class;
		case MultiPatchType:
			return MultiPatch.class;
		default:
			return null;
		}
	}
}
