package com.kitty.radar;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.swing.JFrame;
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

	public static final String APP_NAME = "GWRAS";

	public static final String APP_VERSION = "1.2";

	public static final String PHONE = "15680302675";

	public static final String HOME_PAGE = "";

	public static final String E_MAIL = "275551265@qq.com";

	public static final String AUTHOR = "竹 利";

	public static final String COPYRIGHT_INFO = "川东北强天气研究南充市重点实验室(C)2020-2026";

	public static RadarProcessor processor = new RadarProcessor();

	public static Radar radar;

	private static FilesystemAlterationMonitor fam;

	public static byte updateRate = 30; // ?????????D???????????????o??

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
		ConfigInfo.readConfigInfo();//?????????????t
		this.setTitle(APP_NAME);
		this.setJMenuBar(GUIManager.createMenu(processor));//??????2???????
		Container pane = this.getContentPane();//?????y?????Y????????
//		pane.add(GUIManager.createToolBar(processor), BorderLayout.NORTH);//?????Y??¨??????1???????
//		pane.add(GUIManager.createStatusBar(), BorderLayout.SOUTH);//?????Y??¨??????????????
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

	public static void main(String[] args) {
		// FlatLaf theme setup before any UI
		UIManager.put("FlatLaf.ownWindowDecorations", false);
		UIManager.put("FlatLaf.useWindowDecorations", false);

		File file = new File(CommonUtils.getUserHomeDir(), "output.log");
		try {
			PrintStream ps = new PrintStream(file);
			System.setErr(ps);
			System.setOut(ps);
			UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Accent color
		UIManager.put("Component.accentColor", 0x357EC0);
		UIManager.put("Component.focusColor", 0x357EC040);
		// Rounder corners
		UIManager.put("TabbedPane.tabHeight", 28);
		UIManager.put("Button.arc", 6);
		UIManager.put("Component.arc", 6);
		UIManager.put("ProgressBar.arc", 6);
		UIManager.put("TextComponent.arc", 6);
		UIManager.put("TabbedPane.selectedBackground", new Color(0xF5F5F5));
		// Menus
		UIManager.put("MenuBar.underlineSelectionColor", 0x357EC0);
		UIManager.put("Menu.selectionBackground", new Color(0xE8F0FE));
		UIManager.put("Menu.selectionForeground", 0x1A1A1A);
		UIManager.put("MenuItem.selectionBackground", new Color(0xE8F0FE));
		UIManager.put("MenuItem.selectionForeground", 0x1A1A1A);
		UIManager.put("PopupMenu.borderColor", new Color(0xD0D0D0));
		// Checkbox in menus
		UIManager.put("CheckBoxMenuItem.selectionBackground", new Color(0xE8F0FE));
		UIManager.put("CheckBoxMenuItem.icon.checkmarkColor", 0x357EC0);
		UIManager.put("CheckBoxMenuItem.acceleratorForeground", 0x808080);
		// Table headers
		UIManager.put("TableHeader.background", new Color(0xF0F0F0));

		// Unified font
		Font uiFont = new Font("Microsoft YaHei UI", Font.PLAIN, 13);
		Enumeration<?> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (UIManager.get(key) instanceof FontUIResource) {
				UIManager.put(key, uiFont);
			}
		}

		SwingUtilities.invokeLater(() -> {
			radar = new Radar();
			radar.setVisible(true);
		});
	}

}
