package com.kitty.radar.map.shp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ShpRecordHeader {
	
	private byte[] bytes = new byte[8];
	
	private int recordNumber;
	private int contentLength;
	
	private int realContentLength;
	
	public int getRecordNumber() {
		return recordNumber;
	}
	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public int getRealContentLength() {
		return realContentLength;
	}
	public void setRealContentLength(int realContentLength) {
		this.realContentLength = realContentLength;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public static ShpRecordHeader parse(InputStream ins) throws IOException {
		ShpRecordHeader shpRecordHeader = new ShpRecordHeader();
		byte[] shpRecordsBytes = new byte[8];
		int readLen = ins.read(shpRecordsBytes);
		if(8 != readLen) {
			throw new IOException("读取RecordHeader失败，需要读取8字节，实际读取"+readLen+"字节");
		}
		ByteBuffer buf = ByteBuffer.wrap(shpRecordsBytes);
		buf.order(ByteOrder.BIG_ENDIAN);
		shpRecordHeader.recordNumber = buf.getInt();
		shpRecordHeader.contentLength = buf.getInt();
		shpRecordHeader.realContentLength = shpRecordHeader.contentLength*2;
		return shpRecordHeader;
	}

}
