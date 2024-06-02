package com.kitty.radar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.gui.DrawlinePanel;
import com.kitty.radar.gui.ExportSetDialog;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.listener.RhiWindowHandler;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class RHI extends JPanel {

	public static final byte LEFT_WIDTH = 40; // 左边距，单位：像素

	public static final byte TOP_WIDTH = 22; // 上边距、下边距，单位：像素

	public static RHI rhi;

	public static double azimuth = 0;
	public int postion = 0; // o，表示鼠标滑动获取方位绘制HRI，1表示定点获取方位绘制HRI

	public static Rectangle bounds = new Rectangle(0, 0, 480, 224);

	private int height;

	private int width;

	private byte height_min = 0;

	private byte height_max = 20;

	public float range_min = 0;

	public  float range_max = 0;

	private boolean update = true;

	private BufferedImage image;
	private RadarBase radarBase;
	public static List<RHI> rhis = new LinkedList<RHI>();
	public static List<JDialog> rhiDialogs = new LinkedList<JDialog>();

	public RHI(RadarBase radarBase) {
		this.radarBase = radarBase;
		range_max = radarBase.radius;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = this.getWidth();
		int h = this.getHeight();
		if (w != width || h != height) {
			width = w;
			height = h;
			image = null;
		}
		if (image == null) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			update = true;
		}
		if (update) {
			Graphics2D g2 = image.createGraphics();
			  //消除文字锯齿
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			  //消除画图锯齿
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setPaint(Color.BLACK);
			g2.fillRect(0, 0, width, height);

			displayRHI(g2);
			displayGrid(g2);
			g2.dispose();
			update = false;
		}
		g.drawImage(image, 0, 0, width, height, null);
	}

	private void displayRHI(Graphics2D g) {
		RadarData l2 = radarBase.l2;
		if (l2 == null || l2.getCutNumber() <= 0) {
			return;
		}
		Color[] colorCache = RadarUtils.getRadarColor(radarBase.currentMoment, radarBase).getColorCache();
		if (colorCache == null) {
			return;
		}

		int[] x = new int[4];
		int[] y = new int[4];
		double halfWidth = RadarData.VERTICAL_BEAM_WIDTH / 2.0;
		double scaleX = (width - LEFT_WIDTH-10) / (double) (range_max - range_min);
		double scaleY = (height - TOP_WIDTH * 2)
				/ (double) (height_max - height_min);
		int baseY = height - TOP_WIDTH;

		for (int i = 0; i < l2.getCutNumber(); i++) {
			int recordNum = l2.getCutStart(i);
			l2.readHeader(recordNum);
			if (l2.getBinCount(radarBase.active_moment) <= 0) {
				continue;
			}
			int endRecordNum = l2.getCutStart(i + 1);
			if (endRecordNum == -1) {
				endRecordNum = RadarData.MAX_FILE_RECORDS;
			}
			int k = -1;
			double interval = Double.MAX_VALUE;
			for (int j = recordNum; j < endRecordNum; j++) {
				double diff = Math.abs(l2.getAzimuth(j) - azimuth);
				if (diff < interval) {
					interval = diff;
					k = j;
				}
			}
			if (k != -1) {
				l2.readRecord(k);
				double rangeToFirst = l2
						.getRangeToFirstBin(radarBase.active_moment);
				double rangeStep = l2.getBinInterval(radarBase.active_moment);
				short binCount = l2.getBinCount(radarBase.active_moment);
				double ang1 = (l2.getElevation() - halfWidth) * Math.PI / 180.0;
				double ang2 = (l2.getElevation() + halfWidth) * Math.PI / 180.0;
				double sin1 = Math.sin(ang1);
				double sin2 = Math.sin(ang2);
				double cos1 = Math.cos(ang1);
				double cos2 = Math.cos(ang2);

				for (int bin = 0; bin < binCount; bin++) {
					Color c = colorCache[l2.getBinaryValue(
							radarBase.active_moment, 0, bin)];
					if (c != null) {
						g.setPaint(c);
						double range = rangeToFirst + (bin + 1) * rangeStep;
						double range1 = range - rangeStep / 2.0;
						double range2 = range + rangeStep / 2.0;

						x[0] = (int) Math.round((range1 * cos1 - range_min)
								* scaleX)
								+ LEFT_WIDTH;
						x[1] = (int) Math.round((range2 * cos1 - range_min)
								* scaleX)
								+ LEFT_WIDTH;
						x[2] = (int) Math.round((range2 * cos2 - range_min)
								* scaleX)
								+ LEFT_WIDTH;
						x[3] = (int) Math.round((range1 * cos2 - range_min)
								* scaleX)
								+ LEFT_WIDTH;

						y[0] = baseY
								- (int) Math.round((PositionUtils.getHeight(
										range1, sin1, cos1, radarBase) - height_min)
										* scaleY);
						y[1] = baseY
								- (int) Math.round((PositionUtils.getHeight(
										range2, sin1, cos1, radarBase) - height_min)
										* scaleY);
						y[2] = baseY
								- (int) Math.round((PositionUtils.getHeight(
										range2, sin2, cos2, radarBase) - height_min)
										* scaleY);
						y[3] = baseY
								- (int) Math.round((PositionUtils.getHeight(
										range1, sin2, cos2, radarBase) - height_min)
										* scaleY);

						g.fillPolygon(x, y, 4);
					}
				}
			}
		}
	}

	private void displayGrid(Graphics2D g) {

		// 清除Grid外的图像
		g.setPaint(Color.BLACK);
		g.fillRect(0, 0, LEFT_WIDTH, height);
		g.fillRect(0, 0, width, TOP_WIDTH);
		g.fillRect(0, height - TOP_WIDTH, width, TOP_WIDTH);
		g.setFont(new Font("宋体",Font.PLAIN,15));
		g.setPaint(Color.WHITE);
		g.drawString("方位角: " + CommonUtils.format(azimuth, 1) + "°",
				width / 2 - 40, 15);

		double intervalX = (range_max - range_min) / 10.0;
		double intervalY = (height_max - height_min) / 5.0;
		double scaleX = (width - LEFT_WIDTH-10) / (double) (range_max - range_min);
		double scaleY = (height - TOP_WIDTH * 2)
				/ (double) (height_max - height_min);
		for (int i = 0; i < 11; i++) {
			int pixel = (int) Math.round(i * intervalX * scaleX) + LEFT_WIDTH;//具体网格点的横坐标值
			g.drawLine(pixel, TOP_WIDTH, pixel, (height - TOP_WIDTH));
			if (i != 10) {
				String value = CommonUtils.format(range_min + i * intervalX, 1);
				g.drawString(value + (i == 9 ? " km" : ""), pixel - 8,
						height - 5);
			}
		}
		for (int i = 0; i < 6; i++) {
			int scanl = height - TOP_WIDTH
					- (int) Math.round(i * intervalY * scaleY);
			g.drawLine(LEFT_WIDTH, scanl, width-10, scanl);
			String value = CommonUtils.format(height_min + i * intervalY, 1);
			g.drawString(value, (value.length() < 4 ? 12 : 5), scanl + 5);
			g.drawString( (i == 5 ? " km" : ""), (value.length() < 4 ? 12 : 5), scanl - 8);
		}
	}

	public static void createRhiDialog(JFrame owner) {

		if (VCS.vcsDialogs.size() > 0) {
			VCS.vcsDialogs.forEach(p -> {
				p.removeAll();
				p.dispose();
			});
		}
		rhis.clear();

		if (RHI.rhiDialogs.size() > 0) {
			RHI.rhiDialogs.forEach(p -> {
				p.removeAll();
				p.dispose();
			});
		}
		rhiDialogs.clear();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (!GUIManager.syncTool) {
			RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
			// rhi=rhis.get(i);
			//	if (rhis.isEmpty()) {
			JDialog rhiDialog = new JDialog(owner, "RHI距离高度显示");
			rhiDialog.addWindowListener(new RhiWindowHandler(rhiDialog));
			if (bounds != null) {
				rhiDialog.setBounds(bounds);
				rhiDialog.setLocation(0, (screenSize.height) / 4 );
			} else {
				rhiDialog.setSize(480, 220);
				rhiDialog.setLocation(0, (screenSize.height) / 4 );
			}

			CommonUtils.addEscAction((JComponent) rhiDialog.getContentPane(),
					new AbstractAction() {
						public void actionPerformed(ActionEvent e) {
							rhiDialog.dispose();
							//	rhi=null;
						}
					});
			rhi = new RHI(radarBase);
			rhi.setDoubleBuffered(false);
			rhiDialog.add(rhi);
			rhiDialog.setVisible(true);
			rhis.add(rhi);
			rhiDialogs.add(rhiDialog);
		} else {
			for (int i = 0; i < GUIManager.jlayers.size(); i++) {
				RadarBase radarBase = GUIManager.jlayers.get(i).getView().getRadarBase();
				// rhi=rhis.get(i);
				//	if (rhis.isEmpty()) {
				JDialog rhiDialog = new JDialog(owner, "RHI距离高度显示");
				rhiDialog.addWindowListener(new RhiWindowHandler(rhiDialog));
				if (bounds != null) {
					rhiDialog.setBounds(bounds);
					rhiDialog.setLocation(0, (screenSize.height) / 4 + 220 * i);
				} else {
					rhiDialog.setSize(480, 220);
					rhiDialog.setLocation(0, (screenSize.height) / 4 + 220 * i);
				}

				CommonUtils.addEscAction((JComponent) rhiDialog.getContentPane(),
						new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								rhiDialog.dispose();
								//	rhi=null;
							}
						});
				rhi = new RHI(radarBase);
				rhi.setDoubleBuffered(false);
				rhiDialog.add(rhi);
				rhiDialog.setVisible(true);
				rhis.add(rhi);
				rhiDialogs.add(rhiDialog);
			}
		}
	}

	public static void update() {
		for (int i = 0; i < GUIManager.jlayers.size(); i++) {
			if(i<rhis.size()) {
				rhi = rhis.get(i);
				if (rhi != null) {
					rhi.update = true;
					rhi.repaint();
				}
			}

		}
	}
	public RadarBase getRadarBase() {
		return radarBase;
	}

	public void exportRHI(Writer w) throws IOException {
		if (ExportSetDialog.coordType == 0) {
			RadarUtils.writeValues(w, new String[] { "R(km)", "H(km)",
					"V(" + RadarUtils.getMomentUnitLabel(radarBase.currentMoment) + ")" });
		} else {
			RadarUtils.writeValues(w, new String[] { "LNG(°)", "LAT(°)",
					"H(km)", "V(" + RadarUtils.getMomentUnitLabel(radarBase.currentMoment) + ")" });
		}
		RadarData l2 = radarBase.l2;
		if (l2 == null || l2.getCutNumber() <= 0) {
			return;
		}

		for (int i = 0; i < l2.getCutNumber(); i++) {
			int recordNum = l2.getCutStart(i);
			l2.readHeader(recordNum);
			if (l2.getBinCount(radarBase.active_moment) <= 0) {
				continue;
			}
			int endRecordNum = l2.getCutStart(i + 1);
			if (endRecordNum == -1) {
				endRecordNum = RadarData.MAX_FILE_RECORDS;
			}
			int k = -1;
			double interval = Double.MAX_VALUE;
			for (int j = recordNum; j < endRecordNum; j++) {
				double diff = Math.abs(l2.getAzimuth(j) - azimuth);
				if (diff < interval) {
					interval = diff;
					k = j;
				}
			}
			if (k != -1) {
				l2.readRecord(k);
				double rangeToFirst = l2
						.getRangeToFirstBin(radarBase.active_moment);
				double rangeStep = l2.getBinInterval(radarBase.active_moment);
				short binCount = l2.getBinCount(radarBase.active_moment);
				double ang = (l2.getElevation()) * Math.PI / 180.0;
				double sin = Math.sin(ang);
				double cos = Math.cos(ang);

				for (int bin = 0; bin < binCount; bin++) {
					float value = RadarUtils.getMomentValue(l2.getMomentValue(
							radarBase.active_moment, 0, bin),radarBase.active_moment);
					String svalue;
					if (RadarData.isValid(value)) {
						svalue = CommonUtils.format(value,
								RadarUtils.getMomentScale(radarBase.currentMoment));
					} else {
						svalue = "N/A";
					}
					double range = rangeToFirst + (bin + 1) * rangeStep;
					double x = range * cos;
					double h = PositionUtils.getHeight(range, sin, cos, radarBase);
					if (ExportSetDialog.coordType == 0) {
						RadarUtils.writeValues(w,
								new String[] { CommonUtils.format(x, 1),
										CommonUtils.format(h, 1), svalue });
					} else {
						LLCoord c = PositionUtils.toLLCoord(azimuth, x, radarBase.getLongitude(), radarBase.getLatitude());
						RadarUtils.writeValues(
								w,
								new String[] {
										CommonUtils.format(c.longitude, 6),
										CommonUtils.format(c.latitude, 6),
										CommonUtils.format(h, 1), svalue });
					}
				}
			}
		}
	}
}
