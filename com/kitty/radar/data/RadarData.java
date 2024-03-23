package com.kitty.radar.data;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.kitty.component.gui.domain.Option;
import com.kitty.component.third.RandomAccessFile;
import com.kitty.component.third.TreeMapExt;

/**
 * 雷达文件读取基类。
 */
public abstract class RadarData {

	// ============================ 支持的雷达格式 ============================
	public static final Option RADAR_FORMAT_CA_CB = new Option("CA/CB", 3);

	public static final Option RADAR_FORMAT_SA_SB = new Option("SA/SB", 2);

	public static final Option RADAR_FORMAT_SC = new Option("SC", 1);

	public static final Option RADAR_FORMAT_FMT = new Option("标准格式", 0);

	// ============================ 常量定义 ============================

	public static final int DBT = 1;
	
	public static final int DBZ = 2;

	public static final int V = 3;

	public static final int W = 4;
	
	public static final int ZDR = 7;
	
	public static final int KDP = 11;
	
	public static final int DP = 10;
	
	public static final int CC = 9;
	
	public static final int SNRH = 16;

	public static final byte DOPPLER_RESOLUTION_LOW = 4; // 多普勒速度分辨率，1米/秒

	public static final byte DOPPLER_RESOLUTION_HIGH = 2; // 多普勒速度分辨率，0.5米/秒

	public static final short MAX_FILE_RECORDS = 10000; // 文件中的最大记录数

	public static final byte MAX_FILE_CUTS = 20; // 文件中的最大仰角数

	public static final short MAX_CUT_RECORDS = 600; // 每个仰角中的最大记录数

	public static final float VERTICAL_BEAM_WIDTH = 1.0f; // 垂直波束宽度，单位：度

	public static final float NO_DATA = -3276.8f; // 无效的数据

	// ============================ 子类必须赋值的变量 ============================

	protected short surveillanceRange = 0; // 反射率数据第一个距离库的实际距离(单位: 米)

	protected short dopplerRange = 0; // 多普勒数据第一个距离库的实际距离(单位: 米)

	protected short surveillanceInterval = 0; // 反射率数据的距离库长（单位：米）

	protected short dopplerInterval = 0; // 多普勒数据的距离库长（单位：米）

	protected short surveillanceBins = 0; // 反射率的距离库数

	protected short dopplerBins = 0; // 多普勒的距离库数

	public short resolution = DOPPLER_RESOLUTION_HIGH; // 多普勒速度分辨率 2：表示0.5米/秒；4：表示1.0米/秒

	public String vcp = "21";

	protected short[] cutStarts = new short[MAX_FILE_CUTS]; // 缓存所有仰角的起始recordNum，从0开始

	protected double[] azimuths = new double[MAX_FILE_RECORDS]; // 缓存所有数据的方位角，单位：度

	protected byte cutNumber = 0; // 文件包含的仰角数
	
	/**
	 * 文件中包含的数据类型
	 */
	protected Set<Integer> dataTypeSet = new HashSet<Integer>();

	// ============================ 变量定义 ============================

	public short radarType;

	public double beamWidth = 1;

	protected String srcFileName;

	protected int cutRecordNum = -1;

	public RandomAccessFile raf;

	public RadarData() {
		for (int i = 0; i < cutStarts.length; i++) {
			cutStarts[i] = -1;
		}
		dataTypeSet.add(RadarData.DBZ);
		dataTypeSet.add(RadarData.V);
		dataTypeSet.add(RadarData.W);
	}

	// ======================== Abstract Functions =========================

	/**
	 * 取得Record采集时间，如果无法获取Record采集时间，则返回文件采集时间
	 * 
	 * @return
	 */
	public abstract Date getFileTime();

	/**
	 * 取得指定仰角，单位：度。子类应缓存此方法
	 * 
	 * @param cutNum
	 * @return
	 */
	public abstract double getElevation(int cutNum);

	/**
	 * 读取指定record的header信息 注意：子类必须实现并调用此方法
	 * 
	 * @param recordNum
	 * @return true：读取成功；false：读取错误或到达文件末尾
	 */
	public boolean readHeader(int recordNum) {
		this.cutRecordNum = -1;
		return true;
	}

