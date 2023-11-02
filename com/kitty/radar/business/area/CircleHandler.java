package com.kitty.radar.business.area;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;

import com.kitty.radar.MapOverlay;
import com.kitty.radar.RadarBase;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonUtils;

/**
 * 圆形区域handler。
 */
public class CircleHandler extends AreaHandler {

    private JTextField lng;

    private JTextField lat;

    private JTextField r;

    public CircleHandler(AreaInputDialog dialog, ValidationGroup group, Area area) {
        this.dialog = dialog;
        this.group = group;
        dialog.setSize(310, 245);
        JLabel label = dialog.mainPanel.addLabel("中心经度", 40, 62);
        lng = dialog.mainPanel.addTextField(150, label);
        lng.setHorizontalAlignment(JTextField.RIGHT);
        dialog.mainPanel.addLabel("度", lng);
        label = dialog.mainPanel.addLabel("中心纬度", 40, 94);
        lat = dialog.mainPanel.addTextField(150, label);
        lat.setHorizontalAlignment(JTextField.RIGHT);
        dialog.mainPanel.addLabel("度", lat);
        label = dialog.mainPanel.addLabel("半径", 40, 126);
        r = dialog.mainPanel.addTextField(150, label);
        r.setHorizontalAlignment(JTextField.RIGHT);
        Rectangle rect = r.getBounds();
        rect.x += 24;
        r.setBounds(rect);
        dialog.mainPanel.addLabel("km", r);
        if (area != null && area instanceof CircleArea) {
            CircleArea ca = (CircleArea) area;
            lng.setText(CommonUtils.format(ca.getLx(), 6));
            lat.setText(CommonUtils.format(ca.getLy(), 6));
            r.setText(CommonUtils.format(ca.getR(), 1));
        }

        group.setValidateAll(false);
        group.add(lng, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(lat, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(r, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        lng.requestFocusInWindow();
    }

    public void process(ActionEvent e) {
    	RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
        Problem p = group.validateAll();
        if (p == null) {
            CircleArea circle = new CircleArea(Double.parseDouble(lng.getText()), Double
                    .parseDouble(lat.getText()), Double.parseDouble(r.getText()), radarBase);
            AreaDialog.tableModel.add(circle, dialog.getIndex());
            dialog.dispose();
            AreaDialog.tableModel.fireTableDataChanged();
//            MapOverlay.update = true;
            GUIManager.activeMainPanel.getMap().update = true;
            GUIManager.repaintCurrent();
        }
    }

    public JComponent getFocusComponent() {
        return lng;
    }

}
