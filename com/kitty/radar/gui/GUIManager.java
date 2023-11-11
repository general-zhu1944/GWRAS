package com.kitty.radar.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.kitty.component.gui.BasicDrawPanel;
import com.kitty.component.gui.ImagePanel;
import com.kitty.component.gui.ScrollLabel;
import com.kitty.component.gui.domain.LabelOption;
import com.kitty.component.third.TitledBorderExt;
import com.kitty.radar.*;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.ListElement;
import com.kitty.radar.listener.ListSelectHandler;
import com.kitty.radar.listener.MouseHandler;
import com.kitty.radar.listener.SelectDirHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.RadarUtils;

public class GUIManager {
    public static JButton activeCutButton;

    public static JButton activeToolButton;
    public static Cursor currentToolCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    public static JButton toolGrid;

    public static JButton toolMap;

    public static JButton toolCross;

    public static JRadioButton cappiButton = new JRadioButton("高度", RadarBase.view == CommonProps.VIEW_CAPPI);;

    public static JTextField cappiText = new JTextField(CommonUtils.defaultFormat(RadarBase.level), 11);;

    public static JPanel cutPanel;

    public static TitledBorderExt vcpBorder;

 //   public static JLabel toolBarLabel;

//    public static JLabel statusCenter;

//    public static MainPanel mainPanel;
    //当前被激活的MainPanel
    public static MainPanel activeMainPanel;
    //工具箱同步是否选中
    public static boolean syncTool = false;
    //同步时间(文件)
    public static boolean syncTime = false;
    //同步层次
    public static boolean syncCut = false;
    public static List<JLayer<MainPanel>> jlayers = new LinkedList<JLayer<MainPanel>>();

    public static boolean isSyncTool() {
        return syncTool;
    }
    //设置按钮状态
/*	public static void setSyncTool(boolean syncTool) {
		GUIManager.syncTool = syncTool;
		GUIManager.itemSyncTool.setState(syncTool);
	}*/

    public static boolean isSyncTime() {
        return syncTime;
    }

/*    public static void setSyncTime(boolean syncTime) {
        GUIManager.syncTime = syncTime;
        GUIManager.itemSyncTime.setState(syncTime);
    }*/

    public static boolean isSyncCut() {
        return syncCut;
    }

/*	public static void setSyncCut(boolean syncCut) {
		GUIManager.itemSyncCut.setState(syncCut);
		GUIManager.syncCut = syncCut;
	}*/
    //全部MainPanel
    private static List<MainPanel> jpanels = new ArrayList<MainPanel>();
    public static List<MainPanel> getJpanels() {
        return jpanels;
    }

    public static void setJpanels(List<MainPanel> jpanels) {
        GUIManager.jpanels = jpanels;
    }

    //重绘全部MainPanel
    public static void repaintAll() {
        for (MainPanel jPanel : jpanels) {
            jPanel.getMap().update = true;
            jPanel.repaint();
        }
    }
    //重绘当前MainPanel
    public static void repaintCurrent() {
        activeMainPanel.repaint();
    }
    //右侧新增的选择变量Panel
    private static JPanel varPanel;
    public static JPanel getVarPanel() {
        return varPanel;
    }
    public static void setVarPanel(JPanel varPanel) {
        GUIManager.varPanel = varPanel;
    }

    public static ImagePanel dirImage;

    public static JList list;

    public static JButton previous;

    public static JButton next;

    public static JButton loop;

//    public static JToolBar toolBar;

//    public static JPanel statusBar;

    public static JPanel rightPanel;

    public static JCheckBoxMenuItem menuUpdate;

    public static JCheckBoxMenuItem menuGrid;

    public static JCheckBoxMenuItem menuMap;

    private static Map compMap = new HashMap();

    public static JPanel mainPanelContainer = new JPanel(new GridLayout(2,2));

    private static JCheckBoxMenuItem itemSyncTool;

    private static JCheckBoxMenuItem itemSyncTime;

