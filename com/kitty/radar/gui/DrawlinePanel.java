package com.kitty.radar.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kitty.radar.*;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class DrawlinePanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private Point pointStart = null;
	private Point pointEnd   = null;
	private double longitudeStart;
	private double latitudeStart;
	private double longitudeEnd;
	private double latitudeEnd;
	private int  yoffset;
	private int xoffset;
	private RadarBase radarBase;
	private  boolean wheel=false;
	private boolean mousePressed = false;
	private String startPoint_text="";


	public static Map<DrawlinePanel, LinePosition> linePostions = new HashMap<DrawlinePanel, LinePosition>();
	private static DrawlinePanel active = null;

	public class LinePosition {
		private double longitudeStart;
		private double latitudeStart;
		private double longitudeEnd;
		private double latitudeEnd;
		public double getLongitudeStart() {
			return longitudeStart;
		}
		public void setLongitudeStart(double longitudeStart) {
			this.longitudeStart = longitudeStart;
		}
		public double getLatitudeStart() {
			return latitudeStart;
		}
		public void setLatitudeStart(double latitudeStart) {
			this.latitudeStart = latitudeStart;
		}
		public double getLongitudeEnd() {
			return longitudeEnd;
		}
		public void setLongitudeEnd(double longitudeEnd) {
			this.longitudeEnd = longitudeEnd;
		}
		public double getLatitudeEnd() {
			return latitudeEnd;
		}
		public void setLatitudeEnd(double latitudeEnd) {
			this.latitudeEnd = latitudeEnd;
		}
	}
	public Point getstartPoint()
	{
		return pointStart;
	}
	public LinePosition getLinePosition() {
		if(GUIManager.syncTool) {
			return linePostions.get(active);
		} else {
			return linePostions.get(this);

		}
	}
	public DrawlinePanel(RadarBase radarBase) {
//        this.setOpaque(false);
//        this.addMouseListener(this);
//        this.addMouseMotionListener(this);
		this.radarBase = radarBase;
		this.linePostions.put(this, new LinePosition());
	}

	public void paint(Graphics g) {
//        super.paint(g);
		//        if (pointStart != null && pointEnd != null) {
		if(null != this.linePostions.get(this)) {
			double longitudeStart = this.getLinePosition().getLongitudeStart();
			double latitudeStart = this.getLinePosition().getLatitudeStart();
			double longitudeEnd = this.getLinePosition().getLongitudeEnd();
			double latitudeEnd = this.getLinePosition().getLatitudeEnd();
			ARCoord arcStart = PositionUtils.toARCoord(longitudeStart,latitudeStart);
			XYCoord xyStart=PositionUtils.toXYCoord(arcStart.azimuth,arcStart.r, radarBase);
			ARCoord arcEnd = PositionUtils.toARCoord(longitudeEnd,latitudeEnd);
			XYCoord xyEnd=PositionUtils.toXYCoord(arcEnd.azimuth,arcEnd.r, radarBase);
			Graphics2D g2 = (Graphics2D)g; //g是Graphics对象
			g2.setStroke(new BasicStroke(3.0f));
			g2.setColor(Color.red);
			g2.drawLine(xyStart.x, xyStart.y, xyEnd.x, xyEnd.y);
			g2.dispose();
			g.dispose();

		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		active = this;
		if (e.getClickCount() == 1) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				linePostions.put(this, new LinePosition());
			}
		}
		if(e.getButton() == MouseEvent.BUTTON1) {

			mousePressed = true;
			pointStart = e.getPoint();
			ARCoord arcStart = PositionUtils.toARCoord(pointStart.x, pointStart.y,radarBase);
			LLCoord llcStart = PositionUtils.toLLCoord(arcStart.azimuth, PositionUtils.toR(arcStart.r, radarBase.l2
					.getElevation(radarBase.cutNum)));
			LinePosition p = linePostions.get(this);
			p.longitudeStart=llcStart.longitude;
			p.latitudeStart=llcStart.latitude;
			p.longitudeEnd=llcStart.longitude;
			p.latitudeEnd=llcStart.latitude;
			pointEnd = null;
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		mousePressed = false;
		Point pointEnd2 = e.getPoint();
		if (GUIManager.syncTool) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				for (VCS vcs : VCS.vcss) {
					if (vcs.getRadarBase().equals(panel.getRadarBase())) {
						int pixel = radarBase.center_X - radarBase.xoffset;//pixel为雷达所在x坐标的位置，radarBase.center_X为图中心位置
						int scanl = radarBase.center_Y - radarBase.yoffset;
						vcs.pointStart.x = (int) ((pointStart.x - pixel) * (1 / vcs.getRadarBase().scale_X));
						vcs.pointStart.y = (int) ((pointStart.y - scanl) * (1 / vcs.getRadarBase().scale_Y));
						vcs.pointEnd.x = (int) ((pointEnd2.x - pixel) * (1 / vcs.getRadarBase().scale_X));
						vcs.pointEnd.y = (int) ((pointEnd2.y - scanl) * (1 / vcs.getRadarBase().scale_Y));
						vcs.update = true;
						vcs.repaint();

					}
				}
			}
		} else {
			for (VCS vcs : VCS.vcss) {
				if (vcs.getRadarBase().equals(GUIManager.activeMainPanel.getRadarBase())) {
					int pixel = radarBase.center_X - radarBase.xoffset;//pixel为雷达所在x坐标的位置，radarBase.center_X为图中心位置
					int scanl = radarBase.center_Y - radarBase.yoffset;
					vcs.pointStart.x = (int) ((pointStart.x - pixel) * (1 / radarBase.scale_X));
					vcs.pointStart.y = (int) ((pointStart.y - scanl) * (1 / radarBase.scale_Y));
					vcs.pointEnd.x = (int) ((pointEnd2.x - pixel) * (1 / radarBase.scale_X));
					vcs.pointEnd.y = (int) ((pointEnd2.y - scanl) * (1 / radarBase.scale_Y));
					vcs.update = true;
					vcs.repaint();

				}
			}
		}


