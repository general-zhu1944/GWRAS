package com.kitty.radar.listener;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import com.kitty.component.gui.FileChooser;
import com.kitty.radar.VCS;
import com.kitty.radar.MapOverlay;
import com.kitty.radar.PPI;
import com.kitty.radar.RHI;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.RadarParams;
import com.kitty.radar.RainOverlay;
import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.business.cr.CR;
import com.kitty.radar.business.surface.RadarDialog;
import com.kitty.radar.business.surface.SurfaceDialog;
import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.ListElement;
import com.kitty.radar.gui.*;
import com.kitty.radar.util.BZip2;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.ConfigInfo;
import com.kitty.radar.util.RadarUtils;

//容器类监听
public class RadarProcessor extends RadarParams implements ActionListener {

	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		Cursor oldCursor = Radar.radar.getCursor();
		Radar.radar.setCursor(new Cursor(Cursor.WAIT_CURSOR));
/*		if (CommonProps.AC_REFLECTIVITY.equals(command)) {
			basicMoment(CommonProps.MOMENT_R, RadarData.DBZ);
		} else if (CommonProps.AC_VELOCITY.equals(command)) {
			basicMoment(CommonProps.MOMENT_V, RadarData.V);
		} else if (CommonProps.AC_SPECTRUM_WIDTH.equals(command)) {
			basicMoment(CommonProps.MOMENT_W, RadarData.W);
		} else if (CommonProps.AC_DBT.equals(command)) {
			basicMoment(CommonProps.MOMENT_DBT, RadarData.DBT);
		} else if (CommonProps.AC_ZDR.equals(command)) {
			basicMoment(CommonProps.MOMENT_ZDR, RadarData.ZDR);
		} else if (CommonProps.AC_KDP.equals(command)) {
			basicMoment(CommonProps.MOMENT_KDP, RadarData.KDP);
		} else if (CommonProps.AC_DP.equals(command)) {
			basicMoment(CommonProps.MOMENT_DP, RadarData.DP);
		} else if (CommonProps.AC_CC.equals(command)) {
			basicMoment(CommonProps.MOMENT_CC, RadarData.CC);
		} else if (CommonProps.AC_SNRH.equals(command)) {
			basicMoment(CommonProps.MOMENT_SNRH, RadarData.SNRH);
		} else if (CommonProps.AC_LIQUID_WATER.equals(command)) {
			basicMoment(CommonProps.MOMENT_LW, RadarData.DBZ);
		} else if (CommonProps.AC_VERTICAL_LIQUID_WATER.equals(command)) {
			basicMoment(CommonProps.MOMENT_VIL, RadarData.DBZ);
		} else if (CommonProps.AC_ECHO_TOPS.equals(command)) {
			basicMoment(CommonProps.MOMENT_ET, RadarData.DBZ);
		} else if (CommonProps.AC_HAIL_PROBABILITY.equals(command)) {
			basicMoment(CommonProps.MOMENT_HP, RadarData.DBZ);*/
	//	} else
			if (CommonProps.AC_SAVE_AS.equals(command)) {
			saveAs();
		} else if (CommonProps.AC_EXPORT_PPI.equals(command)) {
			exportFile(CommonProps.VIEW_PPI);
		} else if (CommonProps.AC_EXPORT_CAPPI.equals(command)) {
			exportFile(CommonProps.VIEW_CAPPI);
		} else if (CommonProps.AC_EXPORT_RHI.equals(command)) {
			exportFile(-1);
		} else if (CommonProps.AC_PAGE_SETUP.equals(command)) {
			pageSetup(false);
		} else if (CommonProps.AC_PREVIEW_PAGE_SETUP.equals(command)) {
			this.previewDialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			pageSetup(true);
			this.previewDialog.setCursor(Cursor.getDefaultCursor());
		} else if (CommonProps.AC_PRINT_PREVIEW.equals(command)) {
			this.previewDialog = new PrintPreviewDialog(Radar.radar);
			this.previewDialog.setVisible(true);
		} else if (CommonProps.AC_PREVIEW_PRINT.equals(command)) {
			print(true);
		} else if (CommonProps.AC_PRINT.equals(command)) {
			print(false);
		} else if (CommonProps.AC_EXIT_APP.equals(command)) {
			exitApp();
		} else if (CommonProps.AC_RHI.equals(command)) {
			RHI.createRhiDialog(Radar.radar);
		} else if (CommonProps.AC_GRID.equals(command)) {
			grid();
		} else if (CommonProps.AC_MAP.equals(command)) {
			map();
		} else if (CommonProps.AC_TERRAIN.equals(command)) {
			terrain();			
		} else if (CommonProps.AC_POINT.equals(command)) {
			point();
		} else if (CommonProps.AC_TRACK.equals(command)) {
			track();
		} else if (CommonProps.AC_DiscreteData.equals(command)) {
			rain();
		} else if (CommonProps.AC_AREA.equals(command)) {
			circle();
		} else if (CommonProps.AC_ABOUT.equals(command)) {
			AboutDialog.createAboutDialog(Radar.radar);
		} else if (CommonProps.AC_SITE_INFO.equals(command)) {
			SiteInfoDialog.createSiteInfoDialog(Radar.radar);
		} else if (CommonProps.AC_TQ_INFO.equals(command)) {
			TianQingInfoDialog.createSiteInfoDialog(Radar.radar);
		} else if (CommonProps.AC_MOMENT_RANGE.equals(command)) {
		} else if (CommonProps.AC_BACKGROUND.equals(command)) {
			BackgroundDialog.createBackgroundDialog(Radar.radar);
		} else if (CommonProps.AC_HAILPROPS.equals(command)) {
		} else if (CommonProps.AC_RESOLUTION.equals(command)) {
			new ResolutionDialog(Radar.radar);
		} else if (CommonProps.AC_T_CURSOR.equals(command)) {
			JMainPanelLayerUi.active = 0;
			t_cursor(ae);
		} else if (CommonProps.AC_T_HAND.equals(command)) {
			JMainPanelLayerUi.active = 0;
			t_hand(ae);
		} else if (CommonProps.AC_T_ZOOM_IN.equals(command)) {
			JMainPanelLayerUi.active = 0;
			t_zoomIn(ae);
		} else if (CommonProps.AC_T_ZOOM_OUT.equals(command)) {
			JMainPanelLayerUi.active = 0;
			t_zoomOut(ae);
		} else if (CommonProps.AC_T_MEASURE.equals(command)) {
			JMainPanelLayerUi.active = 2;
			t_measure(ae);
		} else if (CommonProps.AC_T_VCS_MENU.equals(command)) {
				VCS.createVcsDialog(Radar.radar);
		} else if (CommonProps.AC_T_VCS.equals(command)) {
			JMainPanelLayerUi.active = 1;//1 画线, 2 测距
			t_drawline(ae);

		//} else if (CommonProps.AC_T_WIND.equals(command)) {
		//	t_wind(ae);
		} else if (CommonProps.AC_T_GRID.equals(command)) {
			grid();
		} else if (CommonProps.AC_QUERY_RDAR.equals(command)) {
			queryradar();
		} else if (CommonProps.AC_QUERY_RAIN.equals(command)) {
			queryrain();
		} else if (CommonProps.AC_QUERY_TD.equals(command)) {
			query("td");
		} else if (CommonProps.AC_QUERY_TEMPER.equals(command)) {
			query("temper");
		} else if (CommonProps.AC_QUERY_WINDMAX.equals(command)) {
			query("windmax");
		} else if (CommonProps.AC_SURFACE.equals(command)) {
			surface();
		} else if (CommonProps.AC_DOWNLOAD_RADAR.equals(command)) {
			downloadradar();
		} else if (CommonProps.AC_T_MAP.equals(command)) {
			map();
		} else if (CommonProps.AC_T_CROSS.equals(command)) {
			autoUpdate();
		} else if (CommonProps.AC_T_RESET.equals(command)) {
			t_reset();
		} else if (CommonProps.AC_NEW.equals(command)) {
			autoUpdate();
		} else if (CommonProps.AC_PREVIOUS_FILE.equals(command)) {
			browserFile(-1);
		} else if (CommonProps.AC_NEXT_FILE.equals(command)) {
			browserFile(1);
		} else if (CommonProps.AC_LOOP_FILE.equals(command)) {
			loopFile();
		} else if (CommonProps.AC_STOP_LOOP.equals(command)) {
			stopLoop();
		} else if (CommonProps.AC_CAPPI.equals(command)) {
			//cappi();
		} else if (CommonProps.AC_SHOW_RIGHT_PANEL.equals(command)) {
			Radar.showRightPanel = !Radar.showRightPanel;
			GUIManager.rightPanel.setVisible(Radar.showRightPanel);
/*		} else if (CommonProps.AC_SHOW_STATUS.equals(command)) {
			Radar.showStatus = !Radar.showStatus;
			GUIManager.statusBar.setVisible(Radar.showStatus);
		} else if (CommonProps.AC_SHOW_TOOL_BAR.equals(command)) {
			Radar.showToolBar = !Radar.showToolBar;
			GUIManager.toolBar.setVisible(Radar.showToolBar);*/
		} else if (CommonProps.AC_HELP.equals(command)) {
			help();
		} else if (CommonProps.AC_EXPORT_SET.equals(command)) {
			new ExportSetDialog();
		} else if (CommonProps.AC_EXPORT_CURRENT.equals(command)) {
			exportCurrent();
		} else {
//			switchCut(ae);//层次选择事件
		}
		Radar.radar.setCursor(oldCursor);
	}
