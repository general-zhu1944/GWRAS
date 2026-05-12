package com.kitty.radar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.awt.*;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.XYDCoord;
import com.kitty.radar.gui.*;
import com.kitty.radar.listener.VcsWindowHandler;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.DataType;
import org.meteoinfo.ndarray.math.ArrayUtil;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.listener.RhiWindowHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class VCS extends JPanel {

	public static final byte LEFT_WIDTH = 40; // 左边距，单位：像素

	public static final byte TOP_WIDTH = 22; // 上边距、下边距，单位：像素

	public static VCS vcs;

	public static double azimuth = 0;

	public static Rectangle bounds = new Rectangle(0, 0, 480, 224);

	private int height;

	private int width;
	public static int changepanel=0;

	private byte height_min = 0;//剖面回波起始高度

	private byte height_max = 16;//剖面回波顶部高度

	public static float range_min = 0;

	public static float range_max = 0;

	public boolean update = true;

	private BufferedImage image;

	public static List<VCS> vcss = new LinkedList<VCS>();
	public static List<JDialog> vcsDialogs = new LinkedList<JDialog>();
	private RadarBase radarBase;
	public Point pointStart = new Point(0,0);//剖面起始点
	public Point pointEnd   = new Point(60,0);//剖面起终点

	public VCS(RadarBase radarBase) {
		this.radarBase = radarBase;
		range_max = radarBase.radius;
	}
	public RadarBase getRadarBase() {
		return radarBase;
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
			Graphics2D g2 = (Graphics2D) image.createGraphics();
			g2.setPaint(Color.BLACK);
			g2.fillRect(0, 0, width, height);			
			displayCSV(g2);
			displayGrid(g2);
			g2.dispose();
			update = false;
		}
		g.drawImage(image, 0, 0, width, height, null);

	}
	 

	private void displayCSV(Graphics2D g) {
		RadarData l2 = radarBase.l2;
		if (l2 == null || l2.getCutNumber() <= 0) {
			return;
		}

		Color[] colorCache = RadarUtils.getRadarColor(radarBase.currentMoment, radarBase).getColorCache();//根据RadarBase.currentMoment获取相应的颜色数组
		if (colorCache == null) {
			return;
		}
		int[] x = new int[4];
		int[] y = new int[4];
//		pointStart=new Point(0,0);
//		pointEnd=new Point(60,0);
		if (changepanel==1) {

			for (int i = 0; i < GUIManager.jlayers.size(); i++) {
				if (radarBase == GUIManager.jlayers.get(i).getView().getRadarBase()) {
					JMainPanelLayerUi uii = (JMainPanelLayerUi) GUIManager.jlayers.get(i).getUI();
					DrawlinePanel dp = uii.getDrawlinePanel();
					if (null != dp.getLinePosition()) {
						if (dp.getLinePosition().getLongitudeStart() != 0.0f) {
							double longitudeStart = dp.getLinePosition().getLongitudeStart();
							double latitudeStart = dp.getLinePosition().getLatitudeStart();
							double longitudeEnd = dp.getLinePosition().getLongitudeEnd();
							double latitudeEnd = dp.getLinePosition().getLatitudeEnd();
							ARCoord arcStart = PositionUtils.toARCoord(longitudeStart, latitudeStart, radarBase.getLongitude(), radarBase.getLatitude());
							XYCoord xyStart = PositionUtils.toXYCoord(arcStart.azimuth, arcStart.r, radarBase);
							XYDCoord startpoint = PositionUtils.toXYDCoord(xyStart.x, xyStart.y, radarBase);//左上坐标转雷达中心坐标
							ARCoord arcEnd = PositionUtils.toARCoord(longitudeEnd, latitudeEnd, radarBase.getLongitude(), radarBase.getLatitude());
							XYCoord xyEnd = PositionUtils.toXYCoord(arcEnd.azimuth, arcEnd.r, radarBase);
							XYDCoord endpoint = PositionUtils.toXYDCoord(xyEnd.x, xyEnd.y, radarBase);//左上坐标转雷达中心坐标
							pointStart.x = (int) startpoint.x;
							pointStart.y = (int) startpoint.y;
							pointEnd.x = (int) endpoint.x;
							pointEnd.y = (int) endpoint.y;

						}
					}
				}
			}
		}
		double binRes = l2.getBinInterval(radarBase.active_moment);
        float startEndDistance = (float) Math.sqrt(Math.pow(pointEnd.x - pointStart.x, 2) + Math.pow(pointEnd.y - pointStart.y, 2));//计算两点距离	
        range_max=startEndDistance;
        int nPoints = (int) (startEndDistance / binRes + 1);//根据距离库长计算分割点数
        Array xa = ArrayUtil.lineSpace(pointStart.x, pointEnd.x, nPoints, true);//生成分割点的X轴坐标数组
        Array ya = ArrayUtil.lineSpace(pointStart.y, pointEnd.y, nPoints, true);//生成分割点的y轴坐标数组        
        Array azimuth_ar =PositionUtils.xyToAzimuth(xa, ya);//计算分割点的方位        
       // System.out.println("任务2计划时间："+azimuth_ar);       		
		double halfWidth = RadarData.VERTICAL_BEAM_WIDTH / 2.0;
		double scaleX = (width - LEFT_WIDTH-10) / (double) (startEndDistance);//计算像素距离与实际水平距离的比率
		double scaleY = (height - TOP_WIDTH * 2)
				/ (double) (height_max - height_min);
		int baseY = height - TOP_WIDTH;		
		for (int i = 0; i < l2.getCutNumber(); i++) {
			 for (int bin = 0; bin < nPoints; bin++) {
				int recordNum = l2.getCutStart(i);
				l2.readHeader(recordNum);//读取径向头信息
				if (l2.getBinCount(radarBase.active_moment) <= 0) {
					continue;
				}
				int endRecordNum = l2.getCutStart(i + 1);//获取上一层仰角开始径向的序列值
				if (endRecordNum == -1) {
					endRecordNum = RadarData.MAX_FILE_RECORDS;
				}
				int k = -1;
				double interval = Double.MAX_VALUE;
				//搜索接近分割点的那跟径向
				for (int j = recordNum; j < endRecordNum; j++) {
					double diff = Math.abs(l2.getAzimuth(j) - azimuth_ar.getFloat(bin));
					if (diff < interval) {
						interval = diff;
						k = j;
					}
				}			
				if (k != -1) {
					l2.readRecord(k);
					double rangeToFirst = l2
							.getRangeToFirstBin(radarBase.active_moment);
					double rangeStep = binRes;
					short binCount = l2.getBinCount(radarBase.active_moment);
					double ang = (l2.getElevation()) * Math.PI / 180.0;
					double ang1 = (l2.getElevation() - halfWidth) * Math.PI / 180.0;
					double ang2 = (l2.getElevation() + halfWidth) * Math.PI / 180.0;
					double sin1 = Math.sin(ang1);
					double sin2 = Math.sin(ang2);
					double cos1 = Math.cos(ang1);
					double cos2 = Math.cos(ang2);
					double xx = xa.getFloat(bin);
					double yy = ya.getFloat(bin);
					double R = CommonProps.RE * 1000.0 * 4.0 / 3.0;  // effective radius of earth in meters.
					double	s = Math.sqrt(xx * xx + yy * yy);
					//double dis = (float) Math.tan(s/R)*(R+RadarBase.antennaHeight)/Math.cos(ang);//波束中心的径向距离,该计算公式为准确的，但效率低下，计算误差不大，所以采用估算
					double dis = s/Math.cos(ang);//波束中心的径向距离估算
					double dis1 = s/Math.cos(ang1);//波束底的径向距离
					double dis2 =s/Math.cos(ang2);//波束顶的径向距离
					int codebin=(int)(dis/rangeStep);					
						Color c = colorCache[l2.getBinaryValue(
								radarBase.active_moment, 0, codebin)];
						if (c != null) {
							g.setPaint(c);
							//double range = rangeToFirst + dis;
							double range1 = rangeToFirst + dis1 ;
							double range2 = rangeToFirst + dis2 ;
                             //0,1,2,3 分别代表上前，上后，下后，下前，接近雷达为前
							x[0] = (int) Math.round((binRes*bin-binRes/2 )
									* scaleX)
									+ LEFT_WIDTH;
							x[1] = (int) Math.round((binRes*bin+binRes/2)
									* scaleX)
									+ LEFT_WIDTH;
							x[2] = (int) Math.round((binRes*bin+binRes/2 )
									* scaleX)
									+ LEFT_WIDTH;
							x[3] = (int) Math.round((binRes*bin-binRes/2 )
									* scaleX)
									+ LEFT_WIDTH;

							y[0] =baseY- (int) Math.round((PositionUtils.getHeight(
											range2, l2.getElevation() +halfWidth, radarBase) )
											* scaleY);
							y[1] = baseY-(int) Math.round((PositionUtils.getHeight(
											range2, l2.getElevation() + halfWidth, radarBase))
											* scaleY);
							y[2] =baseY-  (int) Math.round((PositionUtils.getHeight(
											range1, l2.getElevation() - halfWidth, radarBase) )
											* scaleY);
							y[3] = baseY- (int) Math.round((PositionUtils.getHeight(
											range1, l2.getElevation() - halfWidth, radarBase) )
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
		//g.drawString("方位角: " + CommonUtils.format(azimuth, 1) + "°",
			//	width / 2 - 40, 15);
		double intervalX = (range_max - range_min) / 10.0;
		if(range_max<=50)
		{
		 intervalX = (range_max - range_min) / 5.0;
		}

		double intervalY = (height_max - height_min) / 4.0;
		double scaleX = (width - LEFT_WIDTH-10) / (double) (range_max - range_min);
		double scaleY = (height - TOP_WIDTH * 2)
				/ (double) (height_max - height_min);
		if(range_max>50)
		{
		for (int i = 0; i < 11; i++) {
			int pixel = (int) Math.round(i * intervalX * scaleX) + LEFT_WIDTH;//具体网格点的横坐标值
			g.drawLine(pixel, TOP_WIDTH, pixel, (height - TOP_WIDTH));
			if (i != 10) {
				String value = CommonUtils.format(range_min + i * intervalX, 1);
				g.drawString(value + (i == 9 ? " km" : ""), pixel - 8,
						height - 5);
			}
		}
		}
		if(range_max<=50)
		{
			for (int i = 0; i < 6; i++) {
				int pixel = (int) Math.round(i * intervalX * scaleX) + LEFT_WIDTH;//具体网格点的横坐标值
				g.drawLine(pixel, TOP_WIDTH, pixel, (height - TOP_WIDTH));
				if (i != 5) {
					String value = CommonUtils.format(range_min + i * intervalX, 1);
					g.drawString(value + (i == 4 ? " km" : ""), pixel - 8,
							height - 5);
				}
		}
		}
		for (int i = 0; i < 5; i++) {
			int scanl = height - TOP_WIDTH
					- (int) Math.round(i * intervalY * scaleY);
			g.drawLine(LEFT_WIDTH, scanl, width-10, scanl);
			String value = CommonUtils.format(height_min + i * intervalY, 1);
			g.drawString(value, (value.length() < 4 ? 12 : 5), scanl + 5);
			g.drawString( (i == 4 ? " km" : ""), (value.length() < 4 ? 12 : 5), scanl - 8);
		}
	}
	
	public static void createVcsDialog(JFrame owner) {
		if (RHI.rhiDialogs.size() > 0) {
			RHI.rhiDialogs.forEach(p -> {
				p.removeAll();
				p.dispose();
			});
		}
		vcss.clear();

		if (VCS.vcsDialogs.size() > 0) {
			VCS.vcsDialogs.forEach(p -> {
				p.removeAll();
				p.dispose();
			});
		}
		vcsDialogs.clear();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (!GUIManager.syncTool) {
			RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
			JDialog vcsDialog = new JDialog(owner, "VCS任意剖面显示");
			vcsDialog.addWindowListener(new VcsWindowHandler(vcsDialog));
			if (bounds != null) {
				vcsDialog.setBounds(bounds);
			} else {
				vcsDialog.setSize(480, 220);
			}
			CommonUtils.addEscAction((JComponent) vcsDialog.getContentPane(),
					new AbstractAction() {
						public void actionPerformed(ActionEvent e) {
							vcsDialog.dispose();
							vcs = null;
							vcsDialogs.remove(vcsDialog);
						}
					});
			vcs = new VCS(radarBase);
			vcs.setDoubleBuffered(false);
			vcsDialog.add(vcs);
			vcsDialog.setVisible(true);
			vcss.add(vcs);
			vcsDialogs.add(vcsDialog);
		}else {
			for (int i = 0; i < GUIManager.jlayers.size(); i++) {
				RadarBase radarBase = GUIManager.jlayers.get(i).getView().getRadarBase();
				JDialog vcsDialog = new JDialog(owner, "VCS任意剖面显示");
				vcsDialog.addWindowListener(new VcsWindowHandler(vcsDialog));
				if (bounds != null) {
					vcsDialog.setBounds(bounds);
					vcsDialog.setLocation(0, (screenSize.height) / 4 + 220 * i);
				} else {
					vcsDialog.setSize(480, 220);
					vcsDialog.setLocation(0, (screenSize.height) / 4 + 220 * i);
				}
				CommonUtils.addEscAction((JComponent) vcsDialog.getContentPane(),
						new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								vcsDialog.dispose();
								vcs = null;
								vcsDialogs.remove(vcsDialog);
							}
						});
				vcs = new VCS(radarBase);

				vcs.setDoubleBuffered(false);
				vcsDialog.add(vcs);
				vcsDialog.setVisible(true);
				vcss.add(vcs);
				vcsDialogs.add(vcsDialog);


			}
		}
	}


	public static void update() {
		for (int i = 0; i < GUIManager.jlayers.size(); i++) {
			if (i < vcss.size()) {
				vcs = vcss.get(i);
				if (vcs != null) {
					vcs.update = true;
					vcs.repaint();
				}
			}
		}
	}

	
	
	
}
