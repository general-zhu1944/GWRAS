package com.kitty.radar.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.kitty.component.gui.BasicPanel;
import com.kitty.component.gui.domain.Option;
//import com.kitty.radar.business.cr.CR;
//import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.listener.ItemHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.RadarUtils;

public class ResolutionDialog extends PropsDialog {

    private JComboBox comboCR;

    private JComboBox comboVIL;

    public ResolutionDialog(Frame owner) {
        super(owner, "分辨率", true);
        Dimension d = new Dimension(275, 210);
        this.setSize(d);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(CommonUtils.getCenterLocation(d));
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        BasicPanel panel = new BasicPanel();
        ItemHandler clistener = new ItemHandler(this);
        JLabel label = panel.addLabel("组合反射率", 35, 85);
        Option[] options = RadarUtils.getResolution(CommonProps.MOMENT_CR);
        this.comboCR = panel.addComboBox(options, label);
        label = panel.addLabel("默认分辨率", 35, 41);
        for (int i = 0; i < options.length; i++) {
//            if (options[i].getValue() == CR.getResolution()) {
//                comboCR.setSelectedIndex(i);
//                break;
//            }
        }
        comboCR.addItemListener(clistener);
        options = RadarUtils.getResolution(CommonProps.MOMENT_VIL);
        this.comboVIL = panel.addComboBox(options, label);
        for (int i = 0; i < options.length; i++) {
//            if (options[i].getValue() == VIL.getResolution()) {
//                comboVIL.setSelectedIndex(i);
//                break;
//            }
        }
        comboVIL.addItemListener(clistener);

        panel.setBorder(BorderFactory.createEtchedBorder());
        this.add(panel, BorderLayout.CENTER);
        this.add(createPropsButtons(), BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public JComboBox getComboCR() {
        return comboCR;
    }

    public void setComboCR(JComboBox comboCR) {
        this.comboCR = comboCR;
    }

    public JComboBox getComboVIL() {
        return comboVIL;
    }

    public void setComboVIL(JComboBox comboVIL) {
        this.comboVIL = comboVIL;
    }

}
