package com.kitty.radar.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

//import com.kitty.radar.RHI;

public class RhiWindowHandler extends WindowAdapter {

    private JDialog dialog;

    public RhiWindowHandler(JDialog dialog) {
        this.dialog = dialog;
    }

    public void windowClosing(WindowEvent e) {
//        RHI.bounds = dialog.getBounds();
        dialog.dispose();
//        RHI.rhi = null;
    }

}
