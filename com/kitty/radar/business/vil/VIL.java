package com.kitty.radar.business.vil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.NavigableMap;

import com.kitty.radar.RadarBase;
import com.kitty.radar.color.RadarColor;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.GridValue;
import com.kitty.radar.domain.ResolutionOption;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class VIL extends RadarBase {

	private static byte resolution = 1;

	public static float gridWidth = 4f; // 单元格宽度，单位：km

	public static float range = 230;

	public static void display(Graphics2D g,RadarBase radarBase) {
		RadarData l2 = radarBase.l2;
		radarBase.datas = null;
		if (l2 == null) { // 没有选中的文件
			return;
		}
		int w = PositionUtils.toLength(gridWidth,radarBase);//像素单元格宽度
		int hw = w / 2;
		GridValue[][] grids = RadarUtils.getGridsXY(w, range,radarBase);
		RadarData rd = radarBase.l2;
		int moment = radarBase.active_moment;
		double binInterval = rd.getBinInterval(moment);
		double rangeToFirst = rd.getRangeToFirstBin(moment);
		NavigableMap map = rd.readFile(moment);
//		GUIManager.toolBarLabel.setText(RadarBase.radarName + " 时间 " + RadarUtils.getFileTime()
//				+ " - 文件 " + l2.getSrcFileName() + " - "
//				+ RadarUtils.getMomentLabel());
		int size = map.size();
		double halfSin = Math.sin(Math
				.toRadians(RadarData.VERTICAL_BEAM_WIDTH / 2));
		double[] cos = new double[size];
		double[] tan = new double[size];
		Double[] keys = (Double[]) map.keySet().toArray(new Double[] {});
		for (int i = 0; i < size; i++) {
			double e = Math.toRadians(keys[i].doubleValue());
			cos[i] = Math.cos(e);
			tan[i] = Math.tan(e);
		}
		Object[] values = map.values().toArray();
		size--;
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids[i].length; j++) {
				if (grids[i][j] != null) {
					ARCoord c = PositionUtils.toARCoord(grids[i][j].x,
							grids[i][j].y);
					double v = 0;
					double value1 = 0;
					double h1 = 0;
					for (int k = 0; k < size; k++) {
						if (k == 0) {
							double range = PositionUtils.toRange2(c.r, cos[k]);
							float f1 = SA_SB
									.binaryToMoment(rd.getPointValue4I(
											values[k], c.azimuth, range,
											binInterval, rangeToFirst), moment,
											RadarBase.resolution);
							if (RadarData.isValid(f1)) {
								value1 = Math.pow(10, f1 / 10.0);
								v += Math.pow(value1, 4.0 / 7.0) * range
										* halfSin;
							}
							h1 = c.r * tan[k];
						}
						double value2 = 0;
						double range = PositionUtils.toRange2(c.r, cos[k + 1]);
						float f2 = SA_SB.binaryToMoment(rd.getPointValue4I(
								values[k + 1], c.azimuth, range, binInterval,
								rangeToFirst), moment, RadarBase.resolution);
						if (RadarData.isValid(f2)) {
							value2 = Math.pow(10, f2 / 10.0);
							if (k == size - 1) {
								v += Math.pow(value2, 4.0 / 7.0) * range
										* halfSin;
							}
						}
						double h2 = c.r * tan[k + 1];
						v += Math.pow((value1 + value2) / 2.0, 4.0 / 7.0)
								* (h2 - h1);
						value1 = value2;
						h1 = h2;
					}
					grids[i][j].setDoubleValue(0.00344 * v);
					grids[i][j].setARCoord(c);
				}
			}
		}
		radarBase.datas = grids;

		// 画出格点
		RadarColor radarColor = RadarUtils.getRadarColor(radarBase.currentMoment);
		Color[] colors = radarColor.getColors();
		float[] cvalues = radarColor.getColorValues();
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids[i].length; j++) {
				if (grids[i][j] != null) {
					double v = grids[i][j].getDoubleValue();
					for (int k = 0; k < cvalues.length; k++) {
						if (k != cvalues.length - 1) {
							if (v >= cvalues[k] && v < cvalues[k + 1]) {
								g.setColor(colors[k]);
								g.fillRect(grids[i][j].x - hw, grids[i][j].y
										- hw, w, w);
							}
						} else {
							if (v >= cvalues[k]) {
								g.setColor(colors[k]);
								g.fillRect(grids[i][j].x - hw, grids[i][j].y
										- hw, w, w);
							}
						}
					}
				}
			}
		}
	}

	public static double getPointValue(int x, int y,RadarBase radarBase) {
		if (radarBase.datas != null) {
			GridValue[][] grids = (GridValue[][]) radarBase.datas;
			int w = PositionUtils.toLength(gridWidth,radarBase);
			int i = (int) Math
					.floor((x + radarBase.xoffset - radarBase.center_X)
							/ (double) w)
					+ grids.length / 2;
			int j = (int) Math
					.floor((y + radarBase.yoffset - radarBase.center_Y)
							/ (double) w)
					+ grids.length / 2;
			if (i >= 0 && j >= 0 && i < grids.length && j < grids[i].length
					&& grids[i][j] != null) {
				return grids[i][j].getDoubleValue();
			}
		}
		return RadarData.NO_DATA;
	}

	public static void setResolution(byte resolution) {
		VIL.resolution = resolution;
		ResolutionOption[] options = RadarUtils
				.getResolution(CommonProps.MOMENT_VIL);
		boolean flag = true;
		for (int i = 0; i < options.length; i++) {
			if (options[i].getValue() == resolution) {
				gridWidth = options[i].getResolution();
				range = options[i].getRange();
				flag = false;
				break;
			}
		}
		if (flag) {
			gridWidth = options[0].getResolution();
			range = options[0].getRange();
		}
	}

	public static byte getResolution() {
		return resolution;
	}

}
