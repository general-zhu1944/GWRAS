package com.kitty.radar.map.shp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ShpRecord<T extends IShape> {

	private ShpRecordHeader recordHeader;
	
	private T recordContent;

	public ShpRecordHeader getRecordHeader() {
		return recordHeader;
	}

	public void setRecordHeader(ShpRecordHeader recordHeader) {
		this.recordHeader = recordHeader;
	}

	public T getRecordContent() {
		return recordContent;
	}

	public void setRecordContent(T recordContent) {
		this.recordContent = recordContent;
	}
	
	public static <T extends IShape> ShpRecord<T> parse(Class<T> shapeClass, InputStream ins) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		ShpRecord<T> shpRecord = new ShpRecord<T>();
		shpRecord.recordHeader = ShpRecordHeader.parse(ins);
		byte[] shapeTypeBytes = new byte[4];
		ins.read(shapeTypeBytes);
		ByteBuffer buf = ByteBuffer.wrap(shapeTypeBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int shapeType = buf.getInt();
		T shape = shapeClass.getDeclaredConstructor().newInstance();
		shape.parse(ins);
		shpRecord.recordContent = shape;
		return shpRecord;
	}

	
}
