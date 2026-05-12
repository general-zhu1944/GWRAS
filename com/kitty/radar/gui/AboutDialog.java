package com.kitty.radar.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.kitty.component.gui.ImagePanel;
import com.kitty.radar.Radar;
import com.kitty.radar.listener.DisposeActionHandler;
import com.kitty.radar.util.CommonUtils;

/**
 * 关于对话框。
 */
public class AboutDialog extends JDialog {

    public AboutDialog(Frame owner) {
        super(owner, "关于 " + Radar.APP_NAME, true);
        Dimension d = new Dimension(400, 255);
        this.setSize(d);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(CommonUtils.getCenterLocation(d));
        this.setResizable(false);
        this.setLayout(null);
        ImagePanel imagePanel = new ImagePanel("resource/about.gif");
        this.add(imagePanel);
        imagePanel.setBounds(0, 0, 400, 43);
        JLabel label = new JLabel("软件版本: " + Radar.APP_VERSION);
        this.add(label);
        label.setBounds(23, 53, 100, 30);
        label = new JLabel(Radar.COPYRIGHT_INFO);
        this.add(label);
        label.setBounds(120, 53, 250, 30);
        label = new JLabel("软件设计: " + Radar.AUTHOR + "  " + Radar.PHONE + "  " + Radar.E_MAIL);
        this.add(label);
        label.setBounds(23, 83, 350, 30);
        label = new JLabel("特别感谢: 康岚  四川省气象台， 王亚强 中国气象科学研究院");
        this.add(label);
        label.setBounds(23, 113, 350, 30);
        label = new JLabel("资助：川东北强天气南充市重点实验室、复盘专项");
        this.add(label);
        label.setBounds(23, 143, 350, 30);
        label = new JLabel("四川省强对流创新团队、南充市气象局强对流创新团队");
        this.add(label);
        label.setBounds(58, 163, 350, 30);
        //String mapFile = CommonUtils.appPath + "province.map";
    	//JOptionPane.showMessageDialog(this, mapFile);

        final JButton confirm = new JButton("    确定    ");
        DisposeActionHandler listener = new DisposeActionHandler(this);
        confirm.addActionListener(listener);
        this.add(confirm);
//        confirm.setBounds(287, 184, 85, 24);
//        JButton button = new JButton("联系我们");
//        button.setDefaultCapable(false);
//        button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    Desktop.getDesktop().browse(new URI("mailto:" + Radar.E_MAIL));
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                    CommonUtils.alert("欢迎来电（" + Radar.PHONE + "）或E-mail（" + Radar.E_MAIL + "）联系",
//                            null);
//                }
//            }
//        });
//        button.setBounds(23, 184, 70, 24);
//        this.add(button);
//        button = new JButton("查看许可协议");
//        button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    Desktop.getDesktop().open(new File(CommonUtils.appPath + "license.txt"));
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
////        });
//        button.setBounds(103, 184, 90, 24);
//        button.setDefaultCapable(false);
//        this.add(button);
        CommonUtils.addEscAction((JComponent) this.getContentPane(), listener);
        this.getRootPane().setDefaultButton(confirm);
        CommonUtils.addFirstFocus(this, confirm);
    }

    public static void createAboutDialog(JFrame owner) {
        AboutDialog dialog = new AboutDialog(owner);
        dialog.setVisible(true);
    }

}
