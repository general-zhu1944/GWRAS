package com.kitty.component.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import com.kitty.radar.util.CommonUtils;

/**
 * 图像JPanel，用于显示图像。
 */
public class ImagePanel extends JPanel {

    private Image image;

    public ImagePanel(String path) {
        try {
            image = CommonUtils.getResImage(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

}
