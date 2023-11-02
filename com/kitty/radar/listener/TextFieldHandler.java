package com.kitty.radar.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.kitty.radar.gui.PropsDialog;

public class TextFieldHandler implements DocumentListener {

    private PropsDialog dialog;

    public TextFieldHandler() {

    }

    public TextFieldHandler(PropsDialog dialog) {
        this.dialog = dialog;
    }

    public void changedUpdate(DocumentEvent e) {
        dialog.setApplyEnable(true);
    }

    public void insertUpdate(DocumentEvent e) {
        dialog.setApplyEnable(true);
    }

    public void removeUpdate(DocumentEvent e) {
        dialog.setApplyEnable(true);
    }

}
