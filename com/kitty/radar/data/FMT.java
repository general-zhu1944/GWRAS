package com.kitty.radar.data;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kitty.component.third.RandomAccessFile;
import com.kitty.radar.RadarBase;

import javax.swing.*;

public class FMT extends RadarData {

	public static final byte[] MOMENT_INDEX = new byte[36];

	public static final byte DATA_TYPE_NUMBER = 9;
	
	static {
		for (int i = 0; i < MOMENT_INDEX.length; i++) {
			MOMENT_INDEX[i] = -1;
		}
		MOMENT_INDEX[RadarData.DBT] = 0;
		MOMENT_INDEX[RadarData.DBZ] = 1;
		MOMENT_INDEX[RadarData.ZDR] = 2;
		MOMENT_INDEX[RadarData.KDP] = 3;
		MOMENT_INDEX[RadarData.CC] = 4;
		MOMENT_INDEX[RadarData.DP] = 5;
		MOMENT_INDEX[RadarData.SNRH] = 6;
		MOMENT_INDEX[RadarData.V] = 7;
		MOMENT_INDEX[RadarData.W] = 8;
	}

	@SuppressWarnings("rawtypes")
	private Map commonMap = new HashMap();

	@SuppressWarnings("rawtypes")
	private Map[] cutMaps;

	private Radial[] radials = new Radial[MAX_FILE_RECORDS];

	private int maxRadialNum = 0;
	private short maxBinNum = 0;
	private int recordNum;
	private float[][][] values;

