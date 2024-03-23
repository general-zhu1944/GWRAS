package com.kitty.radar.business.surface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.eltima.components.ui.DatePicker;
import com.kitty.component.gui.BasicDialog;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.RainOverlay;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.ImportAreaDialog;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.listener.MouseHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;

/**
 * 区域列表对话框。
 */
public class SurfaceDialog extends JDialog {
	    public static com.eltima.components.ui.DatePicker datePicker1;
	    public static com.eltima.components.ui.DatePicker datePicker2;	    
	    public static JCheckBox jCheckBox_SurfaceData;
	    public static JButton queryrain;
	    public static JButton querytemper;
	    public static JButton querytd;
	    public static JButton querywindmax;
	    public static JTextField textrain;

	    JLabel label1 = new JLabel("起始：");
        JLabel label2 = new JLabel("结束：");
        JLabel label3 = new JLabel("   ");
        JLabel label4 = new JLabel("阈值：");




    public SurfaceDialog(ActionListener processor) {      
        super(Radar.radar, "地面实况资料查询");
        Dimension d = new Dimension(400, 150);
        this.setSize(d);
        this.setLocation(CommonUtils.getCenterLocation(this.getSize()));
        JPanel elePanel = new JPanel();
        elePanel.setLayout(new FlowLayout(0,5,5));
        datePicker1 = getDatePicker();
        datePicker2 = getDatePicker();
        elePanel.add(label1);
        elePanel.add(datePicker1);
        elePanel.add(label3);
        elePanel.add(label2);
        elePanel.add(datePicker2);
        JPanel elePanel1 = new JPanel();
        elePanel1.setLayout(new FlowLayout(1,5,5));
        elePanel1.add(label4);
        textrain = new JTextField("", 4);
        elePanel1.add(textrain);
        queryrain = new JButton("分钟累计雨量");
        queryrain.setActionCommand(CommonProps.AC_QUERY_RAIN);
        queryrain.addActionListener(processor);       
        elePanel1.add(queryrain);
        querytemper = new JButton("温度");
        querytemper.setActionCommand(CommonProps.AC_QUERY_TEMPER);
        querytemper.addActionListener(processor);       
        elePanel1.add(querytemper);
        querytd = new JButton("露点");
        querytd.setActionCommand(CommonProps.AC_QUERY_TD);
        querytd.addActionListener(processor);       
        elePanel1.add(querytd);
        querywindmax = new JButton("极大风速");
        querywindmax.setActionCommand(CommonProps.AC_QUERY_WINDMAX);
        querywindmax.addActionListener(processor);       
        elePanel1.add(querywindmax);


        MainPanel mainPanel = GUIManager.activeMainPanel;
        RainOverlay rain = mainPanel.getRain();
        JPanel selectrainPanel = new JPanel();
        selectrainPanel.setLayout(new FlowLayout(1,15,5));
        JCheckBox cb1 = new JCheckBox("散点");
        cb1.setSelected(rain._drawDiscreteData);
        cb1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	rain._drawDiscreteData=true;
                    if(jCheckBox_SurfaceData.isSelected())
                    {
              		  repaintRain();
                    }
                } else {
                	rain._drawDiscreteData=false;
                	  if(jCheckBox_SurfaceData.isSelected())
                      {
                		  repaintRain();
                      }
                }
            }
        });

        selectrainPanel.add(cb1,BorderLayout.WEST);
        JCheckBox cb2 = new JCheckBox("等值线");
        cb2.setSelected(rain._drawContourLine);
        cb2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	rain._drawContourLine=true;
                    if(jCheckBox_SurfaceData.isSelected())
                    {
              		  repaintRain();
                    }
                } else {
                	rain._drawContourLine=false;
                	  if(jCheckBox_SurfaceData.isSelected())
                      {
                		  repaintRain();
                      }
                }
            }
        });
        selectrainPanel.add(cb2,BorderLayout.CENTER); 
        JCheckBox cb3 = new JCheckBox("色斑图");
        cb3.setSelected(rain._drawContourPolygon);
        cb3.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	rain._drawContourPolygon=true;
                    if(jCheckBox_SurfaceData.isSelected())
                    {
                	
                    	repaintRain();
                    }
                } else {
                	rain._drawContourPolygon=false;
                	  if(jCheckBox_SurfaceData.isSelected())
                      {
                		  repaintRain();
                      }
                }
            }
        });
        selectrainPanel.add(cb3,BorderLayout.EAST);
        jCheckBox_SurfaceData = new javax.swing.JCheckBox("叠加地面实况");
        jCheckBox_SurfaceData.setActionCommand(CommonProps.AC_DiscreteData);
        jCheckBox_SurfaceData.addActionListener(processor);
        jCheckBox_SurfaceData.setSelected(RainOverlay.rain_on);
        selectrainPanel.add(jCheckBox_SurfaceData,BorderLayout.SOUTH);
       // this.mainPanel.add(elePanel);
