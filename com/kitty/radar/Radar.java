package com.kitty.radar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.jci.monitor.FilesystemAlterationMonitor;

import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.listener.BasicWindowHandler;
import com.kitty.radar.listener.FileHandler;
import com.kitty.radar.listener.RadarProcessor;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.ConfigInfo;

public class Radar extends JFrame {

	public static final String APP_NAME = "南充天气雷达显示分析系统";

	public static final String APP_VERSION = "1.0";

	public static final String PHONE = "15680302675";

	public static final String HOME_PAGE = "";

	public static final String E_MAIL = "275551265@qq.com";

	public static final String AUTHOR = "竹利";

	public static final String COPYRIGHT_INFO = "川东北强天气研究南充市重点实验室 (C)2021";

	public static RadarProcessor processor = new RadarProcessor();

	public static Radar radar;

	private static FilesystemAlterationMonitor fam;

	public static byte updateRate = 30; // 锟皆讹拷锟斤拷锟铰硷拷锟斤拷锟斤拷锟轿伙拷锟斤拷锟�

//	public static boolean showToolBar = true;

//	public static boolean showStatus = true;

	public static boolean showRightPanel = true;

	public Radar() {
		try {
			this.setIconImage(CommonUtils
					.getResImage("resource/computer_s.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		addWindowListener(new BasicWindowHandler());
		ConfigInfo.readConfigInfo();
		this.setTitle(APP_NAME);
		this.setJMenuBar(GUIManager.createMenu(processor));
		Container pane = this.getContentPane();
//		pane.add(GUIManager.createToolBar(processor), BorderLayout.NORTH);
//		pane.add(GUIManager.createStatusBar(), BorderLayout.SOUTH);
		GUIManager.createPanels(processor, pane);

		startFileMonitor();
		processor.startDeleteTimer();
	}

	public synchronized static void startFileMonitor() {
		if (fam != null) {
			fam.stop();
			GUIManager.removeAllListElems();
		}
		fam = new FilesystemAlterationMonitor();
		fam.addListener(new File(RadarParams.filePath), new FileHandler());
		fam.setInterval(updateRate * 1000);
		fam.setStopDelay(200);
		fam.start();
	}

	public static void main(String args[]) {
		File file = new File(CommonUtils.getUserHomeDir(), "output.log");
		try {
			PrintStream ps = new PrintStream(file);
			System.setErr(ps);
			System.setOut(ps);
			UIManager
					.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
			UIManager.getDefaults().put("FileChooser.cancelButtonText", "取锟斤拷");
			UIManager.getDefaults().put("FileChooser.cancelButtonToolTipText",
					"锟截闭对伙拷锟斤拷");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Font font = new Font("锟斤拷锟斤拷", Font.PLAIN, 12);
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (UIManager.get(key) instanceof FontUIResource) {
				UIManager.put(key, font);
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				radar = new Radar();
				radar.setVisible(true);
			}
		});
	}

}
