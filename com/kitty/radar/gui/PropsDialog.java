package com.kitty.radar.gui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.ui.ValidationPanel;

import com.kitty.radar.listener.PropsActionHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;

public class PropsDialog extends JDialog {

    private PropsActionHandler listener = new PropsActionHandler(this);

    private JButton confirm;

    private JButton cancel;

    private JButton apply;

    private boolean applyEnable = false;

    private Map enableMap = new HashMap();

    public PropsDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        CommonUtils.addEscAction((JComponent) this.getContentPane(), listener);
    }

    protected JPanel createPropsButtons() {
        confirm = new JButton(" 确定(O) ");
        confirm.setMnemonic(KeyEvent.VK_O);
        confirm.addActionListener(listener);
        cancel = new JButton(" 取消(C) ");
        cancel.setMnemonic(KeyEvent.VK_C);
        cancel.addActionListener(listener);
        apply = new JButton(" 应用(A) ");
        apply.setMnemonic(KeyEvent.VK_A);
        apply.addActionListener(listener);
        apply.setEnabled(applyEnable);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        confirm.setActionCommand(CommonProps.AC_CONFIRM);
        cancel.setActionCommand(CommonProps.AC_CANCEL);
        apply.setActionCommand(CommonProps.AC_APPLY);
        buttonPanel.add(confirm);
        buttonPanel.add(cancel);
        buttonPanel.add(apply);
        this.getRootPane().setDefaultButton(confirm);
        return buttonPanel;
    }

    protected void addValidationListener(final ValidationPanel vpanel, final String name) {
        enableMap.put(name, Boolean.TRUE);
        vpanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Problem p = vpanel.getProblem();
                boolean enable = p == null ? true : p.severity() != Severity.FATAL;
                enableMap.put(name, Boolean.valueOf(enable));
                enable = getValidationEnable();
                confirm.setEnabled(enable);
                if (applyEnable) {
                    apply.setEnabled(enable);
                }
            }
        });
    }

    public void setApplyEnable(boolean applyEnable) {
        this.applyEnable = applyEnable;
        if (applyEnable) {
            apply.setEnabled(getValidationEnable());
        } else {
            apply.setEnabled(false);
        }
    }

    private boolean getValidationEnable() {
        Iterator values = enableMap.values().iterator();
        boolean enable = true;
        while (values.hasNext()) {
            Boolean b = (Boolean) values.next();
            if (!b.booleanValue()) {
                enable = false;
                break;
            }
        }
        return enable;
    }

}