//
//		ARCoord arcEnd = PositionUtils.toARCoord(pointEnd.x, pointEnd.y,radarBase);
//		LLCoord llcEnd = PositionUtils.toLLCoord(arcEnd.azimuth, PositionUtils.toR(arcEnd.r, radarBase.l2
//				.getElevation(radarBase.cutNum)));
//		LinePosition p = linePostions.get(this);
//		p.longitudeEnd=llcEnd.longitude;
//		p.latitudeEnd=llcEnd.latitude;
//
//
//
//		int pixel = radarBase.center_X - radarBase.xoffset;//pixel为雷达所在x坐标的位置，radarBase.center_X为图中心位置
//		int scanl = radarBase.center_Y - radarBase.yoffset;
//		List<Float> list=new ArrayList<Float>();
//		list.add((float)((pointStart.x-pixel)*(1/radarBase.scale_X)));
//		list.add((float)(-(scanl-pointStart.y)*(1/radarBase.scale_Y)));
//		list.add((float)((pointEnd.x-pixel)*(1/radarBase.scale_X)));
//		list.add((float)(-(scanl-pointEnd.y)*(1/radarBase.scale_Y)));
//		List<String> list1=new ArrayList<String>();
//		list1.add(radarBase.l2.getSrcFileName());
		//javax.swing.JOptionPane.showMessageDialog(this, RadarBase.l2.getSrcFileName(), "R", JOptionPane.ERROR_MESSAGE);
		//String re=coal(list);

		//javax.swing.JOptionPane.showMessageDialog(this, ((pointStart.x-pixel)*(1/RadarBase.scale_X))+"llllllll"+((pointEnd.x-pixel)*(1/RadarBase.scale_X))+RadarBase.scale_X+RadarBase.scale_Y, "l"+re, JOptionPane.ERROR_MESSAGE);
