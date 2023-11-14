package com.kitty.radar.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import com.kitty.component.third.JDirChooser;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarParams;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.TianQingInfoDialog;

public class SelectDownloadDirHandler extends MouseAdapter {

	 public void mouseClicked(MouseEvent e) {
	        File file = JDirChooser.showDialog(TianQingInfoDialog.dirImage, "浏览文件夹", true, new File("f:\\"), "请选择数据目录");
	        if (file != null) {
	            try {
	            	TianQingInfoDialog.path.setText(file.getCanonicalPath());
	            } catch (Exception e1) {
	            }
	        }
	    }

}
