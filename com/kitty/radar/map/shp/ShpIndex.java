package com.kitty.radar.map.shp;

import java.util.List;

public class ShpIndex {

	private ShpFileHeader fileHeader;
	
	private List<Index> indexes;
	
	public ShpFileHeader getFileHeader() {
		return fileHeader;
	}

	public void setFileHeader(ShpFileHeader fileHeader) {
		this.fileHeader = fileHeader;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public class Index {
		private int offset;
		
		private int contentLength;

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getContentLength() {
			return contentLength;
		}

		public void setContentLength(int contentLength) {
			this.contentLength = contentLength;
		}
		
	}
}
