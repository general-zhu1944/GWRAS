package com.kitty.radar.data;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.kitty.component.third.RandomAccessFile;
import com.kitty.radar.RadarBase;

/**
 * NEXRAD Level II(WSR-88D)格式雷达文件读取基类、SA、SB格式雷达文件读取类。
 */
public class CA_CB extends RadarData {
	public CA_CB(RadarBase radarBase) {
		super(radarBase);
	}

	public static final byte RECORD_HEADER_SIZE = 12; // 单位：字节

	// 国外文件为RandomAccessFile.BIG_ENDIAN
	public int endianMode = RandomAccessFile.LITTLE_ENDIAN;

	// 体扫文件的title（文件头）大小，CINRAD没有title，国外为24字节
	public byte fileHeaderSize = 0;

	public short recordSize = 4132;//注：2432为S波段每层仰角某径向块的数据大小  4132是CA/CB波段每层仰角某径向块的数据大小

	public short messageSize = 0;

	public byte channel = 0;

	public byte messageType = 0;

	public short idSequence = 0;

	public short julianDate = 0; // 数据收集儒略日 自1970年1月1日开始

	public int milliseconds = 0; // 数据收集时间 (毫秒,自00:00开始)

	public short numberSegments = 0;

	public short segNumber = 0;

	public int dataMilliseconds = 0;

	public short dataJulianDate = 0;

	public short unambiguousRange = 0;

	public int azimuthAngle = 0;

	public short radialNumber = 0; // 当前仰角内径向数据序号

	public short radialStatus = 0;

	public short elevationAngle = 0;

	public short elevationNumber = 0;

	public short sectorNumber = 0;

	public float calibration = 0;

	public short reflectivityPointer = 0;

	public short velocityPointer = 0;

	public short spectrumWidthPointer = 0;

	public short nyquistVelocity = 0;

	public short attenuation = 0;

	public short tover = 0;

	public short spotBlanking = 0;

	// 保存仰角内的所有数据(readCut)，或仅使用byte[0]保存一条数据(readRecord)
	public byte[][] bins;

	private double[] elevations = new double[MAX_FILE_CUTS]; // 缓存所有仰角的度数

	/**
	 * 原始基数据转换为变量值
	 * 
	 * @param value
	 * @param moment
	 * @param resolution
	 * @return 如果没有相应的数据，返回RadarData.NO_DATA
	 */
	public static float binaryToMoment(int value, int moment, int resolution) {
		if (value == 0) {
			return RadarData.NO_DATA;
		}
		if (moment == DBZ || moment == DBT) {
			return (float) (value / 2.0 - 33);
		} else if (moment == V) {
			if (resolution == DOPPLER_RESOLUTION_LOW) {
				return value - 129;
			} else {
				return (float) (value / 2.0 - 64.5);
			}
		} else if (moment == W) {
			return (float) (value / 2.0 - 64.5);
		} else if (moment == ZDR) {
			return (float) (value / 16.0 - 8.125);
		} else if (moment == KDP) {
			return (float) (value / 10.0 - 5);
		} else if (moment == DP) {
			return (float) (value / 100.0 - 0.5);
		} else if (moment == CC) {
			return (float) (value / 200.0 - 0.025);
		} else if (moment == SNRH) {
			return (float) (value / 2.0 - 10);
		}
		throw new RuntimeException("无此数据类型");
	}

	public static int momentToBinary(float value, int moment, int resolution) {
		if (moment == DBZ || moment == DBT) {
			return (int) Math.round((value + 33) * 2);
		} else if (moment == V) {
			if (resolution == DOPPLER_RESOLUTION_LOW) {
				return (int) Math.round(value + 129);
			} else {
				return (int) Math.round((value + 64.5) * 2);
			}
		} else if (moment == W) {
			return (int) Math.round((value + 64.5) * 2);
		} else if (moment == ZDR) {
			return (int) Math.round((value + 8.125) * 16);
		} else if (moment == KDP) {
			return (int) Math.round((value + 5) * 10);
		} else if (moment == DP) {
			return (int) Math.round((value + 0.5) * 100);
		} else if (moment == CC) {
			return (int) Math.round((value + 0.025) * 200);
		} else if (moment == SNRH) {
			return (int) Math.round((value + 10) * 2);
		}
		throw new RuntimeException("无此数据类型");
	}

