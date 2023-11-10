package com.kitty.radar.listener;

import com.kitty.radar.gui.GUIManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class ListSelectHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            RadarProcessor.displayFile(null);

            GUIManager.createCutButtons();
            Component[] comps = GUIManager.cutPanel.getComponents();
            int cut = GUIManager.activeMainPanel.getRadarBase().cutNum;
            for (int i = 0; i < comps.length; i++) {
                Component comp = comps[i];
                if(comp instanceof JRadioButton) {
                    int btnCut = Integer.parseInt(((JRadioButton) comp).getActionCommand());
                    if(btnCut == cut) {
                        ((JRadioButton) comp).setSelected(true);
                    } else {
                        ((JRadioButton) comp).setSelected(false);
                    }
                }
            }
        }
    }

}