    private static JCheckBoxMenuItem itemSyncCut;
    public static void reCreateMainPanel(int num) {
        int rows = 2;
        int cols = 2;
        switch(num) {
            case 1:
                rows = 1;
                cols = 1;
                break;
            case 2:
                rows = 1;
                cols = 2;
                break;
            case 4:
            default:
                rows = 2;
                cols = 2;
                break;

        }
        createMainPanel(rows, cols, mainPanelContainer);
        SwingUtilities.updateComponentTreeUI(Radar.radar);//方法立刻更新应用
    }

//构建系统主界面
    public static void createPanels(ActionListener processor, Container pane) {
        mainPanelContainer = new JPanel();
        mainPanelContainer.setBackground(Color.BLACK);
        mainPanelContainer.setBorder(BorderFactory.createLineBorder(Color.gray));
        pane.add(mainPanelContainer, BorderLayout.CENTER);
        //初始化主要显示区
        createMainPanel(1, 1, mainPanelContainer);
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;// 当窗口放大时，长度不变
        gbc.weighty = 0; // 当窗口放大时，高度不变
        gbc.fill = GridBagConstraints.HORIZONTAL; // 当格子有剩余空间时，水平填充空间

        // 初始化Cut按钮区
        cutPanel = new JPanel(gbl);
        vcpBorder = new TitledBorderExt(BorderFactory.createEtchedBorder(), "VCP" + activeMainPanel.getRadarBase().vcp);
//        cutPanel.setBorder(BorderFactory.createCompoundBorder(vcpBorder, BorderFactory
//                .createEmptyBorder(0, 0, 5, 0)));
        cutPanel.setBorder(BorderFactory.createTitledBorder("VCP" + activeMainPanel.getRadarBase().vcp));

        // 初始化文件列表区
        JPanel filePanel = new JPanel(gbl);
        JLabel llist = new JLabel("文件列表");
        llist.setForeground(vcpBorder.getTitleColor());
        gbc.insets.set(0, 0, 5, 0);// 组件彼此的间距
        gbc.gridx = 0;//设置组件所处行与列的起始坐标。例如gridx=0,gridy=0表示将组件放置在0行0列单元格内。
        gbc.gridy = 0;
        gbc.gridwidth = 2;//设置组件横向与纵向的单元格跨越个数。
        filePanel.add(llist, gbc);
        dirImage = new ImagePanel("resource/folder_page.gif");
        setDirToolTip(RadarParams.filePath);
        dirImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dirImage.addMouseListener(new SelectDirHandler());
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.ipadx = 6;
        gbc.ipady = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;//设置组件在单元格中的对齐方式
        filePanel.add(dirImage, gbc);
        list = new JList(new DefaultListModel());
        list.addListSelectionListener(new ListSelectHandler());
        JScrollPane spane = new JScrollPane(list);
        Dimension d = spane.getPreferredSize();
        d.width = 150;
        spane.setPreferredSize(d);
        gbc.weightx = 100;
        gbc.weighty = 100;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        filePanel.add(spane, gbc);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        previous = new JButton("上一个");
        next = new JButton("下一个");
        loop = new JButton("循环动画");
        previous.setActionCommand(CommonProps.AC_PREVIOUS_FILE);
        next.setActionCommand(CommonProps.AC_NEXT_FILE);
        loop.setActionCommand(CommonProps.AC_LOOP_FILE);
        previous.addActionListener(processor);
        next.addActionListener(processor);
        loop.addActionListener(processor);
        gbc.insets.set(0, 0, 0, 0);
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        filePanel.add(previous, gbc);
        gbc.insets.set(0, 8, 0, 0);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        filePanel.add(loop, gbc);
        gbc.insets.set(0, 0, 0, 0);
        gbc.gridx = 2;
        filePanel.add(next, gbc);

        rightPanel = new JPanel(gbl);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
        pane.add(rightPanel, BorderLayout.EAST);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 1;
        gbc.insets.set(0, 0, 5, 0);
        rightPanel.add(createToolBar(gbl, processor),gbc);

        gbc.gridy +=1;
        rightPanel.add(createNumPanel(gbl), gbc);

        gbc.gridy +=1;
        rightPanel.add(cutPanel, gbc);

        gbc.gridy +=1;
        varPanel = createMomentPanel(gbl);
        rightPanel.add(varPanel, gbc);

        gbc.gridy +=1;
        gbc.weightx = 100;
        gbc.weighty = 100;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.set(0, 0, 0, 0);
        rightPanel.add(filePanel, gbc);
        rightPanel.setVisible(Radar.showRightPanel);

        createCutButtons();
    }
    private static Component createNumPanel(GridBagLayout gbl) {
        JPanel numPanel = new JPanel(gbl);
        numPanel.setBorder(BorderFactory.createTitledBorder("视图窗口数量"));
        ActionListener listener = new ActionListener() {

            private int curNum = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                int setNum = 0;
                String  command = e.getActionCommand();
                if ("MAINPANEL_1".equals(command)) {
                    setNum = 1;
                } else if ("MAINPANEL_2".equals(command)) {
                    setNum = 2;
                } else if ("MAINPANEL_4".equals(command)) {
                    setNum = 4;
                } else {

                }
                if(setNum == curNum)
                    return;
                // JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+ curNum +setNum);
                curNum = setNum;
                GUIManager.reCreateMainPanel(curNum);
                if (VCS.vcsDialogs.size()>0)
                {
                    VCS.createVcsDialog(Radar.radar);
                }
                GUIManager.mainPanelContainer.repaint();
                //CommonUtils.alert("请先选择雷达基数据文件！",Radar.radar);

            }
        };

        ButtonGroup numGrp = new ButtonGroup();
        JRadioButton item = new JRadioButton("1  ");
        item.setActionCommand("MAINPANEL_1");
        item.addActionListener(listener);
        numGrp.add(item);
        numPanel.add(item);


        JRadioButton item2 = new JRadioButton("2  ");
        item2.setActionCommand("MAINPANEL_2");
        item2.addActionListener(listener);
        numGrp.add(item2);
        numPanel.add(item2);

