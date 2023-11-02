package com.kitty.radar.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.kitty.radar.Radar;

public class CommonUtils {

	public static String appPath;

	static {
		try {
			appPath = new File(".").getCanonicalPath() + File.separator;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getDirPath(String filePath) {
		if (filePath != null) {
			int index = filePath.lastIndexOf(File.separator);
			if (index != -1) {
				return filePath.substring(0, index + 1);
			} else {
				return "";
			}
		}
		return null;
	}

	public static String format(double value, int scale) {
		return new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP)
				.toString();
	}

	public static String defaultFormat(double value) {
		return format(value, "0.#");
	}

	public static String format(double value, String pattern) {
		return new DecimalFormat(pattern).format(value);
	}

	public static Point getCenterLocation(Dimension d) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension sd = toolkit.getScreenSize();
		Point p = new Point();
		p.setLocation((sd.width - d.width) / 2, (sd.height - d.height) / 2);
		return p;
	}

	public static Cursor createCustomCursor(String path, Point point,
			String name) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			return toolkit.createCustomCursor(getResImage(path), point, name);
		} catch (Exception e) {
			e.printStackTrace();
			return new Cursor(Cursor.DEFAULT_CURSOR);
		}
	}

	public static Image getResImage(String path) throws IOException {
		return ImageIO.read(new FileCacheImageInputStream(Radar.class
				.getResourceAsStream(path), null));
	}

	public static void addEscAction(JComponent comp, Action listener) {
		comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "dispose");
		comp.getActionMap().put("dispose", listener);
	}

	public static void addEscDispose(final JDialog d) {
		addEscAction((JComponent) d.getContentPane(), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
		});
	}

	public static void addFirstFocus(Window w, final JComponent comp) {
		w.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				comp.requestFocusInWindow();
			}
		});
	}

	public static JDialog getParentDialog(Component comp) {
		while (comp != null) {
			if (comp instanceof JDialog) {
				return (JDialog) comp;
			}
			comp = comp.getParent();
		}
		return null;
	}

	public static void alert(String msg, Component focusComp) {
		alert(msg, focusComp, Radar.radar);
	}

	public static void alert(String msg, Component focusComp,
			Component parentComp) {
		JOptionPane.showMessageDialog(parentComp, msg, "",
				JOptionPane.WARNING_MESSAGE);
		if (focusComp != null) {
			if (focusComp instanceof JTextField) {
				((JTextField) focusComp).selectAll();
			}
			focusComp.requestFocusInWindow();
		}
	}

	public static void showWait() {
		Radar.radar.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public static void hideWait() {
		Radar.radar.setCursor(Cursor.getDefaultCursor());
	}

	// ========================= System Methods ==========================

	public static String getSystemDir() {
		String dir = (String) System.getenv("windir");
		if (dir == null) {
			dir = (String) System.getenv("SystemRoot");
		}
		return dir;
	}

	public static String getAppDataDir() {
		return (String) System.getenv("APPDATA");
	}

	public static String getUserHomeDir() {
		return System.getProperty("user.home");
	}

	public static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

}
