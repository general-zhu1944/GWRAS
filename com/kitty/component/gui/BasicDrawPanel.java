package com.kitty.component.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 通用绘图JPanel基类，提高了重复调用paintComponent方法的性能（如：解决初次显示时两次调用paintComponent的问题）。
 */
public abstract class BasicDrawPanel extends JPanel {

    protected int height;

    protected int width;

    private BufferedImage image;

    private boolean update = true;

    public BasicDrawPanel() {
        this.setDoubleBuffered(false);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();
        if (w != width || h != height) {
            width = w;
            height = h;
            image = null;
        }
        if (image == null) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            update = true;
        }
        if (update) {
            Graphics2D g2 = (Graphics2D) image.createGraphics();
            draw(g2);
            g2.dispose();
            update = false;
        }
        g.drawImage(image, 0, 0, width, height, null);
    }

    public abstract void draw(Graphics2D g);

    public void update() {
        update = true;
        this.repaint();
    }

}
