package com.kitty.radar.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

import com.kitty.radar.MapOverlay;
import com.kitty.radar.RadarParams;
import com.kitty.radar.listener.ItemHandler;
import com.kitty.radar.listener.TextFieldHandler;
import com.kitty.radar.util.CommonUtils;

public class BackgroundDialog extends PropsDialog {

    private JTextField textSpoke;
    private JTextField textRing;
    private JCheckBox checkProvince;
    private JCheckBox checkCity;
    private JCheckBox checkTown;
    private JCheckBox checkRiver;
    private JCheckBox checkTownName;
    private JCheckBox checkDetailName;
    private JTextField textMapDir;
    private JTextField textTerrainFile;

    public BackgroundDialog(Frame owner) {
        super(owner, "地理背景", true);
        Dimension d = new Dimension(380, 300);
        this.setSize(d);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(CommonUtils.getCenterLocation(d));
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        this.add(tabbedPane, BorderLayout.CENTER);
        TextFieldHandler listener = new TextFieldHandler(this);

        // --- Grid tab ---
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label1 = new JLabel("辐条角度");
        JLabel label2 = new JLabel("度");
        JLabel label3 = new JLabel("同心圆距");
        JLabel label4 = new JLabel("km");
        textSpoke = new JTextField(CommonUtils.defaultFormat(MapOverlay.polar_grid_spoke), 18);
        textRing = new JTextField(CommonUtils.defaultFormat(MapOverlay.polar_grid_ring), 18);
        d = textSpoke.getMinimumSize();
        d.width = 126;
        textSpoke.setMinimumSize(d);
        textRing.setMinimumSize(d);
        textSpoke.setHorizontalAlignment(JTextField.RIGHT);
        textRing.setHorizontalAlignment(JTextField.RIGHT);
        textSpoke.getDocument().addDocumentListener(listener);
        textRing.getDocument().addDocumentListener(listener);
        gbc.insets.set(18, 5, 0, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label1, gbc);
        gbc.gridx = 1;
        panel.add(textSpoke, gbc);
        gbc.gridx = 2;
        panel.add(label2, gbc);
        gbc.insets.set(25, 5, 0, 5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label3, gbc);
        gbc.gridx = 1;
        panel.add(textRing, gbc);
        gbc.gridx = 2;
        panel.add(label4, gbc);
        ValidationPanel vpanel = new ValidationPanel();
        ValidationGroup group = vpanel.getValidationGroup();
        group.add(textSpoke, Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.REQUIRE_VALID_NUMBER, Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(textRing, Validators.REQUIRE_NON_NEGATIVE_NUMBER,
                Validators.REQUIRE_VALID_NUMBER, Validators.REQUIRE_NON_EMPTY_STRING);
        vpanel.setInnerComponent(panel);
        this.addValidationListener(vpanel, "1");
        tabbedPane.addTab(" 网格 ", vpanel);

        // --- Map tab ---
        panel = new JPanel(new GridBagLayout());
        ItemHandler clistener = new ItemHandler(this);
        checkProvince = new JCheckBox(" 省界", (MapOverlay.mapMode & MapOverlay.MAP_PROVINCE) != 0);
        checkCity = new JCheckBox(" 地区边界", (MapOverlay.mapMode & MapOverlay.MAP_CITY) != 0);
        checkTown = new JCheckBox(" 县界", (MapOverlay.mapMode & MapOverlay.MAP_TOWN) != 0);
        checkRiver = new JCheckBox(" 河流", (MapOverlay.mapMode & MapOverlay.MAP_RIVER) != 0);
        checkTownName = new JCheckBox(" 省/市名", (MapOverlay.mapMode & MapOverlay.MAP_TOWNNAME) != 0);
        checkDetailName = new JCheckBox(" 县名",
                (MapOverlay.mapMode & MapOverlay.MAP_DETAILNAME) != 0);
        checkProvince.addItemListener(clistener);
        checkCity.addItemListener(clistener);
        checkTown.addItemListener(clistener);
        checkRiver.addItemListener(clistener);
        checkTownName.addItemListener(clistener);
        checkDetailName.addItemListener(clistener);
        gbc.insets.set(5, 15, 5, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(checkProvince, gbc);
        gbc.gridy = 1;
        panel.add(checkCity, gbc);
        gbc.gridy = 2;
        panel.add(checkTown, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(checkRiver, gbc);
        gbc.gridy = 1;
        panel.add(checkTownName, gbc);
        gbc.gridy = 2;
        panel.add(checkDetailName, gbc);

        // Map directory field
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel mapDirLabel = new JLabel("地图目录:");
        textMapDir = new JTextField(RadarParams.mapDataDir, 36);
        textMapDir.getDocument().addDocumentListener(listener);
        gbc.insets.set(10, 10, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(mapDirLabel, gbc);
        gbc.insets.set(2, 10, 10, 5);
        gbc.gridy = 4;
        panel.add(textMapDir, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        tabbedPane.addTab(" 地图 ", panel);

        // --- Terrain tab ---
        JPanel terrainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.weightx = 0;
        tgbc.weighty = 0;
        tgbc.anchor = GridBagConstraints.WEST;
        JLabel terrainLabel = new JLabel("地形高程文件 (.nc):");
        textTerrainFile = new JTextField(MapOverlay.elevationFile, 40);
        textTerrainFile.getDocument().addDocumentListener(listener);
        tgbc.fill = GridBagConstraints.HORIZONTAL;
        tgbc.weightx = 1.0;
        tgbc.insets.set(15, 15, 5, 15);
        tgbc.gridx = 0;
        tgbc.gridy = 0;
        terrainPanel.add(terrainLabel, tgbc);
        tgbc.insets.set(5, 15, 15, 15);
        tgbc.gridy = 1;
        terrainPanel.add(textTerrainFile, tgbc);
        tabbedPane.addTab(" 地形 ", terrainPanel);

        this.add(createPropsButtons(), BorderLayout.SOUTH);
    }

    public static void createBackgroundDialog(JFrame owner) {
        BackgroundDialog dialog = new BackgroundDialog(owner);
        dialog.setVisible(true);
    }

    public JTextField getTextRing() { return textRing; }
    public JTextField getTextSpoke() { return textSpoke; }
    public JCheckBox getCheckCity() { return checkCity; }
    public JCheckBox getCheckProvince() { return checkProvince; }
    public JCheckBox getCheckRiver() { return checkRiver; }
    public JCheckBox getCheckTown() { return checkTown; }
    public JCheckBox getCheckTownName() { return checkTownName; }
    public JCheckBox getCheckDetailName() { return checkDetailName; }
    public JTextField getTextMapDir() { return textMapDir; }
    public JTextField getTextTerrainFile() { return textTerrainFile; }
}