//    	  pointStart = null;


		if(GUIManager.syncTool) {
			repaintOthers();
		}
		VCS.changepanel=0;
	}

	private void repaintOthers() {
		GUIManager.jlayers.forEach( p -> {
			p.repaint();
		});
	}

	public void mouseDragged(MouseEvent e) {
		if (!mousePressed) {
			return;
		}
			pointEnd = e.getPoint();
			ARCoord arcEnd = PositionUtils.toARCoord(pointEnd.x, pointEnd.y, radarBase);
			LLCoord llcEnd = PositionUtils.toLLCoord(arcEnd.azimuth, PositionUtils.toR(arcEnd.r, radarBase.l2
					.getElevation(radarBase.cutNum)));
			LinePosition p = linePostions.get(this);
			p.longitudeEnd = llcEnd.longitude;
			p.latitudeEnd = llcEnd.latitude;
			e.getComponent().repaint();
			if (GUIManager.syncTool) {
				repaintOthers();
			}
//         repaint();
		}


	public void mouseMoved(MouseEvent e) {
			if (mousePressed) {
				pointEnd = e.getPoint();
				ARCoord arcEnd = PositionUtils.toARCoord(pointEnd.x, pointEnd.y, radarBase);
				LLCoord llcEnd = PositionUtils.toLLCoord(arcEnd.azimuth, PositionUtils.toR(arcEnd.r, radarBase.l2
						.getElevation(radarBase.cutNum)));
				LinePosition p = linePostions.get(this);
				p.longitudeEnd = llcEnd.longitude;
				p.latitudeEnd = llcEnd.latitude;
			}
			if (GUIManager.syncTool) {
				repaintOthers();
			}

	}

	public static String coal(List<Float> list1)
	{ StringBuilder sbError= new StringBuilder();
		StringBuilder sb= new StringBuilder();
		try {
			//String condaPath1 = "D:\\anconda\\Scripts";//获取conda的系统变量
			//System.out.println(condaPath1);

			String path ="D:\\python代码\\PythonProject\\pycwr\\ceshi8.py ";//获取到项目目录后，补充完整python文件所在目录
			path =path.replace("\\","\\\\");
			String condaPath = "D:\\anconda\\Scripts";//获取conda的系统变量
			//System.out.println(condaPath);
			Process process = Runtime.getRuntime().exec(condaPath+"\\activate.bat &&  C: && conda activate pycwrnc && python "+path+list1);

			//Process process = Runtime.getRuntime().exec(condaPath+"\\activate.bat &&  C: && conda activate pycwrnc && python "+path+" && "+list1);

			int re=process.waitFor();//re=0成功，re=1失败
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			in = new BufferedReader(new InputStreamReader(process.getInputStream(),"gbk"));
			//接收错误流
			BufferedReader isError = new BufferedReader(new InputStreamReader(process.getErrorStream(),"gbk"));


			String line=null;
			String lineError= null;
			process.waitFor();
			while ((line = in.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			System.out.println(sb);

			while ((lineError= isError.readLine()) != null) {
				sbError.append(lineError);
				sbError.append("\n");
			}

			System.out.println(sbError);
			in.close();
			isError.close();


		} catch (Exception e) {
			e.printStackTrace();}

		return sb.toString();


	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
//		pointStart = null;
//		pointEnd = null;
		wheel=true;
		int num = e.getWheelRotation();
		if (num == 0) {
			return;
		} else if (num > 0) {
			num = radarBase.zoom / 2;
			if (num < radarBase.MIN_ZOOM) {
				return;
			}
			if(null != pointStart) {
				pointStart.x = pointStart.x-(pointStart.x-e.getX())/2;
				pointStart.y = pointStart.y-(pointStart.y-e.getY())/2;
			}
			if(null != pointEnd) {
				pointEnd.x = pointEnd.x-(pointEnd.x-e.getX())/2;
				pointEnd.y = pointEnd.y-(pointEnd.y-e.getY())/2;
			}

		} else {
			num = radarBase.zoom * 2;
			if (num > radarBase.MAX_ZOOM) {
				return;
			}
			if(null != pointStart) {
				pointStart.x = pointStart.x+(pointStart.x-e.getX());
				pointStart.y = pointStart.y+(pointStart.y-e.getY());
			}
			if(null != pointEnd) {
				pointEnd.x = pointEnd.x+(pointEnd.x-e.getX());
				pointEnd.y = pointEnd.y+(pointEnd.y-e.getY());
			}
		}
		if(GUIManager.syncTool) {
			repaintOthers();
		}

	}

}



//
//    public  String coal(List<String> list)
//	{ StringBuilder sbError= new StringBuilder();
//	 StringBuilder sb= new StringBuilder();
//    	 try {
//    			//String condaPath1 = "D:\\anconda\\Scripts";//获取conda的系统变量
//    			//System.out.println(condaPath1);
//
//    			String path ="D:\\python代码\\PythonProject\\pycwr\\ceshi8.py ";//获取到项目目录后，补充完整python文件所在目录
//    			path =path.replace("\\","\\\\");
//    			String condaPath = "D:\\anconda\\Scripts";//获取conda的系统变量
//    			//System.out.println(condaPath);
//    			Process process = Runtime.getRuntime().exec(condaPath+"\\activate.bat &&  C: && conda activate pycwrnc && python "+path+list);
//    			//Radar.radar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//    		//	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//
//   			 final InputStream is1 = process.getInputStream();
//   		        new Thread(() -> {
//   		            BufferedReader br = new BufferedReader(new InputStreamReader(is1));
//   		            try{
//   		                while(br.readLine() != null) ;
//   		                br.close();
//   		            }
//   		            catch(Exception e) {
//   		                e.printStackTrace();
//   		            }
//   		        }).start();
//   		        InputStream is2 = process.getErrorStream();
//   		        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
//   		        while(br2.readLine() != null){}
//   		        int i = process.waitFor();
//   		       // Radar.radar.setCursor(Cursor.getDefaultCursor());
//   		      // this.setCursor(Cursor.getDefaultCursor());
//   		 //     this.setCursor(CommonUtils.createCustomCursor(
//   		//			"resource/measure_32.gif", new Point(3, 3), "measure"));
//   		        br2.close();
//
//
//
//    		} catch (Exception e) {
//    			e.printStackTrace();}
//
//    	 return sb.toString();
//
//
//		}
//	@Override
//	public void mouseWheelMoved(MouseWheelEvent e) {
//		this.list.clear();
//	}



