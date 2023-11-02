package com.kitty.radar.gui;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.kitty.component.gui.BasicDialog;

public class ExportSetDialog extends BasicDialog {
	
	public static int coordType = 0;
	
	private JRadioButton b0;
	
	private JRadioButton b1;

    public ExportSetDialog() {
        super("导出格式设置", true);
        this.setCenterSize(300, 150);
        JLabel label = this.mainPanel.addLabel("格点坐标   ", 50, 32);
        ButtonGroup bgroup = new ButtonGroup();
        b0 = this.mainPanel.addRadioButton("方位", label);
        b0.setSelected(coordType == 0);
        bgroup.add(b0);
        b1 = this.mainPanel.addRadioButton("经纬度", b0);
        b1.setSelected(coordType == 1);
        bgroup.add(b1);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
    	if (b1.isSelected()) {
    		coordType = 1;
    	} else {
    		coordType = 0;
    	}
    	this.dispose();
    }

}
