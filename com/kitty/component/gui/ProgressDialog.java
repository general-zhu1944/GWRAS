package com.kitty.component.gui;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import com.kitty.radar.util.CommonUtils;

/**
 * 通用进度条对话框。
 */
public class ProgressDialog extends JDialog {

    private JLabel note;

    private JProgressBar bar;

    private JButton cancel;

    private boolean canceled;

    public ProgressDialog(Window owner, String title, boolean modal, int width, int min, int max) {
        super(owner, title, (modal ? JDialog.DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS));
        this.setLayout(null);
        this.setResizable(false);
        super.setSize(width, 147);
        this.setLocation(CommonUtils.getCenterLocation(this.getSize()));
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        note = new JLabel();
        this.add(note);
        int x = 20;
        int w = width - 6 - x * 2;
        note.setBounds(x, 20, w, BasicPanel.LABEL_HEIGHT);
        bar = new JProgressBar(min, max);
        this.add(bar);
        bar.setBounds(x, 45, w, 19);
        bar.setValue(5);
        cancel = new JButton(" 取消(C) ");
        cancel.setMnemonic(KeyEvent.VK_C);
        Action listener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        };
        cancel.addActionListener(listener);
        this.add(cancel);
        cancel.setBounds((width - 67) / 2, 77, 67, BasicPanel.BUTTON_HEIGHT);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });
        CommonUtils.addEscAction((JComponent) this.getContentPane(), listener);
    }
    
    public void setProgress(int nv, String note) {
        bar.setValue(nv);
        if (note != null) {
            this.note.setText(note);
        }
    }

    private void cancel() {
        cancel.setEnabled(false);
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

}
