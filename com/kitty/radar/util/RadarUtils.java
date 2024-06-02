package com.kitty.radar.util;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JOptionPane;

import com.kitty.radar.RadarBase;
import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.color.CCColor;
import com.kitty.radar.color.DPColor;
import com.kitty.radar.color.EtColor;
import com.kitty.radar.color.HpColor;
import com.kitty.radar.color.KDPColor;
import com.kitty.radar.color.LwColor;
import com.kitty.radar.color.RadarColor;
import com.kitty.radar.color.RefClearColor;
import com.kitty.radar.color.RefPrecipColor;
import com.kitty.radar.color.SNRHColor;
import com.kitty.radar.color.SpwColor;
import com.kitty.radar.color.VelHighColor;
import com.kitty.radar.color.VelLowColor;
import com.kitty.radar.color.VilColor;
import com.kitty.radar.color.ZDRColor;
import com.kitty.radar.data.FMT;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.data.SC;
import com.kitty.radar.data.CA_CB;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.GridValue;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.domain.ResolutionOption;
import com.kitty.radar.gui.ExportSetDialog;
import com.kitty.radar.gui.GUIManager;

public class RadarUtils {

	private static double[] sin = new double[720];

	private static double[] cos = new double[720];

	static { // 初始化sin、cos缓存，精度0.5度
		for (int i = 0; i < 720; i++) {
			sin[i] = Math.sin((i / 2.0 + 90.0) * Math.PI / 180.0);
			cos[i] = Math.cos((i / 2.0 - 90.0) * Math.PI / 180.0);
		}
	}

	public static double sin(double ang) {
		int i = (int) Math.round(2 * ang);
		if (i >= 720) {
			i -= 720;
		}
		if (i < 0) {
			i += 720;
		}
		return sin[i];
	}

	public static double cos(double ang) {
		int i = (int) Math.round(2 * ang);
		if (i >= 720) {
			i -= 720;
		}
		if (i < 0) {
			i += 720;
		}
		return cos[i];
	}

	public static RadarColor getRadarColor(int moment, RadarBase RadarBase) {
		if (moment == CommonProps.MOMENT_DBT) {
			moment = CommonProps.MOMENT_R;
		}
		switch (moment) {
		case CommonProps.MOMENT_R:
			if ("31".equals(RadarBase.vcp) || "32".equals(RadarBase.vcp) || "31D".equals(RadarBase.vcp)
					|| "32D".equals(RadarBase.vcp)) {
				return RefClearColor.color;
			}
			return RefPrecipColor.color;
		case CommonProps.MOMENT_V:
			if (RadarBase.resolution == RadarData.DOPPLER_RESOLUTION_LOW) {
				return VelLowColor.color;
			}
			return VelHighColor.color;
		case CommonProps.MOMENT_W:
			return SpwColor.color;
		case CommonProps.MOMENT_LW:
			return LwColor.color;
		case CommonProps.MOMENT_VIL:
			return VilColor.color;
		case CommonProps.MOMENT_ET:
			return EtColor.color;
		case CommonProps.MOMENT_HP:
			return HpColor.color;
		case CommonProps.MOMENT_ZDR:
			return ZDRColor.color;
		case CommonProps.MOMENT_KDP:
			return KDPColor.color;
		case CommonProps.MOMENT_DP:
			return DPColor.color;
		case CommonProps.MOMENT_CC:
			return CCColor.color;
		case CommonProps.MOMENT_SNRH:
			return SNRHColor.color;
		}
		return RefPrecipColor.color;
	}

