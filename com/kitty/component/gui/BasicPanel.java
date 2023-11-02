package com.kitty.component.gui;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * X、Y坐标布局的Panel基类。
 */
public class BasicPanel extends JPanel {

    public static final byte LABEL_HEIGHT = 13;

    public static final byte TEXT_FIELD_HEIGHT = 22;

    public static final byte BUTTON_HEIGHT = 24;

    public BasicPanel() {
        super(null);
    }

    /**
     * 添加一个JLabel组件，该组件位于comp组件的同行右侧。
     * 
     * @param text
     * @param comp
     * @return
     */
    public JLabel addLabel(String text, Component comp) {
        int[] pos = getPosition(comp, LABEL_HEIGHT);
        return addLabel(text, pos[0], pos[1]);
    }

    /**
     * 按X、Y坐标添加一个JLabel组件。
     * 
     * @param text
     * @param x
     * @param y
     * @return
     */
    public JLabel addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        this.add(l);
        l.setBounds(x, y, getLabelWidth(text), LABEL_HEIGHT);
        return l;
    }

    public static int getLabelWidth(String text) {
        return text.getBytes().length * 6;
    }

    public JTextField addTextField(int width, Component comp) {
        int[] pos = getPosition(comp, TEXT_FIELD_HEIGHT);
        return addTextField(pos[0], pos[1], width);
    }

    public JTextField addTextField(int x, int y, int width) {
        JTextField tf = new JTextField();
        this.add(tf);
        tf.setBounds(x, y, width, TEXT_FIELD_HEIGHT);
        return tf;
    }

    public JButton addButton(String text, Component comp) {
        int[] pos = getPosition(comp, BUTTON_HEIGHT);
        return addButton(text, pos[0], pos[1]);
    }

    public JButton addButton(String text, int x, int y) {
        JButton button = new JButton(text);
        this.add(button);
        button.setBounds(x, y, text.getBytes().length * 6 + 25, BUTTON_HEIGHT);
        return button;
    }

    public JComboBox addComboBox(Object[] values, Component comp) {
        int[] pos = getPosition(comp, TEXT_FIELD_HEIGHT);
        return addComboBox(values, pos[0], pos[1]);
    }

    public JComboBox addComboBox(Object[] values, int x, int y) {
        JComboBox combo = new JComboBox(values);
        this.add(combo);
        int length = 0;
        for (int i = 0; i < values.length; i++) {
            int l = values[i].toString().getBytes().length;
            if (l > length) {
                length = l;
            }
        }
        combo.setBounds(x, y, length * 6 + 26, TEXT_FIELD_HEIGHT);
        return combo;
    }
    
    public JRadioButton addRadioButton(String text, Component comp) {
    	int[] pos = getPosition(comp, BUTTON_HEIGHT);
    	return addRadioButton(text, pos[0], pos[1]);
    }
    
    public JRadioButton addRadioButton(String text, int x, int y) {
    	JRadioButton b = new JRadioButton(text);
    	this.add(b);
    	b.setBounds(x, y, text.getBytes().length * 6 + 25, BUTTON_HEIGHT);
    	return b;
    }

    public ImagePanel addImage(String imgPath, int x, int y, int width, int height) {
        ImagePanel panel = new ImagePanel(imgPath);
        this.add(panel);
        panel.setBounds(x, y, width, height);
        return panel;
    }

    private int[] getPosition(Component comp, int height) {
        Rectangle r = comp.getBounds();
        int x = r.x + r.width + 6;
        int y = r.y + r.height / 2 - height / 2;
        return new int[] { x, y };
    }

}
