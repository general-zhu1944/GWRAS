package com.kitty.radar.listener;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class DisposeActionHandler extends AbstractAction {

    private JDialog dialog;

    public DisposeActionHandler(JDialog dialog) {
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent e) {
        dialog.dispose();
    }

}
