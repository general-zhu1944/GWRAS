package com.kitty.radar.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.kitty.radar.gui.PropsDialog;

public class ItemHandler implements ItemListener {

    protected PropsDialog dialog;

    public ItemHandler(PropsDialog dialog) {
        this.dialog = dialog;
    }

    public void itemStateChanged(ItemEvent e) {
        dialog.setApplyEnable(true);        
    }

}
