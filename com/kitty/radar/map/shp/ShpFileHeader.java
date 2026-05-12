package com.kitty.radar.map.shp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ShpFileHeader {
	
	private byte[] fileHeaderBytes = new byte[100];
	
	private int fileCode;
	private int fileLength;
	private int version;
	private int shapeType;
	private double xmin;
	private double ymin;
	private double xmax;
	private double ymax;
	private double zmin;
	private double zmax;
	private double mmin;
	private double mmax;
	

	private int realFileLength;
	
	public static ShpFileHeader parse(InputStream ins) throws IOException {
		byte[] headerBytes = new byte[100];
		int readLen = ins.read(headerBytes);
		if(readLen != 100) {
			throw new IOException("ShpFileHeader读取失败，需要读取100字节，实际读取"+readLen+"字节");
		}
		ShpFileHeader shpFileHeader = new ShpFileHeader();
		System.arraycopy(headerBytes, 0, shpFileHeader.fileHeaderBytes, 0, 100);
		ByteBuffer buf = ByteBuffer.wrap(shpFileHeader.fileHeaderBytes);
		buf.order(ByteOrder.BIG_ENDIAN);
		shpFileHeader.fileCode = buf.getInt();
		buf.position(24);
		shpFileHeader.fileLength = buf.getInt();
		shpFileHeader.setRealFileLength(shpFileHeader.fileLength*2);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		shpFileHeader.version = buf.getInt();
		shpFileHeader.shapeType = buf.getInt();
		shpFileHeader.xmin = buf.getDouble();
		shpFileHeader.ymin = buf.getDouble();
		shpFileHeader.xmax = buf.getDouble();
		shpFileHeader.ymax = buf.getDouble();
		shpFileHeader.zmin = buf.getDouble();
		return shpFileHeader;
	}
	
	public byte[] getFileHeaderBytes() {
		return fileHeaderBytes;
	}
	public void setFileHeaderBytes(byte[] fileHeaderBytes) {
		this.fileHeaderBytes = fileHeaderBytes;
	}
	public int getFileCode() {
		return fileCode;
	}
	public void setFileCode(int fileCode) {
		this.fileCode = fileCode;
	}
	public int getFileLength() {
		return fileLength;
	}
	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getShapeType() {
		return shapeType;
	}
	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}
	public double getXmin() {
		return xmin;
	}
	public void setXmin(double xmin) {
		this.xmin = xmin;
	}
	public double getYmin() {
		return ymin;
	}
	public void setYmin(double ymin) {
		this.ymin = ymin;
	}
	public double getXmax() {
		return xmax;
	}
	public void setXmax(double xmax) {
		this.xmax = xmax;
	}
	public double getYmax() {
		return ymax;
	}
	public void setYmax(double ymax) {
		this.ymax = ymax;
	}
	public double getZmin() {
		return zmin;
	}
	public void setZmin(double zmin) {
		this.zmin = zmin;
	}
	public double getZmax() {
		return zmax;
	}
	public void setZmax(double zmax) {
		this.zmax = zmax;
	}
	public double getMmin() {
		return mmin;
	}
	public void setMmin(double mmin) {
		this.mmin = mmin;
	}
	public double getMmax() {
		return mmax;
	}
	public void setMmax(double mmax) {
		this.mmax = mmax;
	}

	public int getRealFileLength() {
		return realFileLength;
	}

	public void setRealFileLength(int realFileLength) {
		this.realFileLength = realFileLength;
	}
	
}
