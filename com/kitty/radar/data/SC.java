package com.kitty.radar.data;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;


import com.kitty.component.third.RandomAccessFile;
import com.kitty.radar.RadarBase;
import com.kitty.radar.gui.GUIManager;

public class SC extends RadarData {

	public SC(RadarBase radarBase) {
		super(radarBase);
	}

	RadarDataFileHeader header = new RadarDataFileHeader();

	private int recordNum; // 保存最近读取的recordNum

	private float[][][] values; // 保存反射率、速度、谱宽数据值

	public long headerOffset = 1024; // 数据头偏移地址

	@Override
	public Date getFileTime() {
		Calendar cal = Calendar.getInstance();		
		cal.set(header.radarObservationInfo.SYear, header.radarObservationInfo.SMonth - 1,
				header.radarObservationInfo.SDay, header.radarObservationInfo.SHour,
				header.radarObservationInfo.SMinute, header.radarObservationInfo.SSecond);
		return cal.getTime();
	}

	@Override
	public double getElevation(int cutNum) {
		return header.radarObservationInfo.LayerInfo[cutNum].Swangles / 100.0;
	}

	@Override
	public double getAzimuth() {
		return this.getAzimuth(recordNum);
	}

	@Override
	public double getElevation() {
		return this.getElevation(this.getCutNum(recordNum));
	}

	public int getCutNum(int recordNum) {
		for (int i = cutStarts.length - 1; i >= 0; i--) {
			if (cutStarts[i] != -1 && recordNum >= cutStarts[i]) {
				return i;
			}
		}
		return -1;
	}

