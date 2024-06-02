package com.kitty.radar.map.shp;

import java.io.File;
import java.util.List;

public class ShpMainFile {
	
	private ShpFileHeader fileHeader;
	
	private List<ShpRecord> records;

	public ShpFileHeader getFileHeader() {
		return fileHeader;
	}

	public void setFileHeader(ShpFileHeader fileHeader) {
		this.fileHeader = fileHeader;
	}

	public List<ShpRecord> getRecords() {
		return records;
	}

	public void setRecords(List<ShpRecord> records) {
		this.records = records;
	}
	
	public static ShpMainFile load(File file) {
		return null;
		
	}
	
}