	/**
	 * 读取指定record数据 注意：子类必须实现并调用此方法
	 * 
	 * @param recordNum
	 * @return true：读取成功；false：读取错误或到达文件末尾
	 */
	public boolean readRecord(int recordNum) {
		this.cutRecordNum = -1;
		return true;
	}

	/**
	 * 读取从recordNum开始的仰角中的所有数据 注意：子类必须实现并调用此方法
	 * 
	 * @param recordNum
	 * @return 读取的record个数
	 */
	public int readCut(int recordNum) {
		this.cutRecordNum = recordNum;
		return 0;
	}

	/**
	 * 取得当前Record的方位角，单位：度
	 * 
	 * @return
	 */
	public abstract double getAzimuth();

	/**
	 * 取得当前Record的仰角，单位：度
	 * 
	 * @return
	 */
	public abstract double getElevation();

	/**
	 * 取得原始基数据值
	 * 
	 * @param moment
	 * @param radial 仰角中的radialNum，从0开始
	 * @param bin    库数，从0开始
	 * @return 如果没有相应的数据，返回0
	 */
	public abstract int getBinaryValue(int moment, int radial, int bin);

	/**
	 * 取得基数据变量值
	 * 
	 * @param moment
	 * @param radial 仰角中的radialNum，从0开始
	 * @param bin    库数，从0开始
	 * @return 如果没有相应的数据，返回NO_DATA
	 */
	public abstract float getMomentValue(int moment, int radial, int bin);

	/**
	 * 打开雷达文件
	 * 
	 * @param file
	 * @return true：打开成功；false：错误的数据格式
	 */
	public abstract boolean open(File file);

	// ======================== Common Functions =========================

	public Set<Integer> getDataTypeSet() {
		return this.dataTypeSet;
	}
	
	/**
	 * 取得指定moment的第一个距离库的实际距离，单位：km
	 * 
	 * @param moment
	 * @return
	 */
	public double getRangeToFirstBin(int moment) {
		if (moment == DBZ) {
			return surveillanceRange / 1000.0;
		} else {
			return dopplerRange / 1000.0;
		}
	}

	/**
	 * 取得指定moment的距离库长，单位：km
	 * 
	 * @param moment
	 * @return
	 */
	public double getBinInterval(int moment) {
		if (moment == DBZ) {
			return surveillanceInterval / 1000.0;
		} else {
			return dopplerInterval / 1000.0;
		}
	}

	/**
	 * 取得指定moment的距离库数
	 * 
	 * @param moment
	 * @return
	 */
	public short getBinCount(int moment) {
		if (moment == DBZ) {
			return surveillanceBins;
		} else {
			return dopplerBins;
		}
	}

	/**
	 * 取得雷达文件名
	 * 
	 * @return
	 */
	public String getSrcFileName() {
		return srcFileName;
	}

	/**
	 * 设定雷达文件名
	 * 
	 * @param srcFileName
	 */
	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	/**
	 * 取得指定record的方位角，单位：度，如果没有雷达数据，返回-1
	 * 
	 * @param recordNum
	 * @return
	 */
	public double getAzimuth(int recordNum) {
		return azimuths[recordNum];
	}

	/**
	 * 取得指定仰角中的起始recordNum，如果该仰角没有数据，返回-1
	 * 
	 * @param cutNum
	 * @return
	 */
	public int getCutStart(int cutNum) {
		if (cutNum >= cutStarts.length) {
			return -1;
		}
		return cutStarts[cutNum];
	}

