package com.kitty.radar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.domain.XYDCoord;
import com.kitty.radar.gui.ExportSetDialog;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class PPI extends RadarBase {

	/**
	 * 显示PPI，可支持反射率、速度、谱宽、液水含量。
	 * 
	 * @param g
	 */
	public static void displayPPI(Graphics2D g, RadarBase radarBase) {
		RadarData l2 = radarBase.l2;
		if (l2 == null) { // 没有选中的文件
			return;
		}
		Date queueDate = new Date();
		SimpleDateFormat queueDateFormat= new SimpleDateFormat("HH:mm:ss:SSSS");
		String now2 = queueDateFormat.format(queueDate);

		Color color, oldColor;
		int[] x = new int[4];
		int[] y = new int[4];
		int pixel = radarBase.center_X - radarBase.xoffset;
		int scanl = radarBase.center_Y - radarBase.yoffset;
		double dd2=Math.sqrt(radarBase.xoffset*radarBase.xoffset+radarBase.yoffset*radarBase.yoffset);//与雷达点的距离（像素）
		double dd3=Math.sqrt(radarBase.center_X*radarBase.center_X+radarBase.center_Y*radarBase.center_Y);		
	    int d=(int)((dd2+dd3)/radarBase.scale_X);
		double halfWidth = l2.beamWidth / 2.0;
		Color[] colorCache = RadarUtils.getRadarColor(radarBase.currentMoment).getColorCache();
		int recordNum = l2.getCutStart(radarBase.cutNum);
		int number = l2.readCut(recordNum);
//		GUIManager.toolBarLabel
//				.setText("时间 " + RadarUtils.getFileTime() + " - 仰角 " + CommonUtils.format(l2.getElevation(cutNum), 6)
//						+ "° - 文件 " + l2.getSrcFileName() + " - " + RadarUtils.getMomentLabel(radarBase));
		int active_moment = radarBase.active_moment;
		int bins = l2.getBinCount(active_moment);
		double rangeStep = l2.getBinInterval(active_moment);
		int bins2=(int)(d/rangeStep);//计算显示范围内最大径向长度（中心到矩形定点的距离）对应库数
		if(bins2<bins)
		{
			bins=bins2;
		}
		//JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+bins);
		int dt=1;
		if(radarBase.zoom==4)
		{
			dt=1;
		}
		if(radarBase.zoom==2)
		{
			dt=2;
		}
		if(radarBase.zoom==1)
		{
			dt=3;
		}
		double halfStep = rangeStep / 2.0;
		double[] range = new double[bins + 1]; // 加1用于绘制最后一个bin的图像
		range[0] = l2.getRangeToFirstBin(active_moment) + halfStep; // rangeToFirstBin通常为0
		for (int i = 1; i < range.length; i++) {
			range[i] = range[i - 1] + rangeStep;
		}
		for (int i = 0; i < number; i++) {
			double azimuth = l2.getAzimuth(recordNum + i);
			if (azimuth < 0) {
				continue;
			}
			double ang1 = azimuth - halfWidth;
			double ang2 = azimuth + halfWidth + 0.5; // 加0.5防止产生beam间隙，多画的部分会被后面的beam覆盖
			oldColor = null;
			for (int j = 0; j < bins; j=j+dt) {
				color = colorCache[l2.getBinaryValue(active_moment, i, j)];
				//JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+color);
				if (oldColor == null) {
					if (color != null) {
						x[0] = (int) Math.round(range[j] * RadarUtils.cos(ang1)
								* radarBase.scale_X)
								+ pixel;
						y[0] = (int) Math.round(range[j] * RadarUtils.sin(ang1)
								* radarBase.scale_Y)
								+ scanl;
						x[1] = (int) Math.round(range[j] * RadarUtils.cos(ang2)
								* radarBase.scale_X)
								+ pixel;
						y[1] = (int) Math.round(range[j] * RadarUtils.sin(ang2)
								* radarBase.scale_Y)
								+ scanl;
					}
				} else {
					if (color != oldColor) {
						x[2] = (int) Math.round(range[j] * RadarUtils.cos(ang2)
								* radarBase.scale_X)
								+ pixel;
						y[2] = (int) Math.round(range[j] * RadarUtils.sin(ang2)
								* radarBase.scale_Y)
								+ scanl;
						x[3] = (int) Math.round(range[j] * RadarUtils.cos(ang1)
								* radarBase.scale_X)
								+ pixel;
						y[3] = (int) Math.round(range[j] * RadarUtils.sin(ang1)
								* radarBase.scale_Y)
								+ scanl;
						g.setPaint(oldColor);
						g.fillPolygon(x, y, 4);
						x[0] = x[3];
						y[0] = y[3];
						x[1] = x[2];
						y[1] = y[2];
					}
				}
				oldColor = color;
			}
			if (oldColor != null) {
				x[2] = (int) Math.round(range[bins] * RadarUtils.cos(ang2)
						* radarBase.scale_X)
						+ pixel;
				y[2] = (int) Math.round(range[bins] * RadarUtils.sin(ang2)
						* radarBase.scale_Y)
						+ scanl;
				x[3] = (int) Math.round(range[bins] * RadarUtils.cos(ang1)
						* radarBase.scale_X)
						+ pixel;
				y[3] = (int) Math.round(range[bins] * RadarUtils.sin(ang1)
						* radarBase.scale_Y)
						+ scanl;
				g.setPaint(oldColor);
				g.fillPolygon(x, y, 4);
			}
		}
		Date queueDate1 = new Date();
		String now3 = queueDateFormat.format(queueDate1);
		//JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+now2+"--"+now3+": "+(queueDate1.getTime()-queueDate.getTime()));
	}

	/**
	 * 导出PPI数据。
	 * 
	 * @param w
	 * @param inArea
	 * @throws IOException
	 */
	public static void exportPPI(Writer w, boolean inArea, RadarBase radarBase) throws IOException {
		int moment = radarBase.currentMoment;
		if (ExportSetDialog.coordType == 0) {
			RadarUtils.writeValues(w, new String[] { "A(°)", "R(km)",
					"V(" + RadarUtils.getMomentUnitLabel(moment) + ")" });
		} else {
			RadarUtils.writeValues(w, new String[] { "LNG(°)", "LAT(°)",
					"V(" + RadarUtils.getMomentUnitLabel(moment) + ")" });
		}
		RadarData l2 = radarBase.l2;
		if (l2 == null) { // 没有选中的文件
			return;
		}

		int recordNum = l2.getCutStart(radarBase.cutNum);
		int number = l2.readCut(recordNum);
		double cos = Math.cos(l2.getElevation(radarBase.cutNum) * Math.PI / 180.0);
		int bins = l2.getBinCount(radarBase.active_moment);
		double rangeStep = l2.getBinInterval(radarBase.active_moment);
		double[] range = new double[bins];
		range[0] = l2.getRangeToFirstBin(radarBase.active_moment) + rangeStep; // rangeToFirstBin通常为0
		for (int i = 1; i < range.length; i++) {
			range[i] = range[i - 1] + rangeStep;
		}
		List shapeList = null;
		if (inArea) {
			shapeList = AreaDialog.tableModel.getShapeList();
		}
		for (int i = 0; i < number; i++) {
			double azimuth = l2.getAzimuth(recordNum + i);
			if (azimuth < 0) {
				continue;
			}
			for (int j = 0; j < bins; j++) {
				if (inArea
						&& !AreaDialog.tableModel.contains(azimuth, range[j],
								shapeList, radarBase)) {
					continue;
				}
				float value = RadarUtils.getMomentValue(l2.getMomentValue(
						radarBase.active_moment, i, j), radarBase.currentMoment);
				String svalue;
				if (RadarData.isValid(value)) {
					svalue = CommonUtils.format(value,
							RadarUtils.getMomentScale(radarBase.currentMoment));
				} else {
					svalue = "N/A";
				}
				if (ExportSetDialog.coordType == 0) {
					RadarUtils.writeValues(w,
							new String[] { CommonUtils.format(azimuth, 1),
									CommonUtils.format(range[j], 1), svalue });
				} else {
					LLCoord c = PositionUtils.toLLCoord(azimuth,
							PositionUtils.toR2(range[j], cos));
					RadarUtils
							.writeValues(
									w,
									new String[] {
											CommonUtils.format(c.longitude, 6),
											CommonUtils.format(c.latitude, 6),
											svalue });
				}
			}
		}
	}

}
