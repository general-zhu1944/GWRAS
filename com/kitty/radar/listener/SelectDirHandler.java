package com.kitty.radar.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import com.kitty.component.third.JDirChooser;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarParams;
import com.kitty.radar.gui.GUIManager;

public class SelectDirHandler extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
        File file = JDirChooser.showDialog(Radar.radar, "浏览文件夹", true, new File(
                RadarParams.filePath), "请选择数据目录");
        if (file != null) {
            try {
                RadarParams.filePath = file.getCanonicalPath();
                GUIManager.setDirToolTip(RadarParams.filePath);
                Radar.startFileMonitor();
                Radar.processor.startDeleteTimer();
            } catch (Exception e1) {
            }
        }
    }

}
