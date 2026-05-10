package com.kitty.radar.map.shp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ShpMap {
	
	private ShpFileHeader shpFileHeader;
	
	private List<ShpRecord> shpRecords;

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String shpFilePath = "D:\\PythonProject\\shapefiles_china_sichuan\\sichuan_full";
		File file = new File(shpFilePath+".shp");
		try {

			ShpMap shpMap = ShpMap.load(file);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public static ShpMap load(File file) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		byte[] fileBytes = FileUtils.readFileToByteArray(file);
		ByteArrayInputStream ins = new ByteArrayInputStream(fileBytes);
		ShpMap shpMap = new ShpMap();
		shpMap.parse(ins);
		return shpMap;
	}
	
	public void parse(InputStream ins) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		shpFileHeader = ShpFileHeader.parse(ins);
		shpRecords = new ArrayList<ShpRecord>();
		int shpType = shpFileHeader.getShapeType();
		Class<? extends IShape> clazz = ShapeTypeConst.getShapeTypeClass(shpType);
		while(0 != ins.available()) {
			ShpRecord<?> shpRecord = ShpRecord.parse(clazz, ins);
			shpRecords.add(shpRecord);
		}
	}

	public ShpFileHeader getShpFileHeader() {
		return shpFileHeader;
	}

	public void setShpFileHeader(ShpFileHeader shpFileHeader) {
		this.shpFileHeader = shpFileHeader;
	}

	public List<ShpRecord> getShpRecords() {
		return shpRecords;
	}

	public void setShpRecords(List<ShpRecord> shpRecords) {
		this.shpRecords = shpRecords;
	}
	
}