        JRadioButton item4 = new JRadioButton("4  ");
        item4.setActionCommand("MAINPANEL_4");
        item4.addActionListener(listener);
        numGrp.add(item4);
        numPanel.add(item4);
        if(jpanels.size() == 1) {
            item.setSelected(true);
        }
        return numPanel;
    }
    private static void createMainPanel(int rows, int cols, JPanel mainPanelContainer) {
        jpanels.clear();
        jlayers.clear();
        if (RHI.rhiDialogs.size()>0)
        {
            RHI.rhiDialogs.forEach( p -> {
                p.removeAll();
                p.dispose();
        });
        }
        mainPanelContainer.removeAll();
        mainPanelContainer.setLayout(new GridLayout(rows,cols));
        int num = rows*cols;
        int[] activeMoment = new int[] {
                RadarData.DBZ,
                RadarData.V,
                RadarData.W,
                RadarData.DBZ,
        };
        int[] currentMoment = new int[] {
                CommonProps.MOMENT_R,
                CommonProps.MOMENT_V,
                CommonProps.MOMENT_W,
                CommonProps.MOMENT_VIL,
        };
        for(int i=0; i<num; i++) {
            MainPanel  mainPanel = new MainPanel(activeMoment[i], currentMoment[i]);
            JMainPanelLayerUi layerUI  = new JMainPanelLayerUi(mainPanel.getRadarBase());
            JLayer<MainPanel> jlayer = new JLayer<MainPanel>(mainPanel, layerUI);
            jlayers.add(jlayer);
            mainPanelContainer.add(jlayer);
            if( activeMainPanel!=null) {
                RadarBase base = activeMainPanel.getRadarBase();
                //JOptionPane.showMessageDialog(null, "消息提示tjjjjt：");
                RadarBase base2 = mainPanel.getRadarBase();
                ListElement elem = (ListElement) GUIManager.list.getSelectedValue();
                if (null == elem) {
                    base2.l2 = null;
                    jpanels.add(mainPanel);
                    mainPanel.setDoubleBuffered(false);
                    MouseHandler handler = new MouseHandler();
                    mainPanel.addMouseListener(handler);
                    mainPanel.addMouseMotionListener(handler);
                    mainPanel.addMouseWheelListener(handler);
                    mainPanel.setCursor(GUIManager.currentToolCursor);
                    continue;
                }
                base2.l2 = RadarUtils.createRadarData();//实例化l2为哪类雷达基数据sc?FMT?SA
                if (elem != null) {
                    base2.l2.setSrcFileName(elem.getLabel());
                    File file = new File(RadarParams.filePath, elem.getLabel());
                    base2.l2.open(file);
                }

                base2.vcp = base.l2.vcp;
                base2.resolution = base.l2.resolution;
                mainPanel.update = true;
                mainPanel.repaint();
            }

            jpanels.add(mainPanel);
            mainPanel.setDoubleBuffered(false);
            MouseHandler handler = new MouseHandler();
            mainPanel.addMouseListener(handler);
            mainPanel.addMouseMotionListener(handler);
            mainPanel.addMouseWheelListener(handler);
            mainPanel.setCursor(GUIManager.currentToolCursor);
        }
        activeMainPanel = jpanels.get(0);
    }
    private static class MomentActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            RadarBase radarBase = activeMainPanel.getRadarBase();
            String  command = e.getActionCommand();
            if (CommonProps.AC_REFLECTIVITY.equals(command)) {
                radarBase.active_moment = RadarData.DBZ;
                basicMoment(CommonProps.MOMENT_R);
            } else if (CommonProps.AC_VELOCITY.equals(command)) {
                radarBase.active_moment = RadarData.V;
                basicMoment(CommonProps.MOMENT_V);
            } else if (CommonProps.AC_SPECTRUM_WIDTH.equals(command)) {
                radarBase.active_moment = RadarData.W;
                basicMoment(CommonProps.MOMENT_W);
            } else if (CommonProps.AC_DBT.equals(command)) {
                radarBase.active_moment = RadarData.DBT;
                basicMoment(CommonProps.MOMENT_DBT);
            } else if (CommonProps.AC_ZDR.equals(command)) {
                radarBase.active_moment = RadarData.ZDR;
                basicMoment(CommonProps.MOMENT_ZDR);
            } else if (CommonProps.AC_KDP.equals(command)) {
                radarBase.active_moment = RadarData.KDP;
                basicMoment(CommonProps.MOMENT_KDP);
            } else if (CommonProps.AC_DP.equals(command)) {
                radarBase.active_moment = RadarData.DP;
                basicMoment(CommonProps.MOMENT_DP);
            } else if (CommonProps.AC_CC.equals(command)) {
                radarBase.active_moment = RadarData.CC;
                basicMoment(CommonProps.MOMENT_CC);
            } else if (CommonProps.AC_SNRH.equals(command)) {
                radarBase.active_moment = RadarData.SNRH;
                basicMoment(CommonProps.MOMENT_SNRH);
            } else if (CommonProps.AC_LIQUID_WATER.equals(command)) {
                radarBase.active_moment = RadarData.DBZ;
                basicMoment(CommonProps.MOMENT_LW);
            } else if (CommonProps.AC_VERTICAL_LIQUID_WATER.equals(command)) {
                radarBase.active_moment = RadarData.DBZ;
                basicMoment2(CommonProps.MOMENT_VIL);
            } else if (CommonProps.AC_ECHO_TOPS.equals(command)) {
                radarBase.active_moment = RadarData.DBZ;
                basicMoment2(CommonProps.MOMENT_ET);
            } else {

            }
        }
        private void basicMoment2(byte moment) {
            GUIManager.activeMainPanel.getRadarBase().currentMoment = moment;
            GUIManager.updateComponents();
        }
        private void basicMoment(byte moment) {
            GUIManager.activeMainPanel.getRadarBase().currentMoment = moment;
            GUIManager.createCutButtons();
            Component[] comps = GUIManager.cutPanel.getComponents();
            int cut = GUIManager.activeMainPanel.getRadarBase().cutNum;
            for (int i = 0; i < comps.length; i++) {
                Component comp = comps[i];
                if(comp instanceof JRadioButton) {
                    int btnCut = Integer.parseInt(((JRadioButton) comp).getActionCommand());
                    if(btnCut == cut) {
                        ((JRadioButton) comp).setSelected(true);
                    } else {
                        ((JRadioButton) comp).setSelected(false);
                    }
                }
            }
            GUIManager.updateComponents();
        }

    }

    private static JPanel createMomentPanel(GridBagLayout gbl) {
        JPanel momentPanel = new JPanel(gbl);
        momentPanel.setLayout(new GridLayout(3, 4));
        momentPanel.setBorder(BorderFactory.createTitledBorder("基本产品"));
        ActionListener listener = new MomentActionListener();

        ButtonGroup moment = new ButtonGroup();
//    	JRadioButton item = new JRadioButton("反射率(R)");
        JRadioButton item = new JRadioButton("Ref");
        item.setActionCommand(CommonProps.AC_REFLECTIVITY);
        item.addActionListener(listener);
        moment.add(item);
        momentPanel.add(item);



//    	JRadioButton item2 = new JRadioButton("速度(V)");
        JRadioButton item2 = new JRadioButton("Vel");
        item2.setActionCommand(CommonProps.AC_VELOCITY);
        item2.addActionListener(listener);
        moment.add(item2);
        momentPanel.add(item2);


//    	JRadioButton item3 = new JRadioButton("谱宽(W)");
        JRadioButton item3 = new JRadioButton("Spec");
        item3.setActionCommand(CommonProps.AC_SPECTRUM_WIDTH);
        item3.addActionListener(listener);
        moment.add(item3);
        momentPanel.add(item3);

//    	JRadioButton item4 = new JRadioButton("垂直累积液水含量(I)");
//        JRadioButton item4 = new JRadioButton("VIL");
//        item4.setActionCommand(CommonProps.AC_VERTICAL_LIQUID_WATER);
//        item4.addActionListener(listener);
//        moment.add(item4);
//        momentPanel.add(item4);
//
        JRadioButton item5 = new JRadioButton("DBT");
        item5.setActionCommand(CommonProps.AC_DBT);
        item5.addActionListener(listener);
        item5.setEnabled(false);
        putComponent("menu_moment" + RadarData.DBT, item5);
        moment.add(item5);
        momentPanel.add(item5);

        JRadioButton item6 = new JRadioButton("ZDR");
        item6.setActionCommand(CommonProps.AC_ZDR);
        item6.addActionListener(listener);
        item6.setEnabled(false);
        putComponent("menu_moment" + RadarData.ZDR, item6);
        moment.add(item6);
        momentPanel.add(item6);

        JRadioButton item7 = new JRadioButton("KDP");
        item7.setActionCommand(CommonProps.AC_KDP);
        item7.addActionListener(listener);
        item7.setEnabled(false);
        putComponent("menu_moment" + RadarData.KDP, item7);
        moment.add(item7);
        momentPanel.add(item7);

        JRadioButton item8 = new JRadioButton("φDP");
        item8.setActionCommand(CommonProps.AC_DP);
        item8.addActionListener(listener);
        item8.setEnabled(false);
        putComponent("menu_moment" + RadarData.DP, item8);
        moment.add(item8);
        momentPanel.add(item8);

        JRadioButton item9 = new JRadioButton("CC");
        item9.setActionCommand(CommonProps.AC_CC);
        item9.addActionListener(listener);
        item9.setEnabled(false);
        putComponent("menu_moment" + RadarData.CC, item9);
        moment.add(item9);
        momentPanel.add(item9);

        JRadioButton item10 = new JRadioButton("SNR");
        item10.setActionCommand(CommonProps.AC_SNRH);
        item10.addActionListener(listener);
        item10.setEnabled(false);
        putComponent("menu_moment" + RadarData.SNRH, item10);
        moment.add(item10);
        momentPanel.add(item10);

        if(jpanels.size() == 1&& jpanels.get(0).getRadarBase().currentMoment==CommonProps.MOMENT_R) {
            item.setSelected(true);
        }

        return momentPanel;
    }
    public static void setDirToolTip(String path) {
        dirImage.setToolTipText("选择数据目录(" + path + ")");
    }

    private static class CutButtonActionListner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                int value = Integer.parseInt(ae.getActionCommand());
                if (value == -1) { // Cut index
                    CommonUtils.alert("请选择要显示的数据文件", GUIManager.list);
                } else {
                    GUIManager.cappiButton.setSelected(false);
                    RadarBase.view = CommonProps.VIEW_PPI;
                    if(syncCut) {
                        GUIManager.getJpanels().forEach(panel -> {
                            panel.getRadarBase().cutNum = value;
                            panel.getRadarBase().view = CommonProps.VIEW_PPI;
                            panel.update = true;
                            panel.repaint();
                        });
                    } else {
                        GUIManager.activeMainPanel.getRadarBase().cutNum = value;
                        GUIManager.activeMainPanel.getRadarBase().view = CommonProps.VIEW_PPI;
                        GUIManager.activeMainPanel.update = true;
                        GUIManager.activeMainPanel.repaint();
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void createCutButtons() {
        ActionListener cutListner = new CutButtonActionListner();
        RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
        RadarData l2 = radarBase.l2;
        List values = new ArrayList();
        if (l2 == null || l2.raf == null) {
            int number = RadarUtils.getCutNumberByVCP(RadarBase.vcp);
            for (int i = 0; i < number; i++) {
                values.add(new String[] { " 第" + (i + 1) + "层 ", "-1" });
            }
        } else {
            int cutNum = 0;
            for (int i = 0; i < l2.getCutNumber(); i++) {
                l2.readHeader(l2.getCutStart(i));//获取最近读取的recordNum，获得该层的不同要素的距离库数、库长
                if (l2.getBinCount(radarBase.active_moment) > 0) {
                    values.add(new String[] { " 第" + (cutNum + 1) + "层 ", String.valueOf(i) });
                    cutNum++;
                }
            }
        }
        String cr = String.valueOf(CommonProps.MOMENT_CR);
        if (radarBase.currentMoment == CommonProps.MOMENT_R) {
            values.add(new String[] { "CR", cr });
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;

        ButtonGroup group = new ButtonGroup();

        JRadioButton cut;
        JRadioButton btn1 = null;
        int count = cutPanel.getComponentCount();
        cutPanel.removeAll();
        int size = values.size();
        int rows = size/3+size%3;
        cutPanel.setLayout(new GridLayout(rows, 3));
        int i = 0;
        for (; i<values.size(); i++) {
            String[] value = (String[]) values.get(i);
            cut = new JRadioButton();
            group.add(cut);
            cutPanel.add(cut, gbc);
            cut.addActionListener(cutListner);
            gbc.gridx = i % 3;
            gbc.gridy = i / 3;
            if (gbc.gridx == 0) {
                gbc.insets.set(2, 5, 2, 5);
            } else {
                gbc.insets.set(2, 5, 2, 5);
            }
            cut.setText(value[0]);
            cut.setActionCommand(value[1]);
            if (cr.equals(value[1])) {
                cut.setToolTipText("组合反射率");
            } else {
                cut.setToolTipText(null);
            }
            if (i == 0 && !"-1".equals(value[1])) {
                btn1 = cut;
            }
            if (!cappiButton.isSelected() && activeCutButton != null
                    && activeCutButton.getText().equals(cut.getText())) {
//                selectCutButton(cut);
                btn1 = null;
            }
        }
        if (!cappiButton.isSelected() && btn1 != null) {
//            selectCutButton(btn1);
        }
        for (; i < cutPanel.getComponentCount();) {
            cutPanel.remove(i);
        }
        setPPIEnabled();
        cutPanel.updateUI();
    }

    public static void selectCutButton(JButton cutButton) {
        if (activeCutButton != null) {
            activeCutButton.setSelected(false);
        }
        activeCutButton = cutButton;
        if (activeCutButton != null) {
            activeCutButton.setSelected(true);
            if(syncCut) {
                for (MainPanel mainPanel : jpanels) {
                    RadarBase radarBase = mainPanel.getRadarBase();
                    radarBase.cutNum = Integer.parseInt(activeCutButton.getActionCommand());
                    RadarBase.view = CommonProps.VIEW_PPI;
                }
            } else {
                RadarBase radarBase = GUIManager.activeMainPanel.getRadarBase();
                radarBase.cutNum = Integer.parseInt(activeCutButton.getActionCommand());
                RadarBase.view = CommonProps.VIEW_PPI;
            }
        }
    }

    public static JToolBar createToolBar(GridBagLayout gbl, ActionListener processor) {

//    	JPanel tooBarPanel = new JPanel(gbl);
//
//    	tooBarPanel.setBorder(new TitledBorderExt("工具箱"));

        JToolBar  toolBar = new JToolBar();
        toolBar.setBorder(BorderFactory.createTitledBorder("工具箱"));
        toolBar.setLayout(new GridLayout(2, 5));
//    	toolBar.setMargin(new Insets(5, 0, 0, 0));
        try {
            Insets insets = new Insets(2, 4, 3, 4);
            JButton button = new JButton(new ImageIcon(CommonUtils
                    .getResImage("resource/cursor.png")));
            putComponent("tool_cursor", button);
            button.setToolTipText("指针");
            button.setSelected(true);
            button.setFocusable(false);
            button.setMargin(insets);
            activeToolButton = button;
            button.setActionCommand(CommonProps.AC_T_CURSOR);
            button.addActionListener(processor);
            toolBar.add(button);
            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/hand.gif")));
            button.setToolTipText("移动");
            button.setFocusable(false);
            button.setActionCommand(CommonProps.AC_T_HAND);
            button.addActionListener(processor);
            button.setMargin(insets);
            toolBar.add(button);
            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/reset.png")));
            button.setToolTipText("复位");
            button.setFocusable(false);
            button.setMargin(insets);
            button.setActionCommand(CommonProps.AC_T_RESET);
            button.addActionListener(processor);
            toolBar.add(button);
            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/zoom_in.png")));
            button.setToolTipText("放大");
            button.setFocusable(false);
            button.setMargin(insets);
            button.setActionCommand(CommonProps.AC_T_ZOOM_IN);
            button.addActionListener(processor);
            toolBar.add(button);
            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/zoom_out.png")));
            button.setActionCommand(CommonProps.AC_T_ZOOM_OUT);
            button.addActionListener(processor);
            button.setToolTipText("缩小");
            button.setMargin(insets);
            button.setFocusable(false);
            toolBar.add(button);
            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/zoom_out.png")));
            button.setActionCommand(CommonProps.AC_T_VCS);
            button.addActionListener(processor);
            button.setToolTipText("画线");
            button.setMargin(insets);
            button.setFocusable(false);
            toolBar.add(button);
            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/measure.gif")));
            button.setActionCommand(CommonProps.AC_T_MEASURE);
            button.addActionListener(processor);
            button.setToolTipText("测距");
            button.setFocusable(false);
            button.setMargin(insets);
            toolBar.add(button);
            toolGrid = new JButton();
            toolGrid.setFocusable(false);
            toolGrid.setMargin(insets);
            setToolGridIcon();
            toolGrid.setActionCommand(CommonProps.AC_T_GRID);
            toolGrid.addActionListener(processor);
            toolBar.add(toolGrid);
            toolMap = new JButton();
            toolMap.setFocusable(false);
            toolMap.setMargin(insets);
            setToolMapIcon();
            toolMap.setActionCommand(CommonProps.AC_T_MAP);
            toolMap.addActionListener(processor);
            toolBar.add(toolMap);
            toolCross = new JButton();
            toolCross.setFocusable(false);
            toolCross.setMargin(insets);
            setToolCrossIcon();
            toolCross.setActionCommand(CommonProps.AC_T_CROSS);
            toolCross.addActionListener(processor);
            toolBar.add(toolCross);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        toolBarLabel = new JLabel();
//        toolBarLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
//        toolBar.add(toolBarLabel);
//        toolBar.setVisible(Radar.showToolBar);
//        tooBarPanel.add(toolBar);
        return toolBar;
    }

    public static void setToolCrossIcon() throws IOException {
        if (RadarParams.auto_update) {
            toolCross.setIcon(new ImageIcon(CommonUtils.getResImage("resource/tick.gif")));
            toolCross.setToolTipText("停止(回放资料)");
        } else {
            toolCross.setIcon(new ImageIcon(CommonUtils.getResImage("resource/cross.gif")));
            toolCross.setToolTipText("更新(实时监控)");
        }
    }

    public static void setToolGridIcon() throws IOException {
        if (MapOverlay.grid_on) {
            toolGrid.setIcon(new ImageIcon(CommonUtils.getResImage("resource/grid_delete.png")));
            toolGrid.setToolTipText("隐藏网格");
        } else {
            toolGrid.setIcon(new ImageIcon(CommonUtils.getResImage("resource/grid_add.png")));
            toolGrid.setToolTipText("显示网格");
        }
    }

    public static void setToolMapIcon() throws IOException {
        if (MapOverlay.map_on) {
            toolMap.setIcon(new ImageIcon(CommonUtils.getResImage("resource/map_delete.png")));
            toolMap.setToolTipText("隐藏地图");
        } else {
            toolMap.setIcon(new ImageIcon(CommonUtils.getResImage("resource/map_add.png")));
            toolMap.setToolTipText("显示地图");
        }
    }

/*    public static JPanel createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        statusCenter = new JLabel(" ", JLabel.CENTER);
        statusCenter.setBorder(BorderFactory
                .createMatteBorder(0, 0, 0, 1, new Color(132, 132, 132)));
        statusBar.add(statusCenter, BorderLayout.CENTER);
        LabelOption[] options = new LabelOption[3];
        options[0] = new LabelOption("软件设计: " + Radar.AUTHOR + "  " + Radar.PHONE);
        options[1] = new LabelOption("E-mail: " + Radar.E_MAIL, "mailto:" + Radar.E_MAIL);
        options[2] = new LabelOption("主页: " + Radar.HOME_PAGE, Radar.HOME_PAGE);
        statusBar.add(new ScrollLabel(options), BorderLayout.EAST);
        statusBar.setVisible(Radar.showStatus);
        return statusBar;
    }*/

    public static JMenuBar createMenu(ActionListener processor) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createCompoundBorder(menuBar.getBorder(), BorderFactory
                .createEmptyBorder(3, 0, 3, 0)));

        JMenu rootMenu = new JMenu("文件(F)");
        rootMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(rootMenu);
        menuUpdate = new JCheckBoxMenuItem("更新(N)", RadarParams.auto_update);
        menuUpdate.setMnemonic(KeyEvent.VK_N);
        menuUpdate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        menuUpdate.addActionListener(processor);
        menuUpdate.setActionCommand(CommonProps.AC_NEW);
        rootMenu.add(menuUpdate);
        JMenuItem item = new JMenuItem("另存为(A)...", KeyEvent.VK_A);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_SAVE_AS);
        rootMenu.add(item);
        JMenu menu = new JMenu("导出(E)");
        menu.setMnemonic(KeyEvent.VK_E);
        rootMenu.add(menu);
        item = new JMenuItem("PPI(P)...", KeyEvent.VK_P);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_EXPORT_PPI);
        menu.add(item);
        item = new JMenuItem("CAPPI(C)...", KeyEvent.VK_C);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_EXPORT_CAPPI);
        menu.add(item);
        item = new JMenuItem("RHI(H)...", KeyEvent.VK_H);
        item.setDisplayedMnemonicIndex(4);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_EXPORT_RHI);
        menu.add(item);
        item = new JMenuItem("当前产品(S)...", KeyEvent.VK_S);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_EXPORT_CURRENT);
        menu.add(item);
        item = new JMenuItem("格式设置(F)...", KeyEvent.VK_F);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_EXPORT_SET);
        menu.add(item);
        //        item = new JMenuItem("散点数据(S)...", KeyEvent.VK_S);
        //        item.addActionListener(processor);
        //        item.setActionCommand(CommonProps.AC_EXPORT_POINTS);
        //        menu.add(item);
        rootMenu.addSeparator();
        item = new JMenuItem("页面设置(U)...", KeyEvent.VK_U);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_PAGE_SETUP);
        rootMenu.add(item);
        item = new JMenuItem("打印预览(V)...", KeyEvent.VK_V);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_PRINT_PREVIEW);
        rootMenu.add(item);
        item = new JMenuItem("打印(P)...", KeyEvent.VK_P);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_PRINT);
        rootMenu.add(item);
        rootMenu.addSeparator();
        item = new JMenuItem("退出(X)", KeyEvent.VK_X);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_EXIT_APP);
        rootMenu.add(item);

        rootMenu = new JMenu("查看(V)");
        rootMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(rootMenu);
