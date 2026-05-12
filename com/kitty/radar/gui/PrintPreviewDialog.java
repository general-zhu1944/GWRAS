package com.kitty.radar.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.kitty.radar.Radar;
import com.kitty.radar.RadarParams;
import com.kitty.radar.listener.DisposeActionHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;

public class PrintPreviewDialog extends JDialog {

    private JPanel panel;

    public PrintPreviewDialog(Frame owner) {
        super(owner, "打印预览", true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        d.setSize(d.getWidth(), d.getHeight() - 30);
        this.setSize(d);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(0, 0);
        try {
            JToolBar toolBar = new JToolBar();
            Insets insets = new Insets(2, 4, 3, 4);
            toolBar.setBorder(BorderFactory.createEtchedBorder());

            JButton button = new JButton("打印(P)...");
            button.setToolTipText("打印(Alt+P)");
            button.setMnemonic(KeyEvent.VK_P);
            button.setFocusable(false);
            button.setMargin(insets);
            button.setActionCommand(CommonProps.AC_PREVIEW_PRINT);
            button.addActionListener(Radar.processor);
            toolBar.add(button);
            toolBar.addSeparator();

            button = new JButton(new ImageIcon(CommonUtils.getResImage("resource/page_gear.png")));
            button.setToolTipText("页面设置(Alt+U)");
            button.setMnemonic(KeyEvent.VK_U);
            button.setFocusable(false);
            button.setMargin(insets);
            button.setActionCommand(CommonProps.AC_PREVIEW_PAGE_SETUP);
            button.addActionListener(Radar.processor);
            toolBar.add(button);
            toolBar.addSeparator();

            button = new JButton("关闭(C)");
            button.setToolTipText("关闭(Alt+C)");
            button.setMnemonic(KeyEvent.VK_C);
            button.setFocusable(false);
            button.setMargin(insets);
            button.addActionListener(new DisposeActionHandler(this));
            toolBar.add(button);

            this.add(toolBar, BorderLayout.NORTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                double xoff; //在屏幕上页面初始位置的水平偏移
                double yoff; //在屏幕上页面初始位置的垂直偏移
                double scale; //在屏幕上适合页面的比例
                double px = RadarParams.pageFormat.getWidth(); //页面宽度
                double py = RadarParams.pageFormat.getHeight(); //页面高度
                double sx = getWidth() - 1;
                double sy = getHeight() - 1;
                if (px / py < sx / sy) {
                    scale = sy / py; //计算比例
                    xoff = 0.5 * (sx - scale * px); //水平偏移量
                    yoff = 0;
                } else {
                    scale = sx / px; //计算比例
                    xoff = 0;
                    yoff = 0.5 * (sy - scale * py); //垂直偏移量
                }
                g2.translate(xoff, yoff); //转换坐标
                g2.scale(scale, scale);
                Rectangle2D page = new Rectangle2D.Double(0, 0, px, py); //绘制页面矩形
                g2.setPaint(Color.white); //设置页面背景为白色
                g2.fill(page);
                g2.setPaint(Color.black);//设置页面文字为黑色
                g2.draw(page);
                try {
                    GUIManager.activeMainPanel.print(g2, RadarParams.pageFormat, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.add(panel, BorderLayout.CENTER);
    }

    public JPanel getPreviewPanel() {
        return panel;
    }

}
