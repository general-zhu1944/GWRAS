package com.kitty.radar.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.RadarUtils;

public class MeasurePanel implements MouseListener, MouseWheelListener {

    private List list = new ArrayList();

    private int[] xy = new int[2];
    private RadarBase radarBase;

    public MeasurePanel(RadarBase radarBase) {
        this.radarBase = radarBase;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.MAGENTA);
        g2.setStroke(new BasicStroke(2));
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(qualityHints);
        int size = list.size();
        for (int i = 1; i < size; i++) {
            int[] v1 = (int[]) list.get(i - 1);
            int[] v2 = (int[]) list.get(i);
            g2.drawLine(v1[0], v1[1], v2[0], v2[1]);
        }
        if (size > 0) {
            int[] v2 = (int[]) list.get(size - 1);
            g2.drawLine(v2[0], v2[1], xy[0], xy[1]);
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        xy[0] = e.getX();
        xy[1] = e.getY();
        if (e.getClickCount() == 1) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                list.clear();
                e.getComponent().repaint();
            } else {
                list.add(new int[] { e.getX(), e.getY() });
                MouseListener[] listeners = GUIManager.activeMainPanel.getMouseListeners();
                if (listeners != null) {
                    for (int i = 0; i < listeners.length; i++) {
                        listeners[i].mousePressed(e);
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() > 1 && e.getButton() != MouseEvent.BUTTON3) {
            double length = 0;
            for (int i = 1; i < list.size(); i++) {
                int[] v1 = (int[]) list.get(i - 1);
                int[] v2 = (int[]) list.get(i);
                length += Math.hypot(v2[0] - v1[0], v2[1] - v1[1]);
            }
            length = length / radarBase.scale_X;
            JOptionPane.showMessageDialog(Radar.radar, "¾àÀë²âËã½á¹û£º" + CommonUtils.format(length, 1)
                    + " " + RadarUtils.getDistanceUnitLabel(), "", JOptionPane.INFORMATION_MESSAGE);
            list.clear();
            e.getComponent().repaint();
        }
    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        if (list.size() > 0) {
            xy[0] = e.getX();
            xy[1] = e.getY();
            e.getComponent().repaint();
        }
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.list.clear();
    }

}
