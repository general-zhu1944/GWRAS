package com.kitty.radar.listener;

import com.kitty.radar.VCS;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

//import com.kitty.radar.RHI;

public class VcsWindowHandler extends WindowAdapter {

    private JDialog dialog;

    public VcsWindowHandler(JDialog dialog) {

        this.dialog = dialog;
    }

    public void windowClosing(WindowEvent e) {
//        RHI.bounds = dialog.getBounds();
        dialog.dispose();
        VCS.vcsDialogs.remove(dialog);

//        RHI.rhi = null;
    }

}