// 	Cursor oldCursor1 = Radar.radar.getCursor();
//	JOptionPane.showMessageDialog(null,oldCursor1.getName()); 
	private void queryrain() {		
		  Date str1= (Date)(SurfaceDialog.datePicker1.getValue());
      	Date str2=(Date)(SurfaceDialog.datePicker2.getValue());
      	if((str2.getTime()-str1.getTime())<60000)//毫秒
      	{
      		JOptionPane.showMessageDialog(null, "你选择时间段太短，请重新选择时间段 ！"); 
      	}
      	else
      	{
      		MainPanel mainPanel = GUIManager.activeMainPanel;
      		RainOverlay rain = mainPanel.getRain();
      		rain.CreateDiscreteData(str1, str2);		
      		rain.element="rain";
		
			if(rain._discreteData!=null)
			{
				RainOverlay.rain_on = true;	
				try {
					GUIManager.setToolMapIcon();
				} catch (Exception e) {
					e.printStackTrace();
				}
				SurfaceDialog.jCheckBox_SurfaceData.setSelected(RainOverlay.rain_on);
				rain.update = true;
				mainPanel.repaint();
	      	}
      	}
      	
	}
	private void queryradar() {		
		  Date str1= (Date)(RadarDialog.datePicker1.getValue());
    	 Date str2=(Date)(RadarDialog.datePicker2.getValue());
    	 String datatype=RadarDialog.jcb.getSelectedItem().toString();
    	 String radarid=RadarDialog.rada_id.getText().toString();
    	if((str2.getTime()-str1.getTime())<60000)//毫秒
    	{
    		JOptionPane.showMessageDialog(null, "你选择时间段太短，请重新选择时间段 ！"); 
    	}
    	else
    	{
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm00");
   	        try {
            String date1 = sdf.format(str1);
            String date2 = sdf.format(str2);
    		RadarDialog.downradar(date1, date2, datatype, radarid, RadarParams.userId, RadarParams.pw, RadarParams.radarSavePath+"\\");
            } catch (Exception e) {
               e.printStackTrace();
            }
    	}
    	
	}
	private void query(String element) {
		Date str1 = (Date) (SurfaceDialog.datePicker1.getValue());
		MainPanel mainPanel = GUIManager.activeMainPanel;
		RainOverlay rain = mainPanel.getRain();
		rain.CreateDiscreteData(str1, element);
		rain.element = element;
		if (rain._discreteData != null) {
			RainOverlay.rain_on = true;
			try {
				GUIManager.setToolMapIcon();
			} catch (Exception e) {
				e.printStackTrace();
			}
			SurfaceDialog.jCheckBox_SurfaceData.setSelected(RainOverlay.rain_on);
			rain.update = true;
			mainPanel.repaint();
		}
	}
	
	
	
	
	
	private void exportCurrent() {
		String fileName = "";
		int type = 1;
		RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
		if (radarBase.l2 != null && radarBase.l2.raf != null) {
			fileName = FileHandler.getTime(radarBase.l2.getSrcFileName());
		} else {
			CommonUtils.alert("没有可以导出的数据", null);
			return;
		}
		if (radarBase.currentMoment == CommonProps.MOMENT_ET) {
		} else if (radarBase.currentMoment == CommonProps.MOMENT_HP) {
		} else if (radarBase.currentMoment == CommonProps.MOMENT_VIL) {
		} else {
			if (radarBase.view == CommonProps.VIEW_CAPPI) {
				this.exportFile(CommonProps.VIEW_CAPPI);
				return;
			} else if (radarBase.view == CommonProps.VIEW_PPI) {
				if (radarBase.cutNum == CommonProps.MOMENT_CR) {
					type = 2;
				} else {
					this.exportFile(CommonProps.VIEW_PPI);
					return;
				}
			} else {
				return;
			}
		}
		fileName += "_" + RadarUtils.getMomentShort(GUIManager.activeMainPanel.getRadarBase()) + ".txt";

		boolean inArea = false;
		if (AreaDialog.tableModel.isAreaVisible()) {
			int flag = JOptionPane.showConfirmDialog(Radar.radar,
					"是否仅导出区域内的数据？", "", JOptionPane.YES_NO_OPTION);
			if (flag == JOptionPane.OK_OPTION) {
				inArea = true;
			}
		}
		FileChooser fc = FileChooser.getFileChooser();
		fc.setType(FileChooser.TYPE_SAVE);
		fc.setCurrentDirectory(new File(lastSavePath));
		fc.setSelectedFile(new File(fileName));
		fc.setFileFilter("*.txt");
		fc.setApproveButtonMnemonic(KeyEvent.VK_S);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("导出当前产品");
		int rtn = fc.showDialog(Radar.radar);
		File file = fc.getSelectedFile();
		if (file != null && rtn == FileChooser.APPROVE_OPTION) {
			String path = file.getAbsolutePath();
			lastSavePath = CommonUtils.getDirPath(path);
			try {
				BufferedWriter w = new BufferedWriter(new FileWriter(file));
				RadarUtils.exportGrid(w, inArea, type,GUIManager.activeMainPanel.getRadarBase());
				w.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void circle() {
		new AreaDialog();
	}
	
	private void surface() {
		new SurfaceDialog(this);
	}
	
	private void downloadradar() {
		new RadarDialog(this);
	}

	private void help() {
		try {
			Desktop.getDesktop().browse(new URI(Radar.HOME_PAGE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void exportFile(int type) {
		RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
		String fileName = "";
		if (radarBase.l2 != null && radarBase.l2.raf != null) {
			fileName = FileHandler.getTime(radarBase.l2.getSrcFileName());
		} else {
			CommonUtils.alert("没有可以导出的数据", null);
			return;
		}
		if (type == CommonProps.VIEW_PPI
				&& radarBase.cutNum == CommonProps.MOMENT_CR) {
			CommonUtils.alert("请选择要导出的仰角层", null);
			return;
		}
		String typeDesc;
		if (type == CommonProps.VIEW_PPI) {
			typeDesc = "PPI";
			fileName += "_" + RadarUtils.getMomentShort(radarBase) + "_" + typeDesc
					+ "_L" + GUIManager.getCurrentLayer() + ".txt";
		} else if (type == CommonProps.VIEW_CAPPI) {
			typeDesc = "CAPPI";
			fileName += "_" + RadarUtils.getMomentShort(radarBase) + "_" + typeDesc
					+ "_" + GUIManager.cappiText.getText() + "km.txt";
		} else {
			typeDesc = "RHI";
			fileName += "_" + RadarUtils.getMomentShort(radarBase) + "_" + typeDesc
					+ "_" + CommonUtils.format(RHI.azimuth, 1) + ".txt";
		}
		String label = null;
		if (radarBase.currentMoment == CommonProps.MOMENT_ET) {
			label = "云顶高度";
		} else if (radarBase.currentMoment == CommonProps.MOMENT_HP) {
			label = "冰雹概率";
		} else if (radarBase.currentMoment == CommonProps.MOMENT_VIL) {
			label = "垂直累积液水含量";
		}
		if (label != null) {
			CommonUtils.alert(label + "不能导出" + typeDesc, null);
			return;
		}
		boolean inArea = false;
		if (type == CommonProps.VIEW_PPI || type == CommonProps.VIEW_CAPPI) {
			if (AreaDialog.tableModel.isAreaVisible()) {
				int flag = JOptionPane.showConfirmDialog(Radar.radar,
						"是否仅导出区域内的数据？", "", JOptionPane.YES_NO_OPTION);
				if (flag == JOptionPane.OK_OPTION) {
					inArea = true;
				}
			}
		}
		FileChooser fc = FileChooser.getFileChooser();
		fc.setType(FileChooser.TYPE_SAVE);
		fc.setCurrentDirectory(new File(lastSavePath));
		fc.setSelectedFile(new File(fileName));
		fc.setFileFilter("*.txt");
		fc.setApproveButtonMnemonic(KeyEvent.VK_S);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("导出" + typeDesc);
		int rtn = fc.showDialog(Radar.radar);
		File file = fc.getSelectedFile();
		if (file != null && rtn == FileChooser.APPROVE_OPTION) {
			String path = file.getAbsolutePath();
			lastSavePath = CommonUtils.getDirPath(path);
			try {
				BufferedWriter w = new BufferedWriter(new FileWriter(file));
				if (type == CommonProps.VIEW_PPI) {
					PPI.exportPPI(w, inArea,radarBase);
				} else if (type == CommonProps.VIEW_CAPPI) {
				} else {
					if (RHI.rhi != null) {
						RHI.rhi.exportRHI(w);
					} else {
						RHI rhi = new RHI(radarBase);
						rhi.exportRHI(w);
					}
				}
				w.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void pageSetup(boolean update) {
		pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
		if (update) {
			JPanel panel = this.previewDialog.getPreviewPanel();
			if (panel != null) {
				panel.repaint();
			}
		}
	}

	private void print(boolean close) {
		PrinterJob job = PrinterJob.getPrinterJob();
		Book book = new Book();
		book.append(GUIManager.activeMainPanel, pageFormat);
		job.setPageable(book);
		if (job.printDialog()) {
			try {
				job.print();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (close) {
				this.previewDialog.dispose();
			}
		}
	}

//	private void cappi() {
//		try {
//			RadarBase.level = Float.parseFloat(GUIManager.cappiText.getText());
//			RadarBase.view = CommonProps.VIEW_CAPPI;
//			GUIManager.cappiButton.setSelected(true);
//			GUIManager.selectCutButton(null);
//			GUIManager.activeMainPanel.update = true;
////			GUIManager.mainPanel.repaint();
//			GUIManager.repaintCurrent();
//		} catch (NumberFormatException e) {
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	private void t_drawline(ActionEvent ae ) {
			setActiveToolButton(ae);
			GUIManager.currentToolCursor = CommonUtils.createCustomCursor(
					"resource/measure_32.gif", new Point(3, 3), "measure");
			GUIManager.getJpanels().forEach(panel -> {
				panel.setCursor(GUIManager.currentToolCursor);
			});

		}

/*
	private void t_wind(ActionEvent ae ) {
		setActiveToolButton(ae);
		GUIManager.mainPanel.setCursor(CommonUtils.createCustomCursor(
				"resource/measure_32.gif", new Point(3, 3), "measure"));
		vel_to_wind();
		//GUIManager.mainPanel.updateUI();
	}*/
	
	private void t_measure(ActionEvent ae) {
		setActiveToolButton(ae);
		GUIManager.currentToolCursor = CommonUtils.createCustomCursor(
				"resource/measure_32.gif", new Point(3, 3), "measure");
		GUIManager.getJpanels().forEach(panel -> {
			panel.setCursor(GUIManager.currentToolCursor);
//			panel.add(new MeasurePanel(panel.getRadarBase()), BorderLayout.CENTER);
//			panel.updateUI();
		});
	}
	/*private void vel_to_wind() {
    	List<String> list=new ArrayList<String>();
		list.add(RadarParams.filePath+"\\"+RadarBase.l2.getSrcFileName());
		list.add(Integer.toString(RadarBase.cutNum));

		//javax.swing.JOptionPane.showMessageDialog(this, RadarBase.active_moment, "R", JOptionPane.ERROR_MESSAGE);
	    	 try {
	    			String path ="D:\\python代码\\PythonProject\\pycwr\\cheshi9.py ";//获取到项目目录后，补充完整python文件所在目录
	    			path =path.replace("\\","\\\\");
	    			String condaPath = "D:\\anconda\\Scripts";//获取conda的系统变量	    			
	    			Process process = Runtime.getRuntime().exec(condaPath+"\\activate.bat &&  C: && conda activate pycwrnc && python "+path+list);
	    			//JOptionPane.showMessageDialog(Radar.radar, "请耐心等等！", "",
	    					//JOptionPane.WARNING_MESSAGE);
	    			Radar.radar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			 final InputStream is1 = process.getInputStream();
	    		        new Thread(() -> {
	    		            BufferedReader br = new BufferedReader(new InputStreamReader(is1));
	    		            try{
	    		                while(br.readLine() != null) ;
	    		                br.close();
	    		            }
	    		            catch(Exception e) {
	    		                e.printStackTrace();
	    		            }
	    		        }).start();
	    		        InputStream is2 = process.getErrorStream();
	    		        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
	    		        while(br2.readLine() != null){}
	    		        int i = process.waitFor();
	    		        //Radar.radar.setCursor(Cursor.getDefaultCursor());
	    		        Radar.radar.setCursor(CommonUtils.createCustomCursor(
	    	   					"resource/measure_32.gif", new Point(3, 3), "measure"));
	    		        br2.close();

	    			
	    			
	    		} catch (Exception e) {
	    			}
	    		  
	}*/

	private void stopLoop() {
		GUIManager.setFileEnabled(true, false);
		GUIManager.loop.setActionCommand(CommonProps.AC_LOOP_FILE);
		GUIManager.loop.setText(" 循环动画 ");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void loopFile() {
		int index = GUIManager.list.getSelectedIndex();
		if (index == -1) {
			JOptionPane.showMessageDialog(Radar.radar, "请选择要循环显示的文件", "",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		GUIManager.setFileEnabled(false, false);
		GUIManager.loop.setActionCommand(CommonProps.AC_STOP_LOOP);
		GUIManager.loop.setText(" 停止 ");
		timer = new Timer();
		timer.schedule(new LoopHandler(), 200, Math.round(timerRate * 1000)); // 延迟200毫秒，增强用户体验
	}

	private void browserFile(int i) {
		JList list = GUIManager.list;
		int size = list.getModel().getSize();
		if (size > 0) {
			int index = list.getSelectedIndex() + i;
			if (index < 0) {
				index = size - 1;
			} else if (index >= size) {
				index = 0;
			}
			list.setSelectedIndex(index);
			//long aTime=System.currentTimeMillis();
			displayFile(null);			
			//long bTime=System.currentTimeMillis();
			//JOptionPane.showMessageDialog(null, "体扫切换所需时间"+(bTime-aTime)+"毫秒","测试时差",JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void t_reset() {
		if(GUIManager.isSyncTool()) {
			GUIManager.getJpanels().forEach(panel -> {
				RadarBase radarBase = panel.getRadarBase();
				radarBase.setXoffset(0);
				radarBase.setYoffset(0);
				radarBase.setZoom(1);
				panel.xoffset = 0;
				panel.yoffset = 0;
//				MapOverlay.update = true;
				panel.getMap().update = true;
				panel.getRain().update = true;
				panel.update = true;
				GUIManager.repaintAll();
			});
		} else {
			RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
			radarBase.setXoffset(0);
			radarBase.setYoffset(0);
			radarBase.setZoom(1);
			GUIManager.activeMainPanel.xoffset = 0;
			GUIManager.activeMainPanel.yoffset = 0;
//			MapOverlay.update = true;
			GUIManager.activeMainPanel.getMap().update = true;
			GUIManager.activeMainPanel.getRain().update = true;
			GUIManager.activeMainPanel.update = true;
			GUIManager.repaintCurrent();
		}
	}


	private void autoUpdate() {
		if (CommonProps.AC_STOP_LOOP.equals(GUIManager.loop.getActionCommand())) {
			stopLoop();
		}
		GUIManager.setFileEnabled(auto_update, true);
		auto_update = !auto_update;
		try {
			GUIManager.setToolCrossIcon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GUIManager.menuUpdate.setSelected(auto_update);
	}

	private void t_cursor(ActionEvent ae) {
		setActiveToolButton(ae);
		GUIManager.currentToolCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		GUIManager.getJpanels().forEach(panel -> {
			panel.setCursor(GUIManager.currentToolCursor);
		});
	}

	private void t_hand(ActionEvent ae) {
		setActiveToolButton(ae);
		GUIManager.currentToolCursor = CommonUtils.createCustomCursor(
				"resource/hand_32.gif", new Point(9, 9), "move");
		GUIManager.getJpanels().forEach(panel -> {
			panel.setCursor(GUIManager.currentToolCursor);
		});
	}

	private void t_zoomIn(ActionEvent ae) {
		setActiveToolButton(ae);

		GUIManager.currentToolCursor = CommonUtils.createCustomCursor(
				"resource/zoom_in_32.png", new Point(6, 6), "zoom in");
//		if(GUIManager.syncSelected) {
		GUIManager.getJpanels().forEach(panel -> {
			panel.setCursor(GUIManager.currentToolCursor);
		});
//		} else {
//			GUIManager.activeMainPanel.setCursor(CommonUtils.createCustomCursor(
//					"resource/zoom_in_32.png", new Point(6, 6), "zoom in"));
//		}
	}

	private void t_zoomOut(ActionEvent ae) {
		setActiveToolButton(ae);
		GUIManager.currentToolCursor = CommonUtils.createCustomCursor(
				"resource/zoom_out_32.png", new Point(6, 6), "zoom out");
		GUIManager.getJpanels().forEach(panel -> {
			panel.setCursor(GUIManager.currentToolCursor);
		});
	}

	private void setActiveToolButton(ActionEvent ae) {
		JButton b = (JButton) ae.getSource();
		if (GUIManager.activeToolButton != null
				&& GUIManager.activeToolButton != b) {
			GUIManager.activeToolButton.setSelected(false);
		}
		String command = GUIManager.activeToolButton.getActionCommand();
		if (CommonProps.AC_T_MEASURE.equals(command)) {
//			GUIManager.mainPanel.removeAll();
//			GUIManager.mainPanel.updateUI();
		} else if (CommonProps.AC_T_VCS.equals(command)) {
//			GUIManager.mainPanel.removeAll();
//			GUIManager.mainPanel.updateUI();
//			//((JCheckBoxMenuItem) GUIManager.getComponent("menu_vcs"))
					//.setSelected(false);
		}
		b.setSelected(true);
		GUIManager.activeToolButton = b;
	}

/*	public void basicMoment(int moment, int activeMoment) {
		RadarBase.active_moment = activeMoment;
		RadarBase.currentMoment = moment;
		GUIManager.createCutButtons(this);
		GUIManager.updateComponents();
	}*/

	private void grid() {
		MapOverlay.grid_on = !MapOverlay.grid_on;
		try {
			GUIManager.setToolGridIcon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GUIManager.menuGrid.setSelected(MapOverlay.grid_on);
//		MapOverlay.update = true;
		GUIManager.repaintAll();
	}

	private void map() {
		MapOverlay.map_on = !MapOverlay.map_on;
		try {
			GUIManager.setToolMapIcon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GUIManager.menuMap.setSelected(MapOverlay.map_on);
//		MapOverlay.update = true;
		GUIManager.repaintAll();
	}
	private void terrain() {
		MapOverlay.elevation_on = !MapOverlay.elevation_on;
		try {
			GUIManager.setToolMapIcon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GUIManager.menuTerrain.setSelected(MapOverlay.elevation_on);
		GUIManager.getJpanels().forEach(panel -> {
			panel.getMap().update = true;
			panel.update = true;
			panel.repaint();
		});
	}
	private void rain() {
		RainOverlay.rain_on = !RainOverlay.rain_on;	
		try {
			GUIManager.setToolMapIcon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SurfaceDialog.jCheckBox_SurfaceData.setSelected(RainOverlay.rain_on);
		
		if(GUIManager.isSyncTool()) {
			GUIManager.getJpanels().forEach(panel -> {
				panel.getRain().update = true;
				panel.repaint();
			});
		} else {
			GUIManager.activeMainPanel.getRain().update = true;
			GUIManager.activeMainPanel.repaint();
		}
		
	}

	private void point() {
		MapOverlay.point_on = !MapOverlay.point_on;
//		MapOverlay.update = true;
		GUIManager.repaintAll();
	}

	private void track() {
		MapOverlay.track_on = !MapOverlay.track_on;
		//MapOverlay.update = true;
		GUIManager.repaintAll();
	}

	private void saveAs() {
		RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
		FileChooser fc = FileChooser.getFileChooser();
		fc.setType(FileChooser.TYPE_SAVE);
		fc.setCurrentDirectory(new File(lastSavePath));
		String fileName = null;
		if (radarBase.l2 != null && radarBase.l2.raf != null) {
			fileName = FileHandler.getTime(radarBase.l2.getSrcFileName())
					+ ".jpg";
			fc.setSelectedFile(new File(fileName));
		} else {
			fc.setSelectedFile(new File(""));
		}
		fc.setFileFilter("*.jpg");
		fc.setApproveButtonMnemonic(KeyEvent.VK_S);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("另存为");
		int rtn = fc.showDialog(Radar.radar);
		File file = fc.getSelectedFile();
		if (file != null && rtn == FileChooser.APPROVE_OPTION) {
			String path = file.getAbsolutePath();
			lastSavePath = CommonUtils.getDirPath(path);
			try {
				String name = file.getName();
				if (name.indexOf(".") == -1) {
					file = new File(path + ".jpg");
				}
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));

				BufferedImage image = new BufferedImage(radarBase.getWidth(),
						radarBase.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D) image.createGraphics();
				GUIManager.activeMainPanel.drawImage(g, 0, 0);
				g.dispose();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static void exitApp() {
		ConfigInfo.writeConfiguration();
		GUIManager.getJpanels().forEach( panel -> {
			if (panel.getRadarBase().l2 != null) {
				panel.getRadarBase().l2.close();
			}

		});
		System.exit(0);
	}

	public static void displayFile(ListElement elem) {
		RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
		if (radarBase.l2 != null) {
			radarBase.l2.close();
			radarBase.l2 = null;
		}
		if (elem == null) {
			elem = (ListElement) GUIManager.list.getSelectedValue();
		}
		if (elem != null) {
			File file = new File(filePath, elem.getLabel());		
			if (elem.getLabel().toLowerCase().endsWith(".bz2")) {
				file = extractFile(file);
			}
			if (file != null) {
				radarBase.l2 = RadarUtils.createRadarData(radarBase);
				radarBase.l2.setSrcFileName(elem.getLabel());
				String oldSiteCode = radarBase.siteCode;
				if (radarBase.l2.open(file)) {
					if (radarBase.l2.vcp != null && !radarBase.l2.vcp.equals(radarBase.vcp)) {
						radarBase.vcp = radarBase.l2.vcp;
						GUIManager.vcpBorder.setTitle("VCP" + radarBase.vcp);
					}
					radarBase.resolution = radarBase.l2.resolution;
					int radius = RadarUtils.getRadarRadius();
					if (radius != radarBase.radius) {
						radarBase.setRadarFormat(radarBase.radarFormat);
						CR.setResolution(CR.getResolution());
		                VIL.setResolution(VIL.getResolution());
					}
					GUIManager.updateMomnetMenu(radarBase.l2);
					if(GUIManager.isSyncTime()) {
						GUIManager.syncAllMainPanel4Time();
					}
				} else {
					CommonUtils
							.alert(radarBase.l2.getSrcFileName() + "不是"
									+ RadarUtils.getRadarFormatLabel()
									+ "雷达数据文件", null);
					radarBase.l2.close();
					radarBase.l2 = null;
				}
			}
		}
		GUIManager.createCutButtons();
		GUIManager.updateComponentsAll();
	}
	private static File extractFile(File file) {
		String path = CommonUtils.getTempDir();
		File f = new File(path, "radar_bz2.bin");
		try {
			if (BZip2.extract(file.getAbsolutePath(), f.getAbsolutePath())) {
				return f;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void startDeleteTimer() {
		stopDeleteTimer();
		if (enableDelete) {
			deleteTimer = new Timer();
			deleteTimer.schedule(new DeleteHandler(), 20000, 43200000);
		}
	}

	public void stopDeleteTimer() {
		if (deleteTimer != null) {
			deleteTimer.cancel();
			deleteTimer = null;
		}
	}

}