	@Override
	public Date getFileTime() {
		int sec = (int) this.commonMap.get("ScanStartTime");
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 0, 1, 0, 0, 0);
		cal.add(Calendar.SECOND, sec);
		return cal.getTime();
	}

	@Override
	public double getElevation(int cutNum) {
		return (float) cutMaps[cutNum].get("Elevation");
	}

	@Override
	public boolean readHeader(int recordNum) {
		super.readHeader(recordNum);
		this.recordNum = recordNum;
		this.readParams(this.getCutNum(recordNum));
		return true;
	}
	@Override
	public boolean readRcecordnum(int recordNum) {
		super. readRcecordnum(recordNum);
		this.recordNum = recordNum;
		return true;
	}

	@Override
	public boolean readRecord(int recordNum) {
		super.readRecord(recordNum);
		this.recordNum = recordNum;
		this.readParams(this.getCutNum(recordNum));
		try {
			for (int i = 0; i < radials[recordNum].moments.length; i++) {
				Moment moment = radials[recordNum].moments[i];
				if (moment != null) {
					int scale = (int) moment.header.get("Scale");
					int offset = (int) moment.header.get("Offset");
					int binLength = (int) moment.header.get("BinLength");
					raf.seek(moment.filePointer);
					for (int j = 0; j < radials[recordNum].moments[i].binNumber; j++) {
						int value;
						if (binLength == 2) {
							value = raf.readUnsignedShort();
						} else {
							value = raf.readUnsignedByte();
						}
						if (value < 5 && value != 1) {
							values[0][i][j] = RadarData.NO_DATA;
						} else {
							values[0][i][j] = (value - offset) / (float) scale;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public int readCut(int recordNum) {
		super.readCut(recordNum);
		try {
			this.recordNum = recordNum;
			this.readParams(this.getCutNum(recordNum));
			int cutn = (int) radials[recordNum].header.get("ElevationNumber");
			int k = recordNum;
			for (; k < this.maxRadialNum; k++) {
				int cn = (int) radials[k].header.get("ElevationNumber");
				if (cn != cutn) {
					break;
				}
				for (int i = 0; i < radials[k].moments.length; i++) {
					Moment moment = radials[k].moments[i];
					if (moment != null) {
						int scale = (int) moment.header.get("Scale");
						int offset = (int) moment.header.get("Offset");
						int binLength = (int) moment.header.get("BinLength");
						raf.seek(moment.filePointer);
						for (int j = 0; j < radials[k].moments[i].binNumber; j++) {
							int value;
							if (binLength == 2) {
								value = raf.readUnsignedShort();
							} else {
								value = raf.readUnsignedByte();
							}
							if (value < 5 && value != 1) {
								values[k - recordNum][i][j] = RadarData.NO_DATA;
							} else {
								values[k - recordNum][i][j] = (value - offset) / (float) scale;
							}
						}
					}
				}
			}
			return k - recordNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public double getAzimuth() {
		return this.getAzimuth(recordNum);
	}

	@Override
	public double getElevation() {
		return this.getElevation(this.getCutNum(recordNum));
	}

	@Override
	public int getBinaryValue(int moment, int radial, int bin) {
		float fv = this.getMomentValue(moment, radial, bin);
		if (fv == RadarData.NO_DATA) {
			return 0;
		}
		return SA_SB.momentToBinary(fv, moment, this.resolution);
	}

	@Override
	public float getMomentValue(int moment, int radial, int bin) {
		return values[radial][MOMENT_INDEX[moment]][bin];
	}

	@Override
	public boolean open(File file) {
		try {
			raf = new RandomAccessFile(file.getPath(), "r");
			raf.order(RandomAccessFile.LITTLE_ENDIAN);
			if (!this.readCommonHead()) {
				return false;
			}
			this.readRadialHeaders();
			readParams(0);
			values = new float[MAX_CUT_RECORDS][DATA_TYPE_NUMBER][maxBinNum];
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			this.close();
		}
		return false;
	}

	@SuppressWarnings({ "unchecked" })
	private void readRadialHeaders() throws IOException {
		maxRadialNum = 0;
		maxBinNum = 0;
		try {
			while (true) {
				radials[maxRadialNum] = new Radial();
				int radialState = raf.readInt();
				radials[maxRadialNum].header.put("RadialState", radialState);
				radials[maxRadialNum].header.put("SpotBlank", raf.readInt());
				radials[maxRadialNum].header.put("SequenceNumber", raf.readInt());
				radials[maxRadialNum].header.put("RadialNumber", raf.readInt());
				int elevationNumber = raf.readInt();
				radials[maxRadialNum].header.put("ElevationNumber", elevationNumber);
				radials[maxRadialNum].header.put("Azimuth", raf.readFloat());
				radials[maxRadialNum].header.put("Elevation", raf.readFloat());
				radials[maxRadialNum].header.put("Seconds", raf.readInt());
				radials[maxRadialNum].header.put("Microseconds", raf.readInt());
				int lengthofdata = raf.readInt();
				radials[maxRadialNum].header.put("Lengthofdata", lengthofdata);
				int momentNumber = raf.readInt();
				radials[maxRadialNum].header.put("MomentNumber", momentNumber);
				raf.skipBytes(2);
				radials[maxRadialNum].header.put("HorizontalEstimatedNoise", raf.readShort());
				radials[maxRadialNum].header.put("VerticalEstimatedNoise", raf.readShort());
				raf.skipBytes(14);
				int readLength = 0;
				int readMoment = 0;
				for (int i = 0; i < momentNumber; i++) {
					int dataType = raf.readInt();
					int scale = raf.readInt();
					int offset = raf.readInt();
					int binLength = raf.readShort();
					short flags = raf.readShort();
					int length = raf.readInt();
					raf.skipBytes(12);
					dataTypeSet.add(dataType);
					int index = MOMENT_INDEX[dataType];
					if (index != -1) {
						radials[maxRadialNum].moments[index] = new Moment();
						radials[maxRadialNum].moments[index].header.put("DataType", dataType);
						radials[maxRadialNum].moments[index].header.put("Scale", scale);
						radials[maxRadialNum].moments[index].header.put("Offset", offset);
						radials[maxRadialNum].moments[index].header.put("BinLength", binLength);
						radials[maxRadialNum].moments[index].header.put("Flags", flags);
						radials[maxRadialNum].moments[index].header.put("Length", length);
						short binNum = (short) (length / binLength);
						if (binNum > maxBinNum) {
							maxBinNum = binNum;
						}
						radials[maxRadialNum].moments[index].binNumber = binNum;
						radials[maxRadialNum].moments[index].filePointer = raf.getFilePointer();
						readMoment++;
					}
					raf.skipBytes(length);
					readLength += (32 + length);
					if (readMoment == radials[maxRadialNum].moments.length) {
						raf.skipBytes(lengthofdata - readLength);
						break;
					}
				}
				if (radialState == 0 || radialState == 3) {
					cutStarts[elevationNumber - 1] = (short) maxRadialNum;
				}
				azimuths[maxRadialNum] = (float) radials[maxRadialNum].header.get("Azimuth");
				maxRadialNum++;
			}
		} catch (EOFException e) {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean readCommonHead() throws IOException {
		commonMap.put("MagicNumber", raf.readInt());
		commonMap.put("MajorVersion", raf.readUnsignedShort());
		commonMap.put("MinorVersion", raf.readUnsignedShort());
		commonMap.put("GenericType", raf.readInt());
		commonMap.put("ProductType", raf.readInt());
		raf.skipBytes(16);
		commonMap.put("SiteCode", raf.readString(8).trim());
		commonMap.put("SiteName", raf.readString(32).trim());
		commonMap.put("Latitude", raf.readFloat());
		commonMap.put("Longitude", raf.readFloat());
		commonMap.put("AntennaHeight", raf.readInt());
		commonMap.put("GroundHeight", raf.readInt());
		commonMap.put("Frequency", raf.readFloat());
		commonMap.put("BeamWidthHori", raf.readFloat());
		commonMap.put("BeamWidthVert", raf.readFloat());
		commonMap.put("RDAVersion", raf.readInt());
		this.radarType = raf.readShort();
		commonMap.put("RadarType", radarType);
		if (radarType < 1 || radarType > 100) {
			return false;
		}
		commonMap.put("AntennaGain", raf.readShort());
		commonMap.put("TransmittingFeederLoss", raf.readShort());
		commonMap.put("ReceivingFeederLoss", raf.readShort());
		commonMap.put("OtherLoss", raf.readShort());
		raf.skipBytes(46);
		this.vcp = raf.readString(32).trim();
		this.vcp = vcp.toUpperCase().replaceFirst("^VCP", "");
		commonMap.put("TaskName", vcp);
		commonMap.put("TaskDescription", raf.readString(128).trim());
		commonMap.put("PolarizationType", raf.readInt());
		commonMap.put("ScanType", raf.readInt());
		commonMap.put("PulseWidth", raf.readInt());
		commonMap.put("ScanStartTime", raf.readInt());
		this.cutNumber = (byte) raf.readInt();
		commonMap.put("CutNumber", cutNumber);
		commonMap.put("HorizontalNoise", raf.readFloat());
		commonMap.put("VerticalNoise", raf.readFloat());
		commonMap.put("HorizontalCalibration", raf.readFloat());
		commonMap.put("VerticalCalibration", raf.readFloat());
		commonMap.put("HorizontalNoiseTemperature", raf.readFloat());
		commonMap.put("VerticalNoiseTemperature", raf.readFloat());
		commonMap.put("ZDRCalibration", raf.readFloat());
		commonMap.put("PHIDPCalibration", raf.readFloat());
		commonMap.put("LDRCalibration", raf.readFloat());
		raf.skipBytes(40);
		this.cutMaps = new Map[this.cutNumber];
		for (int i = 0; i < cutMaps.length; i++) {
			cutMaps[i] = new LinkedHashMap();
			cutMaps[i].put("ProcessMode", raf.readInt());
			cutMaps[i].put("WaveForm", raf.readInt());
			cutMaps[i].put("PRF#1", raf.readFloat());
			cutMaps[i].put("PRF#2", raf.readFloat());
			cutMaps[i].put("DealiasingMode", raf.readInt());
			cutMaps[i].put("Azimuth", raf.readFloat());
			cutMaps[i].put("Elevation", raf.readFloat());
			cutMaps[i].put("StartAngle", raf.readFloat());
			cutMaps[i].put("EndAngle", raf.readFloat());
			cutMaps[i].put("AngularResolution", raf.readFloat());
			cutMaps[i].put("ScanSpeed", raf.readFloat());
			cutMaps[i].put("LogResolution", raf.readInt());
			cutMaps[i].put("DopplerResolution", raf.readInt());
			cutMaps[i].put("MaximumRange#1", raf.readInt());
			cutMaps[i].put("MaximumRange#2", raf.readInt());
			cutMaps[i].put("StartRange", raf.readInt());
			cutMaps[i].put("Sample#1", raf.readInt());
			cutMaps[i].put("Sample#2", raf.readInt());
			cutMaps[i].put("PhaseMode", raf.readInt());
			cutMaps[i].put("AtmosphericLoss", raf.readFloat());
			cutMaps[i].put("NyquistSpeed", raf.readFloat());
			cutMaps[i].put("MomentsMask", raf.readLong());
			cutMaps[i].put("MomentsSizeMask", raf.readLong());
			cutMaps[i].put("MiscFilterMask", raf.readInt());
			cutMaps[i].put("SQIThreshold", raf.readFloat());
			cutMaps[i].put("SIGThreshold", raf.readFloat());
			cutMaps[i].put("CSRThreshold", raf.readFloat());
			cutMaps[i].put("LOGThreshold", raf.readFloat());
			cutMaps[i].put("CPAThreshold", raf.readFloat());
			cutMaps[i].put("PMIThreshold", raf.readFloat());
			cutMaps[i].put("DPLOGThreshold", raf.readFloat());
			raf.skipBytes(4);
			cutMaps[i].put("dBTMask", raf.readInt());
			cutMaps[i].put("dBZMask", raf.readInt());
			cutMaps[i].put("VelocityMask", raf.readInt());
			cutMaps[i].put("Spectrum WidthMask", raf.readInt());
			cutMaps[i].put("DPMask", raf.readInt());
			raf.skipBytes(16);
			cutMaps[i].put("Direction", raf.readInt());
			cutMaps[i].put("GroundClutterClassifierType", raf.readShort());
			cutMaps[i].put("GroundClutterFilterType", raf.readShort());
			cutMaps[i].put("GroundClutterFilterNotchWidth", raf.readShort());
			cutMaps[i].put("GroundClutterFilterWindow", raf.readShort());
			raf.skipBytes(72);
		}
		int antennaHeight = (int) commonMap.get("AntennaHeight");
		RadarBase.antennaHeight = antennaHeight / 1000.0f;
		RadarBase.latitude = (float) commonMap.get("Latitude");
		RadarBase.longitude = (float) commonMap.get("Longitude");
		RadarBase.radarName = (String) commonMap.get("SiteName");
		RadarBase.siteCode = (String) commonMap.get("SiteCode");
		return true;
	}

	private void readParams(int cutNum) {
		this.surveillanceRange = (short) (int) cutMaps[cutNum].get("StartRange");
		this.dopplerRange = this.surveillanceRange;
		this.surveillanceInterval = (short) (int) cutMaps[cutNum].get("LogResolution");
		this.dopplerInterval = (short) (int) cutMaps[cutNum].get("DopplerResolution");
		int rnum = this.getCutStart(cutNum);
		Moment moment = radials[rnum].moments[MOMENT_INDEX[DBZ]];
		if (moment != null) {
			this.surveillanceBins = moment.binNumber;
		} else {
			this.surveillanceBins = 0;
		}
		moment = radials[rnum].moments[MOMENT_INDEX[V]];
		if (moment != null) {
			this.dopplerBins = moment.binNumber;
		} else {
			this.dopplerBins = 0;
		}
	}

	public short getBinCount(int moment) {
		Moment m = radials[recordNum].moments[MOMENT_INDEX[moment]];
		if (m != null) {
			return m.binNumber;
		}
		return 0;
	}

}