	/**
	 * 打开雷达文件。 对srcFileName、azimuths[]、cutStarts[]、elevations[]、cutNumber赋值
	 */
	public boolean open(File file) {
		try {
			bins = new byte[MAX_CUT_RECORDS][recordSize - 32];
			if (srcFileName == null) {
				srcFileName = file.getName();
			}
			raf = new RandomAccessFile(file.getPath(), "r");
			raf.order(endianMode);
			short oldCut = -1;
			boolean flag = false;
			for (short i = 0; i < MAX_FILE_RECORDS; i++) {
				if (!readHeader(i)) {
					break;
				}
				if (messageType == 1) { // 表示雷达数据
					azimuths[i] = getAzimuth();
					if (oldCut != elevationNumber) {

						// i > 0用于区分SA和CB
						if (i > 0 && ("21".equals(vcp) || "11".equals(vcp) || "31".equals(vcp) || "32".equals(vcp))) {
							flag = true;
						}
						if (elevationNumber > 0) {
							oldCut = elevationNumber;
							cutStarts[cutNumber] = i;
							elevations[cutNumber] = getElevation();
							cutNumber++;
						} else {
							break;
						}
					}
				} else {
					azimuths[i] = -1;
				}
			}
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			this.close();
		}
		return false;
	}

	public double getElevation(int cutNum) {
		return elevations[cutNum];
	}

	public Date getFileTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 0, 1, 0, 0, 0);
		cal.add(Calendar.DAY_OF_MONTH, julianDate - 1);
		cal.add(Calendar.MILLISECOND, milliseconds);
		return cal.getTime();
	}

	public boolean readHeader(int recordNum) {
		super.readHeader(recordNum);
		try {
			readHeaderOnly(recordNum);
			raf.skipBytes(recordSize - 100);
			return true;
		} catch (EOFException e) {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void readHeaderOnly(int recordNum) throws IOException {//recordNum仰角数
		raf.seek(recordNum * recordSize + fileHeaderSize);
		raf.skipBytes(RECORD_HEADER_SIZE);
		messageSize = raf.readShort();
		messageType = (byte) raf.readShort();
		idSequence = raf.readShort();
		julianDate = raf.readShort();
		milliseconds = raf.readInt();
		numberSegments = raf.readShort();
		segNumber = raf.readShort();
		dataMilliseconds = raf.readInt();
		dataJulianDate = raf.readShort();
		unambiguousRange = raf.readShort();
		azimuthAngle = raf.readUnsignedShort();
		radialNumber = raf.readShort();
		radialStatus = raf.readShort();
		elevationAngle = raf.readShort();
		elevationNumber = raf.readShort();
		surveillanceRange = raf.readShort();
		dopplerRange = raf.readShort();
		surveillanceInterval = raf.readShort();
		dopplerInterval = raf.readShort();
		surveillanceBins = raf.readShort();
		dopplerBins = raf.readShort();
		sectorNumber = raf.readShort();
		calibration = raf.readFloat();
		reflectivityPointer = raf.readShort();
		velocityPointer = raf.readShort();
		spectrumWidthPointer = raf.readShort();
		resolution = raf.readShort();
		vcp = String.valueOf(raf.readShort());
		raf.skipBytes(14);
		nyquistVelocity = raf.readShort();
		attenuation = raf.readShort();
		tover = raf.readShort();
		spotBlanking = raf.readShort();
	}

	public boolean readRecord(int recordNum) {
		super.readRecord(recordNum);
		try {
			this.readHeaderOnly(recordNum);
			raf.skipBytes(32);
			raf.readFully(bins[0], 0, recordSize - 128);
			return true;
		} catch (EOFException e) {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public int readCut(int recordNum) {
		super.readCut(recordNum);
		int i = 0;
		try {
			this.readHeaderOnly(recordNum);
			for (i = 0; i < MAX_CUT_RECORDS; i++) {
				raf.seek((recordNum + i) * recordSize + fileHeaderSize + RECORD_HEADER_SIZE + 2);
				if (raf.readShort() == 1) {
					raf.skipBytes(28);
					if (elevationNumber != raf.readShort()) {
						break;
					} else {
						raf.skipBytes(82);
						raf.readFully(bins[i], 0, recordSize - 128);
					}
				}
			}
		} catch (EOFException e) {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	public double getAzimuth() {
		return 180 * azimuthAngle / 32768.0;
	}

	public double getElevation() {
		return 180 * elevationAngle / 32768.0;
	}

	public int getBinaryValue(int moment, int radial, int bin) {
		byte v;
		if (moment == DBZ) {
			v = bins[radial][reflectivityPointer - 100 + bin];
		} else if (moment == V) {
			v = bins[radial][velocityPointer - 100 + bin];
		} else {
			v = bins[radial][spectrumWidthPointer - 100 + bin];
		}
		return (short) (v >= 0 ? v : 256 + v);
	}

	public float getMomentValue(int moment, int radial, int bin) {
		return binaryToMoment(this.getBinaryValue(moment, radial, bin), moment, resolution);
	}

}
