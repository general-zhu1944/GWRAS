package com.kitty.component.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.kitty.radar.Radar;

/**
 * 扩展的文件选择对话框。
 */
public class FileChooser extends JFileChooser {

    public static final byte TYPE_OPEN = 0;

    public static final byte TYPE_SAVE = 1;

    public static FileChooser fc;

    private byte type = 0;

    public synchronized static FileChooser getFileChooser() {
        if (fc == null) {
            fc = new FileChooser();
        }
        fc.setSelectedFile(new File(""));
        fc.setType(FileChooser.TYPE_OPEN);
        return fc;
    }

    /**
     * 如果当前打开的是保存对话框，存在同名文件时提示是否要替换。
     */
    public void approveSelection() {
        if (type == TYPE_SAVE) {
            File file = this.getSelectedFile();
            if (file.exists()) {
                int flag = JOptionPane.showConfirmDialog(this, file.getAbsolutePath()
                        + " 已存在。\r\n要替换它吗？", "", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (flag != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
        super.approveSelection();
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int showDialog(Component parent) throws HeadlessException {
        Cursor cursor = Radar.radar.getCursor();
        Radar.radar.setCursor(Cursor.getDefaultCursor());
        int rtn;
        if (type == TYPE_SAVE) {
            this.setApproveButtonMnemonic(KeyEvent.VK_S);
            rtn = super.showDialog(parent, "保存(S)");
        } else {
            this.setApproveButtonMnemonic(KeyEvent.VK_O);
            rtn = super.showDialog(parent, "打开(O)");
        }
        Radar.radar.setCursor(cursor);
        return rtn;
    }

    /**
     * 按ext指定的扩展名添加文件过滤器。
     * 
     * @param ext
     */
    public void setFileFilter(String ext) {
        this.resetChoosableFileFilters();
        if (ext.indexOf("*.*") != -1) {
            this.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    return true;
                }

                public String getDescription() {
                    return "所有文件 (*.*)";
                }
            });
        }
        if (ext.indexOf("*.xls") != -1) {
            this.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".xls");
                }

                public String getDescription() {
                    return "Excel 文件 (*.xls)";
                }
            });
        }
        if (ext.indexOf("*.txt") != -1) {
            this.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".txt");
                }

                public String getDescription() {
                    return "文本文档 (*.txt)";
                }
            });
        }
        if (ext.indexOf("*.jpg") != -1) {
            this.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg");
                }

                public String getDescription() {
                    return "JPEG (*.jpg, *.jpeg)";
                }
            });
        }
    }

}
