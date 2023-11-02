package com.kitty.radar.listener;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.gui.WelcomeDialog;

public class BasicWindowHandler extends WindowAdapter {

    public void windowClosing(WindowEvent e) {
        RadarProcessor.exitApp();
    }

    public void windowOpened(WindowEvent e) {
        GUIManager.list.requestFocusInWindow();
        e.getWindow().setMinimumSize(new Dimension(720, 540));
        if (WelcomeDialog.showWelcome) {
            new WelcomeDialog();
        }
        setWindowSize();
    }
    
	private void setWindowSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension d = Radar.radar.getSize();
		d.height -= 20;
		int w = GUIManager.activeMainPanel.getWidth() - RadarBase.getColorWidth();
		int h = GUIManager.activeMainPanel.getHeight() - 20;
		if (w > h) {
			d.width -= w - h;
			GUIManager.activeMainPanel.initSize = new Dimension(h + RadarBase.getColorWidth(), h);
		} else if (h > w) {
			d.height -= h - w;
			GUIManager.activeMainPanel.initSize = new Dimension(w + RadarBase.getColorWidth(), w);
		}
		Radar.radar.setSize(d);
		Radar.radar.setLocation((screenSize.width - d.width) / 2, 0);
	}

}