/*        item = new JCheckBoxMenuItem("工具栏(T)", Radar.showToolBar);
        item.setMnemonic(KeyEvent.VK_T);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_SHOW_TOOL_BAR);
        rootMenu.add(item);
        item = new JCheckBoxMenuItem("状态栏(B)", Radar.showStatus);
        item.setMnemonic(KeyEvent.VK_B);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_SHOW_STATUS);
        rootMenu.add(item);*/
        item = new JCheckBoxMenuItem("控制栏(C)", Radar.showRightPanel);
        item.setMnemonic(KeyEvent.VK_T);
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_SHOW_RIGHT_PANEL);
        rootMenu.add(item);

        rootMenu = new JMenu("同步(S)");
        rootMenu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(rootMenu);
        itemSyncTool = new JCheckBoxMenuItem("工具箱操作同步(T)",syncTool);
        itemSyncTool.setMnemonic(KeyEvent.VK_T);
        itemSyncTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIManager.syncTool = ((JCheckBoxMenuItem)e.getSource()).getState();
            }
        });
        itemSyncTool.setActionCommand(CommonProps.AC_SYNC_TOOL);
        rootMenu.add(itemSyncTool);
        itemSyncTime = new JCheckBoxMenuItem("文件时间同步(B)", syncTime);
        itemSyncTime.setMnemonic(KeyEvent.VK_B);
        itemSyncTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIManager.syncTime = ((JCheckBoxMenuItem)e.getSource()).getState();
                if(GUIManager.isSyncTime()) {
                    GUIManager.syncAllMainPanel4Time();
                }
            }
        });
        itemSyncTime.setActionCommand(CommonProps.AC_SYNC_TIME);
        rootMenu.add(itemSyncTime);
        itemSyncCut = new JCheckBoxMenuItem("仰角同步(C)", syncCut);
        itemSyncCut.setMnemonic(KeyEvent.VK_T);
        itemSyncCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIManager.syncCut = ((JCheckBoxMenuItem)e.getSource()).getState();
                if(GUIManager.isSyncCut()) {
                    GUIManager.syncAllMainPanel4Cut();
                }
            }
        });
        itemSyncCut.setActionCommand(CommonProps.AC_SYNC_CUT);
        rootMenu.add(itemSyncCut);
        ActionListener listener = new MomentActionListener();
        ButtonGroup moment = new ButtonGroup();
        rootMenu = new JMenu("变量(M)");
        rootMenu.setMnemonic(KeyEvent.VK_M);
        menuBar.add(rootMenu);
        item = new JMenuItem("      基本产品");
        rootMenu.add(item);
        item = new JRadioButtonMenuItem("反射率(R)", true);
        item.setMnemonic(KeyEvent.VK_R);
        item.setActionCommand(CommonProps.AC_REFLECTIVITY);
        item.addActionListener(processor);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        putComponent("menu_moment" + RadarData.DBZ, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("速度(V)");
        item.setActionCommand(CommonProps.AC_VELOCITY);
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(processor);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        putComponent("menu_moment" + RadarData.V, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("谱宽(W)");
        item.setActionCommand(CommonProps.AC_SPECTRUM_WIDTH);
        item.setMnemonic(KeyEvent.VK_W);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        item.addActionListener(processor);
        putComponent("menu_moment" + RadarData.W, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("滤波前反射率(DBT)");
        item.setActionCommand(CommonProps.AC_DBT);
        item.addActionListener(processor);
        item.setVisible(false);
        putComponent("menu_moment" + RadarData.DBT, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("差分反射率(ZDR)");
        item.setActionCommand(CommonProps.AC_ZDR);
        item.addActionListener(processor);
        item.setVisible(false);
        putComponent("menu_moment" + RadarData.ZDR, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("差分相移率(KDP)");
        item.setActionCommand(CommonProps.AC_KDP);
        item.addActionListener(processor);
        item.setVisible(false);
        putComponent("menu_moment" + RadarData.KDP, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("差分相移(φDP)");
        item.setActionCommand(CommonProps.AC_DP);
        item.addActionListener(processor);
        item.setVisible(false);
        putComponent("menu_moment" + RadarData.DP, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("协相关系数(CC)");
        item.setActionCommand(CommonProps.AC_CC);
        item.addActionListener(processor);
        item.setVisible(false);
        putComponent("menu_moment" + RadarData.CC, item);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("水平通道信噪比(SNRH)");
        item.setActionCommand(CommonProps.AC_SNRH);
        item.addActionListener(processor);
        item.setVisible(false);
        putComponent("menu_moment" + RadarData.SNRH, item);
        rootMenu.add(item);
        moment.add(item);
        rootMenu.addSeparator();
        item = new JMenuItem("      计算产品");
        rootMenu.add(item);
        item = new JRadioButtonMenuItem("垂直累积液水含量(I)");
        item.setMnemonic(KeyEvent.VK_I);
        item.setActionCommand(CommonProps.AC_VERTICAL_LIQUID_WATER);
        item.addActionListener(listener);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("cappi(I)");
        item.setMnemonic(KeyEvent.VK_C);
        item.setActionCommand(CommonProps.AC_CAPPI_new);
        item.addActionListener(listener);
        rootMenu.add(item);
        moment.add(item);
        item = new JRadioButtonMenuItem("etops(I)");
        item.setMnemonic(KeyEvent.VK_E);
        item.setActionCommand(CommonProps.AC_ECHO_TOPS);
        item.addActionListener(listener);
        rootMenu.add(item);
        moment.add(item);
        //分析
        rootMenu = new JMenu("分析(A)");
        rootMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(rootMenu);
        item = new JMenuItem("RHI距离高度显示(H)", KeyEvent.VK_H);
        //item.setDisplayedMnemonicIndex(10);
        item.setActionCommand(CommonProps.AC_RHI);
        item.addActionListener(processor);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        rootMenu.add(item);
        item = new JMenuItem("任意两点剖面图(H)", KeyEvent.VK_L);
        //item.setDisplayedMnemonicIndex(10);
        item.setActionCommand(CommonProps.AC_T_VCS_MENU);
        item.addActionListener(processor);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));//是当鼠标焦点在你所在的程序界面上，你按下ALT和D键就能打开相应的menuItem
        rootMenu.add(item);
        menuBar.add(rootMenu);


        rootMenu = new JMenu("设置(P)");
        rootMenu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(rootMenu);
        item = new JMenuItem("站点信息(I)...");
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_SITE_INFO);
        item.setMnemonic(KeyEvent.VK_I);
        rootMenu.add(item);
        item = new JMenuItem("地理背景(B)...");
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_BACKGROUND);
        item.setMnemonic(KeyEvent.VK_B);
        rootMenu.add(item);
        item = new JMenuItem("分辨率(R)...");
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_RESOLUTION);
        item.setMnemonic(KeyEvent.VK_R);
        rootMenu.add(item);

        rootMenu = new JMenu("叠加(O)");
        rootMenu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(rootMenu);
        menuGrid = new JCheckBoxMenuItem("网格(G)", MapOverlay.grid_on);
        menuGrid.setActionCommand(CommonProps.AC_GRID);
        menuGrid.setMnemonic(KeyEvent.VK_G);
        menuGrid.addActionListener(processor);
        menuGrid.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
        rootMenu.add(menuGrid);
        menuMap = new JCheckBoxMenuItem("地图(M)", MapOverlay.map_on);
        menuMap.setMnemonic(KeyEvent.VK_M);
        menuMap.setActionCommand(CommonProps.AC_MAP);
        menuMap.addActionListener(processor);
        menuMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
        rootMenu.add(menuMap);
        item = new JMenuItem("区域(C)...");
        item.addActionListener(processor);
        item.setActionCommand(CommonProps.AC_AREA);
        item.setMnemonic(KeyEvent.VK_C);
        rootMenu.add(item);

        rootMenu = new JMenu("帮助(H)");
        rootMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(rootMenu);
        item = new JMenuItem("在线帮助(H)");
        item.setActionCommand(CommonProps.AC_HELP);
        item.addActionListener(processor);
        item.setMnemonic(KeyEvent.VK_H);
        item.setAccelerator(KeyStroke.getKeyStroke("F1"));
        rootMenu.add(item);
        item = new JMenuItem("关于 " + Radar.APP_NAME + "(A)");
        item.setActionCommand(CommonProps.AC_ABOUT);
        item.addActionListener(processor);
        item.setMnemonic(KeyEvent.VK_A);
        rootMenu.add(item);

        return menuBar;
    }

    public static void removeAllListElems() {
        DefaultListModel model = (DefaultListModel) list.getModel();
        model.removeAllElements();
    }

    public static void setFileEnabled(boolean enabled, boolean includeLoop) {
        list.setEnabled(enabled);
        previous.setEnabled(enabled);
        next.setEnabled(enabled);
        if (includeLoop) {
            loop.setEnabled(enabled);
        }
    }

    public static void setPPIEnabled() {
        boolean enabled = true;
        if (activeMainPanel.getRadarBase().currentMoment == CommonProps.MOMENT_ET
                || activeMainPanel.getRadarBase().currentMoment == CommonProps.MOMENT_HP
                || activeMainPanel.getRadarBase().currentMoment == CommonProps.MOMENT_VIL) {
            enabled = false;
        }
        if (enabled) {
            Component[] comps = cutPanel.getComponents();
            if (comps != null) {
                for (int i = 0; i < comps.length; i++) {
                    comps[i].setEnabled(true);
                }
            }
            cappiButton.setEnabled(true);
            cappiText.setEditable(true);
        } else {
            cappiButton.setSelected(false);
            selectCutButton(null);
            Component[] comps = cutPanel.getComponents();
            if (comps != null) {
                for (int i = 0; i < comps.length; i++) {
                    comps[i].setEnabled(false);
                }
            }
            cappiButton.setEnabled(false);
            cappiText.setEditable(false);
            list.requestFocusInWindow();
        }
    }

    public static String getCurrentLayer() {
        if (activeCutButton != null) {
            String text = activeCutButton.getText();
            return text.substring(2, text.length() - 2);
        } else {
            Component[] comps = cutPanel.getComponents();
            for (int i = 0; i < comps.length; i++) {
                JButton b = (JButton) comps[i];
                if (activeMainPanel.getRadarBase().cutNum == Integer.parseInt(b.getActionCommand())) {
                    String text = b.getText();
                    return text.substring(2, text.length() - 2);
                }
            }
        }
        return "1";
    }

    public static Component putComponent(String key, Component comp) {
        return (Component) compMap.put(key, comp);
    }

    public static Component getComponent(String key) {
        return (Component) compMap.get(key);
    }

    public static Component removeComponent(String key) {
        return (Component) compMap.remove(key);
    }

    public static void updateComponents() {
        activeMainPanel.update = true;
        GUIManager.repaintCurrent();
        RHI.update();
        BasicDrawPanel panel = (BasicDrawPanel) GUIManager.getComponent("vcs_panel");
        if (panel != null) {
            panel.update();
        }
    }
    public static void updateComponentsAll() {
        if(GUIManager.isSyncTime()) {
            for (MainPanel jPanel : jpanels) {
                jPanel.update = true;
            }
            GUIManager.repaintAll();
            RHI.update();
            VCS.update();
            BasicDrawPanel panel = (BasicDrawPanel) GUIManager.getComponent("vcs_panel");
            if (panel != null) {
                panel.update();
            }
        }
        GUIManager.activeMainPanel.update = true;
        GUIManager.activeMainPanel.getMap().update = true;
        GUIManager.activeMainPanel.repaint();
        RHI.update();
        VCS.update();

    }



    public static void syncAllMainPanel(boolean panelUpdate, boolean mapUpdate, int dx, int dy, int x, int y) {
        if(!GUIManager.syncTool)
            return;
        RadarBase base = activeMainPanel.getRadarBase();
        for (MainPanel mainPanel : jpanels) {
            if(mainPanel != activeMainPanel) {
                RadarBase radarBase = mainPanel.getRadarBase();
                radarBase.xoffset -=dx;
                radarBase.yoffset -=dy;
                radarBase.zoom = base.zoom;
                radarBase.scale_X = base.scale_X;
                radarBase.scale_Y = base.scale_Y;
                mainPanel.xoffset +=dx;
                mainPanel.yoffset +=dy;
                mainPanel.getMap().update = mapUpdate;

                mainPanel.setmX(x);
                mainPanel.setmY(y);

                mainPanel.update = panelUpdate;
                mainPanel.repaint();
            }
        }
    }

    public static void syncAllMainPanel(boolean update) {
        if(!GUIManager.syncTool)
            return;
        RadarBase base = activeMainPanel.getRadarBase();
        for (MainPanel mainPanel : jpanels) {
            if(mainPanel != activeMainPanel) {
                RadarBase radarBase = mainPanel.getRadarBase();
                radarBase.xoffset = base.xoffset;
                radarBase.yoffset = base.yoffset;
                radarBase.zoom = base.zoom;
                radarBase.scale_X = base.scale_X;
                radarBase.scale_Y = base.scale_Y;
                mainPanel.xoffset = activeMainPanel.xoffset;
                mainPanel.yoffset = activeMainPanel.yoffset;

                mainPanel.getMap().update = true;
                mainPanel.update = update;
                mainPanel.repaint();
            }
        }
    }

    public static void syncAllMainPanelMouseMoved(int x, int y) {
        if(!GUIManager.syncTool)
            return;
        RadarBase base = activeMainPanel.getRadarBase();
        for (MainPanel mainPanel : jpanels) {
//			RadarBase radarBase = mainPanel.getRadarBase();
//			radarBase.xoffset = base.xoffset;
//			radarBase.yoffset = base.yoffset;
//			radarBase.zoom = base.zoom;
//			radarBase.scale_X = base.scale_X;
//			radarBase.scale_Y = base.scale_Y;
//			mainPanel.xoffset = activeMainPanel.xoffset;
//			mainPanel.yoffset = activeMainPanel.yoffset;
//			mainPanel.getMap().update = true;
//			mainPanel.update = update;
            mainPanel.setmX(x);
            mainPanel.setmY(y);
            mainPanel.setmActive(true);
            mainPanel.setShowStatusText(true);
            mainPanel.repaint();
        }
    }



    public static void syncAllMainPanel4Time() {
        RadarBase base = activeMainPanel.getRadarBase();
        for (MainPanel mainPanel : jpanels) {
            if(mainPanel != activeMainPanel) {
                RadarBase base2 = mainPanel.getRadarBase();
                ListElement elem = (ListElement) GUIManager.list.getSelectedValue();
                if(null == elem) {
                    base2.l2 = null;
                    continue;
                }
                base2.l2= base.l2;
                mainPanel.update = true;
                mainPanel.repaint();
            }
        }
    }


    protected static void syncAllMainPanel4Cut() {
        RadarBase base = activeMainPanel.getRadarBase();
        for (MainPanel mainPanel : jpanels) {
            if(mainPanel != activeMainPanel) {
                RadarBase base2 = mainPanel.getRadarBase();
                base2.cutNum = base.cutNum;
//    			base2.l2 = base.l2;

//    			base2.vcp = base.l2.vcp;
//    			base2.resolution = base.l2.resolution;
                mainPanel.update = true;
                mainPanel.repaint();
            }
        }
    }


	public static void updateMomnetMenu(RadarData l2) {
		Set<Integer> set = l2.getDataTypeSet();
		for (int i = 1; i <= 16; i++) {
			Component comp = getComponent("menu_moment" + i);
			if (comp != null) {
				if (set.contains(i)) {
                    comp.setEnabled(true);
				} else {
					comp.setEnabled(false);
				}
			}
		}
		if (!set.contains(activeMainPanel.getRadarBase().active_moment)) {
			((JMenuItem) getComponent("menu_moment" + RadarData.DBZ)).doClick();
		}
	}

}