//        buttonPanel.remove(this.confirmButton);
//        this.setButtonHgap(5);
//        JButton button = new JButton(" 新建(N) ");
//        button.setMnemonic(KeyEvent.VK_N);
//        button.setActionCommand("new");
//        button.addActionListener(this);
//        buttonPanel.add(button, 0);
//        button = new JButton(" 修改(M) ");
//        button.setMnemonic(KeyEvent.VK_M);
//        button.setActionCommand("modify");
//        button.addActionListener(this);
//        buttonPanel.add(button, 1);
//        button = new JButton(" 删除(D) ");
//        button.setMnemonic(KeyEvent.VK_D);
//        button.setActionCommand("delete");
//        button.addActionListener(this);
//        buttonPanel.add(button, 2);
//        button = new JButton(" 导入(I) ");
//        button.setMnemonic(KeyEvent.VK_I);
//        button.setActionCommand("import");
//        button.addActionListener(this);
//        buttonPanel.add(button, 3);
 //       cancelButton.setText(" 关闭(C) ");
        
        this.add(elePanel, BorderLayout.NORTH);
        this.add(elePanel1, BorderLayout.CENTER);
        this.add(selectrainPanel,BorderLayout.SOUTH);
        this.setVisible(true);
        this.setResizable(false);
    }
    
    public static void repaintRain() {
    	if(GUIManager.syncTool) {
    		GUIManager.getJpanels().forEach(panel -> {
    			panel.getRain().update = true;
    			panel.repaint();
    		});
    	} else {
    		MainPanel panel = GUIManager.activeMainPanel;
    		panel.getRain().update = true;
			panel.repaint();
    	}
    }
    public static DatePicker getDatePicker() {
        // 显示格式
        String DefaultFormat = "yyyy-MM-dd HH:mm";
        // 当前时间
        Date date = new Date();
        // 设置字体
        Font font = new Font("Times New Roman", Font.BOLD, 14);
        Dimension dimension = new Dimension(130, 24);
        // 高亮显示的日期
        int[] hilightDays = { 1, 3, 5, 7 };
        // 灰色显示的日期
        //int[] disabledDays = { 4, 6, 5, 9 };        
        //构造方法（初始时间，时间显示格式，字体，控件大小）
        DatePicker datepick = new DatePicker(date, DefaultFormat, font, dimension);
        //设置起始位置
        datepick.setLocation(137, 83);
        // 设置一个月份中需要高亮显示的日子
        datepick.setHightlightdays(hilightDays, Color.red);
        // 设置一个月份中不需要的日子，呈灰色显示
       // datepick.setDisableddays(disabledDays);
        // 设置国家
        datepick.setLocale(Locale.CANADA);
        // 设置时钟面板可见
        datepick.setTimePanleVisible(true);       
        return datepick;
    }

    public void actionPerformed(ActionEvent e) {
        
                CommonUtils.alert("请选择要删除的区域", null);
      
    }

}
