package com.kitty.radar.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;

import com.kitty.component.gui.BasicDialog;
import com.kitty.radar.Radar;
import com.kitty.radar.util.CommonUtils;

/**
 * 欢迎对话框。
 */
public class WelcomeDialog extends BasicDialog {

    public static boolean showWelcome = true;

    private JCheckBox cb;

    public WelcomeDialog() {
        super("欢迎使用" + Radar.APP_NAME, false);
        this.setCenterSize(400, 255);
        this.mainPanel.addImage("resource/about.gif", 0, 0, 400, 43);
        this.mainPanel.addLabel("软件版本: " + Radar.APP_VERSION, 23, 59);
        this.mainPanel.addLabel(Radar.COPYRIGHT_INFO, 104, 59);
        this.mainPanel.addLabel("软件设计: " + Radar.AUTHOR + "  " + Radar.PHONE + "  " + Radar.E_MAIL,
                23, 89);
        this.mainPanel.addLabel("特别感谢: 康岚  四川省气象台、王亚强  中国气象科学研究院", 23, 119);
        this.mainPanel.addLabel("", 23, 149);
        this.buttonPanel.remove(this.cancelButton);
        this.confirmButton.setText("    确定    ");
        cb = new JCheckBox("下次启动不再显示");
        this.buttonPanel.add(cb, 0);
        FlowLayout layout = (FlowLayout) this.buttonPanel.getLayout();
        layout.setAlignment(FlowLayout.LEFT);
        this.setButtonHgap(20);
        CommonUtils.addFirstFocus(this, confirmButton);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        showWelcome = !cb.isSelected();
        this.dispose();
    }

}
