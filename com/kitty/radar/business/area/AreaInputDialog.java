package com.kitty.radar.business.area;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.netbeans.validation.api.ui.ValidationPanel;

import com.kitty.component.gui.BasicDialog;
import com.kitty.radar.util.CommonUtils;

/**
 * 新建/修改区域对话框。
 */
public class AreaInputDialog extends BasicDialog {

    public static final String AREA_CIRCLE = "圆形";

    public static final String AREA_RECTANGLE = "矩形";

    public static final String AREA_ELLIPSE = "椭圆形";

    private JComboBox type;

    private JLabel typeLabel;

    private ValidationPanel vpanel;

    private AreaHandler handler;

    private int index;

    public AreaInputDialog(Window owner, String title, int index) {
        super(owner, title, true);
        this.index = index;
        final Area area = AreaDialog.tableModel.get(index);
        typeLabel = this.mainPanel.addLabel("类别", 40, 30);
        type = this.mainPanel.addComboBox(
                new String[] { AREA_CIRCLE, AREA_RECTANGLE }, typeLabel);
        Rectangle rect = type.getBounds();
        rect.x += 24;
        type.setBounds(rect);
        type.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                createPanel((String) cb.getSelectedItem(), area, false);
            }
        });
        String typeName = null;
        if (area != null) {
            typeName = area.getAreaName();
            type.setSelectedItem(typeName);
        }
        createPanel(typeName, area, true);
        CommonUtils.addFirstFocus(this, handler.getFocusComponent());
        this.setVisible(true);
    }

    private void createPanel(String typeName, Area area, boolean init) {
        if (vpanel == null) {
            vpanel = new ValidationPanel();
            vpanel.setBorder(mainPanel.getBorder());
            mainPanel.setBorder(null);
            this.remove(mainPanel);
        } else {
            Border border = vpanel.getBorder();
            this.remove(vpanel);
            vpanel = new ValidationPanel();
            vpanel.setBorder(border);
        }
        vpanel.setInnerComponent(mainPanel);
        Component[] comps = mainPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] != type && comps[i] != typeLabel) {
                mainPanel.remove(comps[i]);
            }
        }
        this.add(vpanel, BorderLayout.CENTER);
        if (AREA_RECTANGLE.equals(typeName)) {
            handler = new RectangleHandler(this, vpanel.getValidationGroup(), area);
        } else if (AREA_ELLIPSE.equals(typeName)) {
            handler = new EllipseHandler(this, vpanel.getValidationGroup(), area);
        } else {
            handler = new CircleHandler(this, vpanel.getValidationGroup(), area);
        }
        if (init) {
            this.setLocation(CommonUtils.getCenterLocation(this.getSize()));
        } else {
            vpanel.updateUI();
        }
    }

    public void actionPerformed(ActionEvent e) {
        handler.process(e);
    }

    public int getIndex() {
        return index;
    }

}