	public int getCutNum(int recordNum) {
		for (int i = cutStarts.length - 1; i >= 0; i--) {
			if (cutStarts[i] != -1 && recordNum >= cutStarts[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 取得文件中的仰角数
	 * 
	 * @return
	 */
	public int getCutNumber() {
		return cutNumber;
	}

	/**
	 * 取得基数据变量值 注意：此方法支持延迟调用
	 * 
	 * @param moment
	 * @param cutNum
	 * @param azimuth 方位角，单位：度
	 * @param range   距离，单位：km
	 * @return 如果没有相应的数据，返回NO_DATA
	 */
	public float getMomentValue(int moment, int cutNum, double azimuth, double range) {
		int startRecordNum = this.getCutStart(cutNum);
		if (startRecordNum != this.cutRecordNum) { // 延迟调用时如果数据发生了变化，重新读取
			this.readCut(startRecordNum);
		}
		int endRecordNum = -1;
		if (cutNum < this.cutStarts.length - 1) {
			endRecordNum = this.getCutStart(cutNum + 1);
			if (endRecordNum < 0) {
				endRecordNum = this.azimuths.length;
			}
		} else {
			endRecordNum = this.azimuths.length;
		}
		double interval = Double.MAX_VALUE;
		int radial = -1;
		for (int i = startRecordNum; i < endRecordNum; i++) {
			double diff = Math.abs(this.getAzimuth(i) - azimuth);
			if (diff < interval) {
				interval = diff;
				radial = i;
			}
		}
		radial -= startRecordNum;
		interval = getBinInterval(moment);
		int bin = (int) ((range - interval / 2.0 - getRangeToFirstBin(moment)) / interval);
		if (bin >= this.getBinCount(moment)) {
			return NO_DATA;
		}
		return this.getMomentValue(moment, radial, bin);
	}
	
	/**
	 * 关闭雷达文件
	 */
	public void close() {
		if (raf != null) {
			try {
				raf.close();
			} catch (Exception e) {
			}
			raf = null;
		}
	}

	/**
	 * 读取文件中所有仰角的数据。
	 * 
	 * @param moment
	 * @return 第一层：所有仰角数据，每个仰角为<TreeMapExt>；第二层：所有方位角数据，每个方位角为<short[]>
	 */
	public TreeMapExt readFile(int moment) {
		TreeMapExt map = new TreeMapExt();
		for (int i = 0; i < this.getCutNumber(); i++) {
			int recordNum = this.getCutStart(i);
			this.readHeader(recordNum);
			int bins = this.getBinCount(moment);
			if (bins <= 0) {
				continue;
			}
			TreeMapExt map1 = new TreeMapExt();
			map.put(this.getElevation(i), map1);
			int number = this.readCut(recordNum);
			for (int j = 0; j < number; j++) {
				double azimuth = this.getAzimuth(recordNum + j);
				if (azimuth < 0) {
					continue;
				}
				short[] values = new short[bins];
				map1.put(azimuth, values);
				for (int bin = 0; bin < bins; bin++) {
					values[bin] = (short) this.getBinaryValue(moment, j, bin);
				}
			}
		}
		return map;
	}

	/**
	 * 取得某一点的数据值（4点双线性插值法）
	 * 
	 * @param map
	 * @param azimuth
	 * @param range
	 * @param binInterval
	 * @param rangeToFirst
	 * @return 如果没有相应的数据，返回0
	 */
	public short getPointValue4I(Object map, double azimuth, double range, double binInterval, double rangeToFirst) {
		TreeMapExt tme = (TreeMapExt) map;
		Entry[] e = tme.closest2Entry(azimuth);
		if (e != null) {
			double d = (range - rangeToFirst) / binInterval - 1;
			int i = (int) d;
			double ii = d - i;
			if (e[0] == e[1]) {
				if (ii <= 0) {
					return this.getValue(e[0], i);
				} else {
					short v = this.getValue(e[0], i);
					return (short) Math.round(((this.getValue(e[0], i + 1) - v) * ii + v));
				}
			} else {
				if (e[0] == null) {
					e[0] = tme.lastEntry();
				} else if (e[1] == null) {
					e[1] = tme.firstEntry();
				}
				double ai = (azimuth - ((Double) e[0].getKey()).doubleValue())
						/ (((Double) e[1].getKey()).doubleValue() - ((Double) e[0].getKey()).doubleValue());
				if (ii <= 0) {
					short v = this.getValue(e[0], i);
					return (short) Math.round((this.getValue(e[1], i) - v) * ai + v);
				} else {
					short v = this.getValue(e[0], i);
					short v1 = (short) Math.round((this.getValue(e[0], i + 1) - v) * ii + v);
					v = this.getValue(e[1], i);
					short v2 = (short) Math.round((this.getValue(e[1], i + 1) - v) * ii + v);
					return (short) Math.round((v2 - v1) * ai + v1);
				}
			}
		}
		return 0;
	}

	private short getValue(Entry e, int i) {
		short[] values = (short[]) e.getValue();
		if (i >= 0 && i < values.length) {
			return values[i];
		}
		return 0;
	}
	public boolean readRcecordnum(int recordNum)
	{
		this.cutRecordNum = -1;
		return true;
	}


	public static boolean isValid(double value) {
		return value > -3270;
	}
	
}
