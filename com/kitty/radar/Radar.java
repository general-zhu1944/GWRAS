package com.kitty.radar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.jci.monitor.FilesystemAlterationMonitor;

import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.listener.BasicWindowHandler;
import com.kitty.radar.listener.FileHandler;
import com.kitty.radar.listener.RadarProcessor;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.ConfigInfo;

public class Radar extends JFrame {

	public static final String APP_NAME = "南充市天气雷达显示分析软件系统";

	public static final String APP_VERSION = "1.0";

	public static final String PHONE = "15680302675";

	public static final String HOME_PAGE = "";

	public static final String E_MAIL = "275551265@qq.com";

	public static final String AUTHOR = "竹利";

	public static final String COPYRIGHT_INFO = "版权所有 (C) 2020-2023";

	public static RadarProcessor processor = new RadarProcessor();

	public static Radar radar;

	private static FilesystemAlterationMonitor fam;

	public static byte updateRate = 30; // 自动更新间隔，单位：秒

	public static boolean showToolBar = true;

	public static boolean showStatus = true;

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
		ConfigInfo.readConfigInfo();//读取配置文件
		this.setTitle(APP_NAME);
		this.setJMenuBar(GUIManager.createMenu(processor));//设置菜单栏
		Container pane = this.getContentPane();//实例化容器对象
//		pane.add(GUIManager.createToolBar(processor), BorderLayout.NORTH);//向容器添加工具栏
//		pane.add(GUIManager.createStatusBar(), BorderLayout.SOUTH);//向容器添加状态栏
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
			UIManager.getDefaults().put("FileChooser.cancelButtonText", "取消");
			UIManager.getDefaults().put("FileChooser.cancelButtonToolTipText",
					"关闭对话框");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Font font = new Font("宋体", Font.PLAIN, 12);
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
