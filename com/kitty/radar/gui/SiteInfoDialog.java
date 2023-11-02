package com.kitty.radar.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

public class SiteInfoDialog extends PropsDialog {

    private JTextField textTimerRate;

    private JCheckBox enableDelete;

    private JTextField textDeleteDays;

    private JTextField textRadarName;

    private JTextField textLongitude;

    private JTextField textLatitude;

    private JComboBox comboFormat;

    public SiteInfoDialog(Frame owner) {
        super(owner, "站点信息", true);
        Dimension d = new Dimension(300, 243);
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
        JLabel label1 = new JLabel("雷达名称");
        JLabel label2 = new JLabel("经度");
        JLabel label3 = new JLabel("纬度");
        JLabel label4 = new JLabel("度");
        JLabel label5 = new JLabel("度");
        textRadarName = new JTextField(RadarBase.radarName, 20);
        d = textRadarName.getMinimumSize();
        d.width = 126;
        textRadarName.setMinimumSize(d);
        BigDecimal bd = new BigDecimal(RadarBase.longitude);
        bd = bd.setScale(6,RoundingMode.HALF_UP);
        textLongitude = new JTextField(bd.toString(), 20);
        textLongitude.setMinimumSize(d);
        textLongitude.setHorizontalAlignment(JTextField.RIGHT);
        bd = new BigDecimal(RadarBase.latitude);
        bd = bd.setScale(6, RoundingMode.HALF_UP);
        textLatitude = new JTextField(bd.toString(), 20);
        textLatitude.setMinimumSize(d);
        textLatitude.setHorizontalAlignment(JTextField.RIGHT);
        textRadarName.getDocument().addDocumentListener(listener);
        textLongitude.getDocument().addDocumentListener(listener);
        textLatitude.getDocument().addDocumentListener(listener);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(textRadarName, gbc);
        gbc.insets.set(10, 5, 0, 5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(label2, gbc);
        gbc.gridy = 2;
        panel.add(label3, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(textLongitude, gbc);
        gbc.gridy = 2;
        panel.add(textLatitude, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(label4, gbc);
        gbc.gridy = 2;
        panel.add(label5, gbc);
        ValidationPanel vpanel = new ValidationPanel();
        ValidationGroup group = vpanel.getValidationGroup();
        group.add(textRadarName, Validators.maxLength(1000));
        group.add(textLongitude, Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.REQUIRE_VALID_NUMBER, Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(textLatitude, Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.REQUIRE_VALID_NUMBER, Validators.REQUIRE_NON_EMPTY_STRING);
        vpanel.setInnerComponent(panel);
        this.addValidationListener(vpanel, "1");
        tabbedPane.addTab(" 雷达 ", vpanel);

        panel = new JPanel(null);
        label1 = new JLabel("数据格式");
        panel.add(label1);
        label1.setBounds(25, 18, 50, 30);
        ItemHandler clistener = new ItemHandler(this);
        Option[] options = new Option[] { RadarData.RADAR_FORMAT_FMT, RadarData.RADAR_FORMAT_SC,
        		RadarData.RADAR_FORMAT_SA_SB };
        comboFormat = new JComboBox(options);
        for (int i = 0; i < options.length; i++) {
            if (options[i].getValue() == RadarBase.radarFormat) {
                comboFormat.setSelectedIndex(i);
                break;
            }
        }
        comboFormat.addItemListener(clistener);
        panel.add(comboFormat);
        comboFormat.setBounds(100, 22, 139, BasicPanel.TEXT_FIELD_HEIGHT);
        label1 = new JLabel("循环间隔");
        label2 = new JLabel("秒");
        textTimerRate = new JTextField(CommonUtils.defaultFormat(RadarProcessor.timerRate), 15);
        textTimerRate.setHorizontalAlignment(JTextField.RIGHT);
        textTimerRate.getDocument().addDocumentListener(listener);
        panel.add(label1);
        label1.setBounds(25, 51, 50, 30);
        panel.add(textTimerRate);
        textTimerRate.setBounds(100, 55, 96, 22);
        panel.add(label2);
        label2.setBounds(207, 51, 50, 30);
        enableDelete = new JCheckBox("自动删除", RadarParams.enableDelete);
        enableDelete.addItemListener(new ItemHandler(this) {
            public void itemStateChanged(ItemEvent e) {
                dialog.setApplyEnable(true);
                textDeleteDays.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        panel.add(enableDelete);
        enableDelete.setBounds(25, 84, 70, 30);
        textDeleteDays = new JTextField(String.valueOf(RadarParams.deleteDays), 15);
        textDeleteDays.setHorizontalAlignment(JTextField.RIGHT);
        textDeleteDays.getDocument().addDocumentListener(listener);
        textDeleteDays.setEnabled(RadarParams.enableDelete);
        panel.add(textDeleteDays);
        textDeleteDays.setBounds(100, 88, 96, 22);
        label1 = new JLabel("天前的文件");
        panel.add(label1);
        label1.setBounds(207, 84, 70, 30);
        vpanel = new ValidationPanel();
        group = vpanel.getValidationGroup();
        group.add(textTimerRate, Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.REQUIRE_VALID_NUMBER, Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(textDeleteDays, Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.REQUIRE_VALID_INTEGER, Validators.REQUIRE_NON_EMPTY_STRING);
        vpanel.setInnerComponent(panel);
        this.addValidationListener(vpanel, "2");
        tabbedPane.addTab(" 数据 ", vpanel);

        this.add(createPropsButtons(), BorderLayout.SOUTH);
    }

    public static void createSiteInfoDialog(JFrame owner) {
        SiteInfoDialog dialog = new SiteInfoDialog(owner);
        dialog.setVisible(true);
    }

    public JTextField getTextTimerRate() {
        return textTimerRate;
    }

    public JTextField getTextLatitude() {
        return textLatitude;
    }

    public JTextField getTextLongitude() {
        return textLongitude;
    }

    public JTextField getTextRadarName() {
        return textRadarName;
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
