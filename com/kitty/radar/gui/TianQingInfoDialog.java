package com.kitty.radar.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

import com.kitty.component.gui.BasicPanel;
import com.kitty.component.gui.domain.Option;
import com.kitty.radar.RadarBase;
import com.kitty.radar.RadarParams;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.listener.ItemHandler;
import com.kitty.radar.listener.RadarProcessor;
import com.kitty.radar.listener.TextFieldHandler;
import com.kitty.radar.util.CommonUtils;

import com.kitty.component.gui.ImagePanel;
import com.kitty.radar.RadarParams;
import com.kitty.radar.listener.SelectDownloadDirHandler;
import com.kitty.radar.listener.TextFieldHandler;
import com.kitty.radar.util.CommonUtils;

public class TianQingInfoDialog extends PropsDialog {

    private JTextField textTimerRate;

    private JCheckBox enableDelete;

    private JTextField textDeleteDays;

    private JTextField userId;

    private JTextField pw;

    public static JTextField path;

    private JComboBox comboFormat;
    public static ImagePanel dirImage;


    public TianQingInfoDialog(Frame owner) {
        super(owner, "天擎配置", true);
        Dimension d = new Dimension(350, 243);
        this.setSize(d);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(CommonUtils.getCenterLocation(d));
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        this.add(tabbedPane, BorderLayout.CENTER);
        TextFieldHandler listener = new TextFieldHandler(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets.set(18, 5, 0, 5);
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label1 = new JLabel("账号：");
        JLabel label2 = new JLabel("密码：");
        JLabel label3 = new JLabel("存放路径：");
        userId = new JTextField(RadarParams.userId,20);
        d = userId.getMinimumSize();
        d.width = 126;
        userId.setMinimumSize(d);

        pw = new JTextField(RadarParams.pw,20);
        pw.setMinimumSize(d);
       // pw.setHorizontalAlignment(JTextField.RIGHT);

        path = new JTextField(RadarParams.radarSavePath, 20);
        path.setMinimumSize(d);
       // path.setHorizontalAlignment(JTextField.RIGHT);
        userId.getDocument().addDocumentListener(listener);
        pw.getDocument().addDocumentListener(listener);
        path.getDocument().addDocumentListener(listener);
        dirImage = new ImagePanel("resource/folder_page.gif");
        dirImage.setToolTipText("选择存储雷达基数据目录(" + RadarParams.radarSavePath + ")");
        dirImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dirImage.addMouseListener(new SelectDownloadDirHandler());

        
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(userId, gbc);
        gbc.insets.set(10, 5, 0, 5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(label2, gbc);
        gbc.gridy = 2;
        panel.add(label3, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(pw, gbc);
        gbc.gridy = 2;
        panel.add(path, gbc);
        gbc.gridx = 2;
        gbc.ipadx = 8;
        gbc.ipady = 7;
        panel.add(dirImage, gbc);
        
        
        gbc.gridx = 2;

        ValidationPanel vpanel = new ValidationPanel();
        ValidationGroup group = vpanel.getValidationGroup();
        group.add(userId, Validators.maxLength(100));
        group.add(pw, Validators.maxLength(100));
        group.add(path, Validators.maxLength(100));
        vpanel.setInnerComponent(panel);
        this.addValidationListener(vpanel, "1");
        tabbedPane.addTab(" 天擎账号和雷达基数据存储路径配置 ", vpanel);

        this.add(createPropsButtons(), BorderLayout.SOUTH);
    }

    public static void createSiteInfoDialog(JFrame owner) {
    	TianQingInfoDialog dialog = new TianQingInfoDialog(owner);
        dialog.setVisible(true);
    }

    public JTextField getTextTimerRate() {
        return textTimerRate;
    }

    public JTextField getTextuserId() {
        return userId;
    }

    public JTextField getTextpw() {
        return pw;
    }

    public JTextField getTextpath() {
        return path;
    }

    public JCheckBox getEnableDelete() {
        return enableDelete;
    }

    public JTextField getTextDeleteDays() {
        return textDeleteDays;
    }

    public JComboBox getComboFormat() {
        return comboFormat;
    }

}
