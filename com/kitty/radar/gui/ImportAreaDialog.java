package com.kitty.radar.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.kitty.component.gui.BasicDialog;
import com.kitty.component.gui.FileChooser;
import com.kitty.radar.MapOverlay;
import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.business.area.CircleArea;
import com.kitty.radar.util.CommonUtils;

public class ImportAreaDialog extends BasicDialog {

    private JTextField textSrcFile;

    private AreaDialog dialog;

    public ImportAreaDialog(AreaDialog dialog) {
        super("导入区域", true);
        this.dialog = dialog;
        this.setCenterSize(411, 170);

        JLabel label = this.mainPanel.addLabel("文件路径", 20, 42);
        textSrcFile = this.mainPanel.addTextField(236, label);
        JButton button = this.mainPanel.addButton("浏览...", textSrcFile);
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                FileChooser fc = FileChooser.getFileChooser();
                fc.setFileFilter("*.*, *.xls");
                fc.setDialogTitle("选择导入文件");
                int rtn = fc.showDialog(ImportAreaDialog.this);
                File file = fc.getSelectedFile();
                if (file != null && rtn == FileChooser.APPROVE_OPTION) {
                    textSrcFile.setText(file.getAbsolutePath());
                }
            }

        });
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String path = textSrcFile.getText();
        if (path.trim().equals("")) {
            CommonUtils.alert("请选择导入文件", textSrcFile);
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            CommonUtils.alert("文件不存在，请确认路径是否正确", textSrcFile);
            return;
        }
        this.dispose();
        importFile(file);
    }

    private void importFile(File file) {
        CommonUtils.showWait();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            for (int i = 0; i < rows; i++) {
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    int cells = row.getPhysicalNumberOfCells();
                    double n1 = -1;
                    double n2 = -1;
                    double n3 = -1;
                    for (int j = 0; j < cells; j++) {
                        HSSFCell cell = row.getCell(j);
                        if (cell != null) {
                            double t = -1;
                            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                try {
                                    t = Double.parseDouble(cell.getStringCellValue());
                                } catch (Exception e1) {
                                }
                            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                t = cell.getNumericCellValue();
                            }
                            if (t > -1) { // 有值
                                if (n1 < 0) {
                                    n1 = t;
                                } else if (n2 < 0) {
                                    n2 = t;
                                } else {
                                    n3 = t;
                                    break;
                                }
                            }
                        }
                    }
                    if (n3 > 0) {

                        AreaDialog.tableModel.add(new CircleArea(n1, n2, n3,GUIManager.activeMainPanel.getRadarBase()), -1);
                    }
                }
            }

            AreaDialog.tableModel.fireTableDataChanged();
            //MapOverlay.update = true;
            GUIManager.repaintAll();
            CommonUtils.hideWait();
            CommonUtils.alert("导入区域成功", null, dialog);
        } catch (Exception e) {
            CommonUtils.hideWait();
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
