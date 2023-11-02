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
 * 椭圆形区域handler。
 */
public class EllipseHandler extends AreaHandler {

    private JTextField lng;

    private JTextField lat;

    private JTextField width;

    private JTextField height;

    public EllipseHandler(AreaInputDialog dialog, ValidationGroup group, Area area) {
        this.dialog = dialog;
        this.group = group;
        dialog.setSize(310, 277);
        JLabel label = dialog.mainPanel.addLabel("中心经度", 40, 62);
        lng = dialog.mainPanel.addTextField(150, label);
        lng.setHorizontalAlignment(JTextField.RIGHT);
        dialog.mainPanel.addLabel("度", lng);
        label = dialog.mainPanel.addLabel("中心纬度", 40, 94);
        lat = dialog.mainPanel.addTextField(150, label);
        lat.setHorizontalAlignment(JTextField.RIGHT);
        dialog.mainPanel.addLabel("度", lat);
        label = dialog.mainPanel.addLabel("长轴", 40, 126);
        width = dialog.mainPanel.addTextField(150, label);
        width.setHorizontalAlignment(JTextField.RIGHT);
        Rectangle rect = width.getBounds();
        rect.x += 24;
        width.setBounds(rect);
        dialog.mainPanel.addLabel("km", width);
        label = dialog.mainPanel.addLabel("短轴", 40, 158);
        height = dialog.mainPanel.addTextField(150, label);
        height.setHorizontalAlignment(JTextField.RIGHT);
        rect = height.getBounds();
        rect.x += 24;
        height.setBounds(rect);
        if (area != null && area instanceof EllipseArea) {
            EllipseArea ra = (EllipseArea) area;
            lng.setText(CommonUtils.format(ra.getLx(), 6));
            lat.setText(CommonUtils.format(ra.getLy(), 6));
            width.setText(CommonUtils.format(ra.getWidth(), 1));
            height.setText(CommonUtils.format(ra.getHeight(), 1));
        }

        dialog.mainPanel.addLabel("km", height);
        group.setValidateAll(false);
        group.add(lng, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(lat, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(width, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(height, Validators.REQUIRE_NON_NEGATIVE_NUMBER, Validators.REQUIRE_VALID_NUMBER,
                Validators.REQUIRE_NON_EMPTY_STRING);
        lng.requestFocusInWindow();
    }

    public void process(ActionEvent e) {
    	RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
        Problem p = group.validateAll();
        if (p == null) {
            EllipseArea rect = new EllipseArea(Double.parseDouble(lng.getText()), Double
                    .parseDouble(lat.getText()), Double.parseDouble(width.getText()), Double
                    .parseDouble(height.getText()), radarBase);
            AreaDialog.tableModel.add(rect, dialog.getIndex());
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
