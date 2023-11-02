package com.kitty.radar.listener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListSelectHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            RadarProcessor.displayFile(null);
        }
    }

}
