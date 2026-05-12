package com.kitty.component.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.kitty.component.gui.domain.LabelOption;

/**
 * 支持滚动显示的Label。
 */
public class ScrollLabel extends BasicPanel {

    private LabelOption[] options;

    private int interval = 3600000; // 文本显示时间间隔，单位：ms

    private Timer timer;

    private JLabel label;

    private int length = -1; // 移动的距离

    private boolean over = false; // 鼠标是否停留在ScrollLabel上

    public ScrollLabel(LabelOption[] options, int horizontalAlignment) {
        this.options = options;
        this.label = new JLabel(" ", horizontalAlignment);
        this.add(label);
        int width = -1;
        for (int i = 0; i < options.length; i++) {
            int w = options[i].getWidth();
            if (w > width) {
                width = w;
            }
        }
        width += 16;
        Dimension d = this.getMinimumSize();
        d.setSize(width, BasicPanel.LABEL_HEIGHT);
        this.setMinimumSize(d);
        this.setPreferredSize(d);
        label.setBounds(0, 0, width, BasicPanel.LABEL_HEIGHT);
        timer = new Timer();
        timer.schedule(new ScrollTimerTask(), 90000, interval);
    }

    public ScrollLabel(LabelOption[] options) {
        this(options, JLabel.CENTER);
    }

    private class ScrollTimerTask extends TimerTask {

        public void run() {
            try {
                if (length < 0) {
                    this.getLength();
                }
                if (length > 0) {
                    for (int i = 0; i < options.length; i++) {
                        this.initLabel(i);
                        for (int j = 0; j < length; j++) {
                            this.moveLabel();
                            Thread.sleep(40);
                        }
                        Thread.sleep(options[i].getShowTime());
                        while (over) {
                            Thread.sleep(3000);
                        }
                        for (int j = 0; j < length; j++) {
                            this.moveLabel();
                            Thread.sleep(40);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void initLabel(final int index) throws Exception {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    label.setLocation(0, ScrollLabel.this.getHeight());
                    label.setText(options[index].getText());
                    label.setCursor(Cursor.getDefaultCursor());
                    label.setForeground(Color.BLACK);
                    over = false;
                    MouseListener[] listeners = label.getMouseListeners();
                    if (listeners != null) {
                        for (int i = 0; i < listeners.length; i++) {
                            label.removeMouseListener(listeners[i]);
                        }
                    }
                    if (options[index].getHref() != null) {
                        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        label.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent e) {
                                over = true;
                                label.setForeground(Color.BLUE);
                            }

                            public void mouseExited(MouseEvent e) {
                                over = false;
                                label.setForeground(Color.BLACK);
                            }

                            public void mouseClicked(MouseEvent e) {
                                try {
                                    Desktop.getDesktop().browse(new URI(options[index].getHref()));
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }

        private void moveLabel() throws Exception {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Point p = label.getLocation();
                    label.setLocation(p.x, p.y - 1);
                }
            });
        }

        private void getLength() throws Exception {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    int h = ScrollLabel.this.getHeight();
                    if (h > 0) {
                        length = (int) Math.round((h + BasicPanel.LABEL_HEIGHT) / 2.0);
                    }
                }
            });
        }

    }

}
