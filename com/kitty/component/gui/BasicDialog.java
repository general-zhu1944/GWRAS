package com.kitty.component.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.kitty.radar.Radar;
import com.kitty.radar.listener.DisposeActionHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;

/**
 * 通用对话框基类，包含基本的“确定”、“取消”按钮。
 */
public abstract class BasicDialog extends JDialog implements ActionListener {

    public BasicPanel mainPanel = new BasicPanel();

    public JPanel buttonPanel = new JPanel();

    public JButton confirmButton;

    public JButton cancelButton;

    public BasicDialog(Window owner, String title, boolean modal) {
        super(owner, title, (modal ? JDialog.DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS));
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        DisposeActionHandler handler = new DisposeActionHandler(this);
        CommonUtils.addEscAction((JComponent) this.getContentPane(), handler);
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        this.add(mainPanel, BorderLayout.CENTER);

        FlowLayout layout = (FlowLayout) buttonPanel.getLayout();
        layout.setHgap(30);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        confirmButton = new JButton(" 确定(O) ");
        confirmButton.setMnemonic(KeyEvent.VK_O);
        confirmButton.setActionCommand(CommonProps.AC_CONFIRM);
        confirmButton.addActionListener(this);
        buttonPanel.add(confirmButton);
        this.getRootPane().setDefaultButton(confirmButton);
        cancelButton = new JButton(" 取消(C) ");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(handler);
        buttonPanel.add(cancelButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    public BasicDialog(String title, boolean modal) {
        this(Radar.radar, title, modal);
    }

    public void setCenterSize(int width, int height) {
        super.setSize(width, height);
        this.setLocation(CommonUtils.getCenterLocation(this.getSize()));
    }

    public void setButtonHgap(int gap) {
        ((FlowLayout) buttonPanel.getLayout()).setHgap(gap);
    }

}
