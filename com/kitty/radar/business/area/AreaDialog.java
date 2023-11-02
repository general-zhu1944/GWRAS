package com.kitty.radar.business.area;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.kitty.component.gui.BasicDialog;
import com.kitty.radar.gui.ImportAreaDialog;
import com.kitty.radar.util.CommonUtils;

/**
 * 区域列表对话框。
 */
public class AreaDialog extends BasicDialog {

    public static AreaTableModel tableModel = new AreaTableModel();

    private JTable table = new JTable(tableModel);

    public AreaDialog() {
        super("区域列表", true);
        this.setCenterSize(509, 300);
        this.mainPanel.setLayout(new BorderLayout());
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
        TableColumnModel model = table.getColumnModel();
        model.getColumn(0).setPreferredWidth(50);
        model.getColumn(1).setPreferredWidth(400);
        model.getColumn(2).setPreferredWidth(50);
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table.getTableHeader()
                .getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        model.getColumn(0).setCellRenderer(renderer);
        this.mainPanel.add(new JScrollPane(table));
        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    int[] rows = table.getSelectedRows();
                    if (rows.length == 1) {
                        new AreaInputDialog(AreaDialog.this, "修改区域", rows[0]);
                    }
                }
            }
        });
        buttonPanel.remove(this.confirmButton);
        this.setButtonHgap(5);
        JButton button = new JButton(" 新建(N) ");
        button.setMnemonic(KeyEvent.VK_N);
        button.setActionCommand("new");
        button.addActionListener(this);
        buttonPanel.add(button, 0);
        button = new JButton(" 修改(M) ");
        button.setMnemonic(KeyEvent.VK_M);
        button.setActionCommand("modify");
        button.addActionListener(this);
        buttonPanel.add(button, 1);
        button = new JButton(" 删除(D) ");
        button.setMnemonic(KeyEvent.VK_D);
        button.setActionCommand("delete");
        button.addActionListener(this);
        buttonPanel.add(button, 2);
//        button = new JButton(" 导入(I) ");
//        button.setMnemonic(KeyEvent.VK_I);
//        button.setActionCommand("import");
//        button.addActionListener(this);
//        buttonPanel.add(button, 3);
        cancelButton.setText(" 关闭(C) ");
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("new".equals(command)) {
            new AreaInputDialog(this, "新建区域", -1);
        } else if ("modify".equals(command)) {
            int[] rows = table.getSelectedRows();
            if (rows.length == 1) {
                new AreaInputDialog(this, "修改区域", rows[0]);
            } else if (rows.length > 1) {
                CommonUtils.alert("请选择一个要修改的区域", null);
            } else {
                CommonUtils.alert("请选择要修改的区域", null);
            }
        } else if ("import".equals(command)) {
            new ImportAreaDialog(this);
        } else {
            int[] rows = table.getSelectedRows();
            if (rows.length > 0) {
                int flag = JOptionPane.showConfirmDialog(this, "确定删除选中的区域吗？", "",
                        JOptionPane.YES_NO_OPTION);
                if (flag == JOptionPane.OK_OPTION) {
                    tableModel.remove(rows);
                }
            } else {
                CommonUtils.alert("请选择要删除的区域", null);
            }
        }
    }

}
