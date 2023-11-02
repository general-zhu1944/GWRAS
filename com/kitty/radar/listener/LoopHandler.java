package com.kitty.radar.listener;

import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.kitty.radar.domain.ListElement;
import com.kitty.radar.gui.GUIManager;

public class LoopHandler extends TimerTask {

    private int index = 0;

    private Object[] values;

    public LoopHandler() {
        values = GUIManager.list.getSelectedValues();
    }

    public void run() {
        if (values != null && values.length > 0) {
            index--;
            if (index < 0) {
                index = values.length - 1;
            }
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        RadarProcessor.displayFile((ListElement) values[index]);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