	public static RadarData createRadarData(RadarBase radarBase) {
		if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SA_SB.getValue()) {
			//JOptionPane.showMessageDialog(null, "消息提示tjjjjt1：");
			return new SA_SB(radarBase);
		} else if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SC.getValue()) {
			//JOptionPane.showMessageDialog(null, "消息提示tjjjjt2：");
			return new SC(radarBase);
		} else if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_CA_CB.getValue()) {
			//JOptionPane.showMessageDialog(null, "消息提示tjjjjt2：");
			return new CA_CB(radarBase);
		} else if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_FMT.getValue()) {
			return new FMT(radarBase);
		}
		return new FMT(radarBase);
	}

	public static String getRadarFormatLabel() {
		if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SA_SB.getValue()) {
			return RadarData.RADAR_FORMAT_SA_SB.getName();
		} else if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SC
				.getValue()) {
			return RadarData.RADAR_FORMAT_SC.getName();
		} else if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_CA_CB
				.getValue()) {
			return RadarData.RADAR_FORMAT_CA_CB.getName();
		} else if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_FMT.getValue()) {
			return RadarData.RADAR_FORMAT_FMT.getName();
		}
		return RadarData.RADAR_FORMAT_SC.getName();
	}

	public static int getRadarRadius() {
		if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SC.getValue()) {
			return 230;
		}
		if (GUIManager.activeMainPanel!=null) {
			RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
			if (radarBase.l2 != null) {
				if (radarBase.l2.radarType == 33 || radarBase.l2.radarType == 38
						|| radarBase.l2.radarType == 34 || radarBase.l2.radarType == 39) {
					return 230;
				}
			}
		}
		return 360;
	}


	public static String getMomentUnitLabel(int moment) {
		String label = "";
		if (moment == CommonProps.MOMENT_R) {
			label = "dBZ";
		} else if (moment == CommonProps.MOMENT_V) {
			label = "m/s";
		} else if (moment == CommonProps.MOMENT_W) {
			label = "m/s";
		} else if (moment == CommonProps.MOMENT_LW) {
			label = "g/m3";
		} else if (moment == CommonProps.MOMENT_VIL) {
			label = "kg/m2";
		} else if (moment == CommonProps.MOMENT_ET) {
			label = "km";
		} else if (moment == CommonProps.MOMENT_HP) {
			label = "%";
		} else if (moment == CommonProps.MOMENT_ZDR) {
			label = "dB";
		} else if (moment == CommonProps.MOMENT_DP) {
			label = "°";
		} else if (moment == CommonProps.MOMENT_KDP) {
			label = "°/km";
		} else if (moment == CommonProps.MOMENT_DBT) {
			label = "dBT";
		} else if (moment == CommonProps.MOMENT_SNRH) {
			label = "dBT";
		} else if (moment == CommonProps.MOMENT_CAPPI) {
			label = "dBZ";
		}
		return label;
	}

	public static String getMomentLabel(RadarBase radarBase) {
		int current = radarBase.currentMoment;
		String label = "";
		if (radarBase.currentMoment == CommonProps.MOMENT_R) {
			if (radarBase.cutNum == CommonProps.MOMENT_CR) {
				label = "组合反射率因子";
			} else {
				label = "反射率因子";
			}
		} else if (current == CommonProps.MOMENT_V) {
			label = "径向速度";
		} else if (current == CommonProps.MOMENT_W) {
			label = "谱宽";
		} else if (current == CommonProps.MOMENT_LW) {
			label = "液态水含量";
		} else if (current == CommonProps.MOMENT_VIL) {
			label = "垂直累积液水含量";
		} else if (current == CommonProps.MOMENT_ET) {
			label = "云顶高度";
		} else if (current == CommonProps.MOMENT_HP) {
			label = "冰雹概率";
		} else if (current == CommonProps.MOMENT_DBT) {
			label = "滤波前反射率";
		} else if (current == CommonProps.MOMENT_KDP) {
			label = "差分相移率";
		} else if (current == CommonProps.MOMENT_DP) {
			label = "差分相移";
		} else if (current == CommonProps.MOMENT_CC) {
			label = "协相关系数";
		} else if (current == CommonProps.MOMENT_ZDR) {
			label = "差分反射率";
		} else if (current == CommonProps.MOMENT_SNRH) {
			label = "水平通道信噪比";
		}
		return label;
	}

	public static String getMomentShort(RadarBase radarBase) {
		int moment = radarBase.currentMoment;
		if (moment == CommonProps.MOMENT_R) {
			if (radarBase.cutNum == CommonProps.MOMENT_CR) {
				return "CR";
			} else {
				return "R";
			}
		} else if (moment == CommonProps.MOMENT_V) {
			return "V";
		} else if (moment == CommonProps.MOMENT_W) {
			return "W";
		} else if (moment == CommonProps.MOMENT_ET) {
			return "ET";
		} else if (moment == CommonProps.MOMENT_LW) {
			return "LW";
		} else if (moment == CommonProps.MOMENT_VIL) {
			return "VIL";
		} else if (moment == CommonProps.MOMENT_DBT) {
			return "dBT";
		} else if (moment == CommonProps.MOMENT_ZDR) {
			return "ZDR";
		} else if (moment == CommonProps.MOMENT_KDP) {
			return "KDP";
		} else if (moment == CommonProps.MOMENT_DP) {
			return "φDP";
		} else if (moment == CommonProps.MOMENT_CC) {
			return "CC";
		} else if (moment == CommonProps.MOMENT_SNRH) {
			return "SNRH";
		} else if (moment == CommonProps.MOMENT_HP) {
			return "HP";
		}
		return "";
	}

	public static int getMomentScale(int moment) {
		if (moment == CommonProps.MOMENT_LW || moment == CommonProps.MOMENT_KDP) {
			return 2;
		}
		return 1;
	}

	public static float getMomentValue(float value,int moment) {
		if (moment == CommonProps.MOMENT_LW
				&& RadarData.isValid(value)) {
			return (float) (0.00344 * Math.pow(Math.pow(10, value / 10.0),
					4.0 / 7.0));
		}
		return value;
	}

	public static ResolutionOption[] getResolution(int moment) {
		ResolutionOption[] options = null;
		if (moment == CommonProps.MOMENT_CR) {
			if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SC
					.getValue()) {
				options = new ResolutionOption[3];
				options[0] = new ResolutionOption("1", "150", 4);
				options[1] = new ResolutionOption("0.6", "150", 5);
				options[2] = new ResolutionOption("0.3", "150", 6);
			} else {
				options = new ResolutionOption[3];
				options[0] = new ResolutionOption("1", "230", 1);
				options[1] = new ResolutionOption("4", "460", 2);
				options[2] = new ResolutionOption("1", "460", 3);
			}
		} else if (moment == CommonProps.MOMENT_VIL) {
			if (RadarBase.radarFormat == RadarData.RADAR_FORMAT_SC
					.getValue()) {
				options = new ResolutionOption[3];
				options[0] = new ResolutionOption("1", "150", 4);
				options[1] = new ResolutionOption("0.6", "150", 5);
				options[2] = new ResolutionOption("0.3", "150", 6);
			} else {
				options = new ResolutionOption[3];
				options[0] = new ResolutionOption("4", "230", 1);
				options[1] = new ResolutionOption("4", "460", 2);
				options[2] = new ResolutionOption("1", "230", 3);
			}
		}
		return options;
	}

	public static String getDistanceUnitLabel() {
		return "km";
	}

	public static short getCutNumberByVCP(String vcp) {
		if ("21".equals(vcp) || "21D".equals(vcp)) {
			return 9;
		} else if ("11".equals(vcp) || "11D".equals(vcp)) {
			return 14;
		}
		return 5;
	}

	public static String getFileTime(RadarBase radarBase) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(radarBase.l2.getFileTime());
	}

	public static String[] splitValues(String text) {
		return text.trim().replaceAll("[\\s,;]+", " ").split(" ");
	}

	public static void writeValues(Writer w, String[] v) throws IOException {
		for (int j = 0; j < v.length; j++) {
			if (j != 0) {
				w.write(CommonProps.FILE_SEPARATOR);
			}
			int n = 7 - v[j].length();
			for (int i = 0; i < n; i++) {
				w.write(' ');
			}
			w.write(v[j]);
		}
		w.write(CommonProps.FILE_RETURN);
	}

	/**
	 * 获取需要插值计算的所有格点。
	 * 
	 * @param gridWidth
	 *            格点宽度，单位：像素
	 * @return
	 */
	public static GridValue[][] getGridsXY(int gridWidth, float range, RadarBase radarBase) {
		int halfWidth = gridWidth / 2;
		int r = PositionUtils.toLength(range, radarBase.getScale_X());
		int n = (r + halfWidth) / gridWidth;
		int l = n * gridWidth - halfWidth;
		GridValue[][] grids = new GridValue[2 * n + 1][2 * n + 1];
		int i = 0;
		for (int x = -l; x <= l; x += gridWidth) {
			int j = 0;
			for (int y = -l; y <= l; y += gridWidth) {
				if (Math.sqrt(x * x + y * y) <= r) {
					int px = x - radarBase.getXoffset() + radarBase.getCenter_X();
					int py = y - radarBase.getYoffset() + radarBase.getCenter_Y();
					if (px >= 0 && py >= 0) {
						grids[i][j] = new GridValue(px, py);
					}
				}
				j++;
			}
			i++;
		}
		return grids;
	}

	public static void exportGrid(Writer w, boolean inArea, int type, RadarBase radarBase)
			throws IOException {
		int moment = radarBase.currentMoment;
		if (ExportSetDialog.coordType == 0) {
			RadarUtils.writeValues(w, new String[] { "A(°)", "R(km)",
					"V(" + RadarUtils.getMomentUnitLabel(moment) + ")" });
		} else {
			RadarUtils.writeValues(w, new String[] { "LNG(°)", "LAT(°)",
					"V(" + RadarUtils.getMomentUnitLabel(moment) + ")" });
		}
		if (radarBase.datas != null) {
			List shapeList = null;
			if (inArea) {
				shapeList = AreaDialog.tableModel.getShapeList();
			}
			GridValue[][] grids = (GridValue[][]) radarBase.datas;
			for (int i = 0; i < grids.length; i++) {
				for (int j = 0; j < grids[i].length; j++) {
					if (grids[i][j] != null) {
						ARCoord c = grids[i][j].getARCoord();
						if (c != null) {
							if (inArea
									&& !AreaDialog.tableModel.contains(
											c.azimuth, c.r, shapeList, radarBase)) {
								continue;
							}
							double value = 0;
							if (type == 1) {
								value = grids[i][j].getDoubleValue();
							} else if (type == 2) {
								value = SA_SB.binaryToMoment(
										grids[i][j].getShortValue(),
										radarBase.active_moment,
										radarBase.resolution);
							}
							String svalue;
							if (RadarData.isValid(value)) {
								svalue = CommonUtils.format(value,
										RadarUtils.getMomentScale(moment));
							} else {
								svalue = "N/A";
							}
							if (ExportSetDialog.coordType == 0) {
								RadarUtils.writeValues(w, new String[] {
										CommonUtils.format(c.azimuth, 1),
										CommonUtils.format(c.r, 1), svalue });
							} else {
								LLCoord llc = PositionUtils.toLLCoord(
										c.azimuth, c.r, radarBase.getLongitude(), radarBase.getLatitude());
								RadarUtils.writeValues(w, new String[] {
										CommonUtils.format(llc.longitude, 6),
										CommonUtils.format(llc.latitude, 6),
										svalue });
							}
						}
					}
				}
			}
		}
	}

}