	public int readCut(int recordNum) {
		super.readCut(recordNum);
		try {
			this.recordNum = recordNum;
			int cutNum = this.getCutNum(recordNum);
			int offset = 0;
			for (int i = 0; i < cutNum; i++) {
				offset += header.radarObservationInfo.LayerInfo[i].recordnumber;
			}
			raf.seek(this.headerOffset + offset * (8 + surveillanceBins * 4));
			int numberInCut = header.radarObservationInfo.LayerInfo[cutNum].recordnumber; // record number某层径向数
			for (int i = 0; i < numberInCut; i++) {
				readData(cutNum, i);
			}
			return numberInCut;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean readHeader(int recordNum) {
		super.readHeader(recordNum);
		this.recordNum = recordNum;
		this.readParams(this.getCutNum(recordNum));
		return true;
	}

	private void readParams(int cutNum) {
		this.surveillanceInterval = (short) (header.radarObservationInfo.LayerInfo[cutNum].binWidth / 10);
		this.dopplerInterval = this.surveillanceInterval;
		this.surveillanceBins = 998;
		this.dopplerBins = this.surveillanceBins;
	}

	public boolean readRecord(int recordNum) {
		super.readRecord(recordNum);
		this.recordNum = recordNum;
		int cutNum = this.getCutNum(recordNum);
		this.readParams(cutNum);
		try {
			raf.seek(this.headerOffset + recordNum * (8 + surveillanceBins * 4));
			readData(cutNum, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void readData(int cutNum, int cutRecordNum) throws IOException {
		int startaz = raf.readUnsignedShort();
		int startel = raf.readUnsignedShort();
		int endaz = raf.readUnsignedShort();
		int endel = raf.readUnsignedShort();
		double maxV = header.radarObservationInfo.LayerInfo[cutNum].MaxV / 100.0;
		for (int j = 0; j < surveillanceBins; j++) {
			short value = (short) raf.readUnsignedByte();
			if (value == 0) {
				values[cutRecordNum][j][0] = RadarData.NO_DATA;
			} else {
				values[cutRecordNum][j][0] = (float) ((value - 64) / 2.0);
			}
			value = (short) raf.readUnsignedByte();
			if (value == 0) {
				values[cutRecordNum][j][1] = RadarData.NO_DATA;
			} else {
				values[cutRecordNum][j][1] = (float) (maxV * (value - 128) / 128.0);
			}
			value = (short) raf.readUnsignedByte();
			if (value == 0) {
				values[cutRecordNum][j][2] = RadarData.NO_DATA;
			} else {
				values[cutRecordNum][j][2] = (float) ((value - 64) / 2.0);
			}
			value = (short) raf.readUnsignedByte();
			if (value == 0) {
				values[cutRecordNum][j][3] = RadarData.NO_DATA;
			} else {
				values[cutRecordNum][j][3] = (float) (maxV * value / 512.0);
			}
		}
	}

	@Override
	public int getBinaryValue(int moment, int radial, int bin) {
		int vb = SA_SB.momentToBinary(this.getMomentValue(moment, radial, bin), moment, this.resolution);
		if (vb < 0 || vb > 255) {
			vb = 0;
		}
		return vb;
	}

	@Override
	public float getMomentValue(int moment, int radial, int bin) {
		float value;
		if (moment == DBZ) {
			value = values[radial][bin][0];
		} else if (moment == V) {
			value = values[radial][bin][1];
		} else {
			value = values[radial][bin][3];
		}
		return value;
	}

	@Override
	public boolean open(File file) {
		try {
			if (srcFileName == null) {
				srcFileName = file.getName();
			}
			raf = new RandomAccessFile(file.getPath(), "r");
			raf.order(RandomAccessFile.LITTLE_ENDIAN);//编码方式
			readFileHeader();			
			//JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+header.radarSiteInfo.RadarType);
			if (header.radarSiteInfo.RadarType == null || header.radarSiteInfo.RadarType.indexOf("SC") == -1) {
				this.close();
				return false;
			}
	        RadarBase RadarBase = GUIManager.activeMainPanel.getRadarBase();
			RadarBase.setLatitude((float)header.radarSiteInfo.LatitudeValue/100);
			RadarBase.setLongitude((float)header.radarSiteInfo.LongitudeValue/100);
			RadarBase.radarName = (String)header.radarSiteInfo.Station;
			RadarBase.siteCode = (String)header.radarSiteInfo.StationNumber;
			initParams();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			this.close();
		}
		return false;
	}

	private void initParams() {
		this.cutNumber = (byte) (header.radarObservationInfo.SType - 100);
		radarBase.setAntennaHeight((float) (header.radarSiteInfo.Height / 1000000.0));
		this.readParams(0);

		// 初始化cutStarts
		cutStarts[0] = 0;
		for (int i = 1; i < cutNumber; i++) {
			cutStarts[i] = (short) (cutStarts[i - 1] + header.radarObservationInfo.LayerInfo[i - 1].recordnumber);
		}

		// 初始化azimuths
		this.beamWidth = 360.0 / header.radarObservationInfo.LayerInfo[0].recordnumber;
		for (int i = 0; i < this.cutNumber; i++) {
			for (int j = 0; j < header.radarObservationInfo.LayerInfo[0].recordnumber; j++) {
				azimuths[i * header.radarObservationInfo.LayerInfo[0].recordnumber + j] = j * beamWidth;
			}
		}

		values = new float[MAX_CUT_RECORDS][surveillanceBins][4];
	}

	private void readFileHeader() throws IOException {
		header.radarSiteInfo.Country = raf.readString(30);
		header.radarSiteInfo.Province = raf.readString(20);
		header.radarSiteInfo.Station = raf.readString(40);
		header.radarSiteInfo.StationNumber = raf.readString(10).trim();
		header.radarSiteInfo.RadarType = raf.readString(20);
		header.radarSiteInfo.Longitude = raf.readString(16);
		header.radarSiteInfo.Latitude = raf.readString(16);
		header.radarSiteInfo.LongitudeValue = raf.readInt();
		header.radarSiteInfo.LatitudeValue = raf.readInt();
		header.radarSiteInfo.Height = raf.readInt();
		header.radarSiteInfo.MaxAngle = raf.readShort();
		header.radarSiteInfo.OptiAngle = raf.readShort();
		header.radarSiteInfo.MangFreq = raf.readShort();
		header.radarPerformanceInfo.AntennaG = raf.readInt();
		header.radarPerformanceInfo.VerBeamW = raf.readUnsignedShort();
		header.radarPerformanceInfo.HorBeamW = raf.readUnsignedShort();
		header.radarPerformanceInfo.Polarizations = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.SideLobe = raf.readUnsignedByte();
		header.radarPerformanceInfo.Power = raf.readInt();
		header.radarPerformanceInfo.WaveLength = raf.readInt();
		header.radarPerformanceInfo.LogA = raf.readUnsignedShort();
		header.radarPerformanceInfo.LineA = raf.readUnsignedShort();
		header.radarPerformanceInfo.AGCP = raf.readUnsignedShort();
		header.radarPerformanceInfo.ClutterT = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.VelocityP = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.FilterP = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.NoiseT = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.SQIT = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.IntensityC = (short) raf.readUnsignedByte();
		header.radarPerformanceInfo.IntensityR = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SType = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SYear = raf.readUnsignedShort();
		header.radarObservationInfo.SMonth = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SDay = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SHour = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SMinute = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SSecond = (short) raf.readUnsignedByte();
		header.radarObservationInfo.TimeP = (short) raf.readUnsignedByte();
		header.radarObservationInfo.SMillisecond = raf.readUnsignedInt();
		header.radarObservationInfo.Calibration = (short) raf.readUnsignedByte();
		header.radarObservationInfo.IntensityI = (short) raf.readUnsignedByte();
		header.radarObservationInfo.VelocityP = (short) raf.readUnsignedByte();
		for (int i = 0; i < header.radarObservationInfo.LayerInfo.length; i++) {
			header.radarObservationInfo.LayerInfo[i] = new LayerParam();
			header.radarObservationInfo.LayerInfo[i].ambiguousp = (short) raf.readUnsignedByte();
			header.radarObservationInfo.LayerInfo[i].Arotate = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].Prf1 = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].Prf2 = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].spulseW = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].MaxV = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].MaxL = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].binWidth = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].binnumber = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].recordnumber = raf.readUnsignedShort();
			header.radarObservationInfo.LayerInfo[i].Swangles = raf.readShort();
		}
		header.radarObservationInfo.RHIA = raf.readUnsignedShort();
		header.radarObservationInfo.RHIL = raf.readShort();
		header.radarObservationInfo.RHIH = raf.readShort();
		header.radarObservationInfo.EYear = raf.readUnsignedShort();
		header.radarObservationInfo.EMonth = (short) raf.readUnsignedByte();
		header.radarObservationInfo.EDay = (short) raf.readUnsignedByte();
		header.radarObservationInfo.EHour = (short) raf.readUnsignedByte();
		header.radarObservationInfo.EMinute = (short) raf.readUnsignedByte();
		header.radarObservationInfo.ESecond = (short) raf.readUnsignedByte();
		header.radarObservationInfo.ETenth = (short) raf.readUnsignedByte();
		header.reserved = raf.readBytes(header.reserved.length);
	}

	/**
	 * 文件头
	 */
	class RadarDataFileHeader {
		RadarSite radarSiteInfo = new RadarSite();
		RadarPerformanceParam radarPerformanceInfo = new RadarPerformanceParam();
		RadarObservationParam radarObservationInfo = new RadarObservationParam();
		byte[] reserved = new byte[163];
	}

	/**
	 * 站点基本情况
	 */
	class RadarSite {
		String Country; // 国家名，文本格式输入
		String Province; // 省名，文本格式输入
		String Station; // 站名，文本格式输入
		String StationNumber; // 区站号，文本格式输入
		String RadarType; // 雷达型号，文本格式输入
		String Longitude; // 天线所在经纬度，文本格式输入
		String Latitude; // 天线所在纬度，文本格式输入
		long LongitudeValue; // 天线所在经度的数值，以1/1000度为计数单位
								// 东经（E）为正，西经（W）为负
		long LatitudeValue; // 天线所在纬度的数值，以1/1000度为计数单位
							// 北纬（N）为正，南纬（S）为负
		long Height; // 天线海拔高度，以毫米为计数单位
		short MaxAngle; // 测站周围地物最大遮挡仰角，以1/100度为计数单位
		short OptiAngle; // 测站的最佳观测仰角（地物回波强度<10dBZ），以1/100度为计数单位
		short MangFreq; // 雷达工作频点（可由此值计算波长）
	}

	class RadarPerformanceParam {
		long AntennaG; // 天线增益，以0.001dBZ为计数单位
		int VerBeamW; // 垂直波束宽度，以1/100度为计数单位
		int HorBeamW; // 水平波束宽度，以1/100读为计数单位
		short Polarizations; // 偏振情况
								// 0=水平
								// 1=垂直
								// 2=双线偏振
								// 3=园偏振
								// 4=其他
		int SideLobe; // 第一旁瓣，以0.01dBZ为计数单位
		long Power; // 雷达脉冲峰值功率，以瓦为单位
		long WaveLength; // 波长，以微米为计数单位
		int LogA; // 对数接收机动态范围，以0.01dBZ为计数单位
		int LineA; // 线性接收机动态范围，以0.01dBZ为计数单位
		int AGCP; // AGC延迟量，以微秒为计数单位
		short ClutterT; // 杂波消除阈值，计数单位为0.01dB

		short VelocityP; // 速度处理方式
							// 0=无速度处理
							// 1=PPP
							// 2=FFT
							// 3＝RANDP随即编码
		// 4=PPP＋RANDP随即编码
		// 5=FFT＋RANDP随即编码
		short FilterP; // 地物杂波消除方式
						// 0=无地物杂波消除
						// 1=地物杂波扣除法
						// 2=地物杂波+滤波器处理
						// 3=滤波器处理
						// 4=谱分析处理
						// 5=其他处理法
		short NoiseT; // 噪声消除阈值（0-255）
		short SQIT; // SQI阈值，以0.01为计数单位
		short IntensityC; // RVP强度值估算采用通道
							// 1=对数通道
							// 2=线性通道
		short IntensityR; // 强度估算是否进行了距离订正
							// 0=无
							// 1=以进行了距离订正
	}

	class RadarObservationParam {
		short SType; // 扫描方式
		// 1=RHI
		// 10=PPI
		// 1XX=VOL，XX为层数
		int SYear; // 观测记录开始时间的年（2000-）
		short SMonth; // 观测记录开始时间的月（1-12）
		short SDay; // 观测记录开始时间的日（1-31）
		short SHour; // 观测记录开始时间的时（00-23）
		short SMinute; // 观测记录开始时间的分（00-59）
		short SSecond; // 观测记录开始时间的秒（00-59）
		short TimeP; // 时间来源
		// 0=计算机时钟，但一天内未进行对时
		// 1=计算机时钟，一天内已进行对时
		// 2=GPS
		// 3=其他
		long SMillisecond; // 秒的小数位（计数单位微秒）
		short Calibration; // 标校状态
		// 0=无标校
		// 1=自动标校
		// 2=一星期内人工标校
		// 3=一月内人工标校
		// 其他码不用
		short IntensityI; // 强度积分次数（32-128）
		short VelocityP; // 速度处理样本（31-255）（样本数减一）
		LayerParam[] LayerInfo = new LayerParam[30]; // 层参数结构（各层扫描状态设置）
		int RHIA;
		// RHI时的所在方位角，计数单位为1/100度，作PPI和立体扫描时不用
		short RHIL;
		// RHI时的最低仰角，计数单位为1/100度，作其他扫描时不用
		short RHIH;
		// RHI时的最高仰角，计数单位为1/100度，做其他扫描时不用
		int EYear; // 观测记录结束时间的年（2000-）
		short EMonth; // 观测记录结束时间的月（1-12）
		short EDay; // 观测记录结束时间的日（1-31）
		short EHour; // 观测记录结束时间的时（00-23）
		short EMinute; // 观测记录结束时间的分（00-59）
		short ESecond; // 观测记录结束时间的秒（00-59）
		short ETenth; // 观测记录结束时间的1/100秒（00-99）
	}

	class LayerParam {
		short ambiguousp; // 本层退模糊状态
		// 0 = 无退模糊状态
		// 1 = 软件退模糊
		// 2 = 双T退模糊
		// 3 = 批式退模糊
		// 4 = 双T + 软件退模糊
		// 5 = 批式 + 软件退模糊
		// 6 = 双PPI退模糊
		// 9 = 其他方式
		int Arotate; // 本层天线转速,计数单位:0.01度/秒
		int Prf1; // 本层的第一种脉冲重复频率,计数单位: 1/10 Hz
		int Prf2; // 本层的第二种脉冲重复频率,计数单位: 1/10 Hz
		// (通过重复频率1、重复频率2和磁控管频率可计算最大速度：
		// if(Prf2==0|) Vmax=30000.0*Prf1/MangFreq*400.0
		// else Vmax=30000.0*Prf1*Prf2/MangFreq*400.0*abs(Prf2-Prf1)
		int spulseW; // 本层的脉冲宽度,计数单位: 微秒
		int MaxV; // 本层的最大可测速度,计数单位: 厘米/秒
		int MaxL; // 本层的最大可测距离，以10米为计数单位
		int binWidth; // 本层数据的库长，以分米为计数单位
		int binnumber; // 本层每个径向的库数
		int recordnumber; // 本层径向数(记录个数)
		short Swangles; // 本层的仰角，计数单位 ：1/100度
	}

}
