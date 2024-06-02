package com.kitty.radar.listener;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.kitty.component.gui.domain.Option;
import com.kitty.radar.MapOverlay;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.RadarParams;
import com.kitty.radar.business.cr.CR;
import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.gui.BackgroundDialog;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.gui.PropsDialog;
import com.kitty.radar.gui.ResolutionDialog;
import com.kitty.radar.gui.SiteInfoDialog;
import com.kitty.radar.gui.TianQingInfoDialog;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.RadarUtils;

public class PropsActionHandler extends AbstractAction { 
    private PropsDialog dialog;

    public PropsActionHandler(PropsDialog dialog) {
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command == null || command.trim().equals("") || CommonProps.AC_CANCEL.equals(command)) {
            dialog.dispose();
        } else {
        	if (dialog instanceof TianQingInfoDialog) {
        		TianQingInfoDialog d = (TianQingInfoDialog) dialog;              
                RadarParams.userId = d.getTextuserId().getText();
                RadarParams.pw = d.getTextpw().getText();
                RadarParams.radarSavePath = d.getTextpath().getText();
//                MapOverlay.update = true;
                RadarProcessor.displayFile(null);
        	} else if (dialog instanceof SiteInfoDialog) {
                SiteInfoDialog d = (SiteInfoDialog) dialog;
                RadarBase RadarBase = GUIManager.activeMainPanel.getRadarBase();
                RadarBase.radarName = d.getTextRadarName().getText();
                RadarBase.setLongitude(Double.parseDouble(d.getTextLongitude().getText()));
                RadarBase.setLatitude(Double.parseDouble(d.getTextLatitude().getText()));
                RadarBase
                        .setRadarFormat(((Option) d.getComboFormat().getSelectedItem()).getValue());
                RadarParams.timerRate = Float.parseFloat(d.getTextTimerRate().getText());
                RadarParams.enableDelete = d.getEnableDelete().isSelected();
                RadarParams.deleteDays = Integer.parseInt(d.getTextDeleteDays().getText());
                CR.setResolution(CR.getResolution());
                VIL.setResolution(VIL.getResolution());
                Radar.processor.startDeleteTimer();
               // MapOverlay.update = true;
                RadarProcessor.displayFile(null);
            } else if (dialog instanceof BackgroundDialog) {
                BackgroundDialog d = (BackgroundDialog) dialog;
                MapOverlay.polar_grid_spoke = Float.parseFloat(d.getTextSpoke().getText());
                MapOverlay.polar_grid_ring = Float.parseFloat(d.getTextRing().getText());
                byte mode = 0;
                if (d.getCheckProvince().isSelected()) {
                    mode += MapOverlay.MAP_PROVINCE;
                }
                if (d.getCheckCity().isSelected()) {
                    mode += MapOverlay.MAP_CITY;
                }
                if (d.getCheckTown().isSelected()) {
                    mode += MapOverlay.MAP_TOWN;
                }
                if (d.getCheckRiver().isSelected()) {
                    mode += MapOverlay.MAP_RIVER;
                }
                if (d.getCheckTownName().isSelected()) {
                    mode += MapOverlay.MAP_TOWNNAME;
                }
                if (d.getCheckDetailName().isSelected()) {
                    mode += MapOverlay.MAP_DETAILNAME;
                }
                MapOverlay.mapMode = mode;
                GUIManager.updateComponentsAll();;
            } else if (dialog instanceof ResolutionDialog) {
                ResolutionDialog d = (ResolutionDialog) dialog;
                CR.setResolution((byte) ((Option) d.getComboCR().getSelectedItem()).getValue());
                VIL.setResolution((byte) ((Option) d.getComboVIL().getSelectedItem()).getValue());
                GUIManager.updateComponentsAll();;
            }
            if (CommonProps.AC_CONFIRM.equals(command)) {
                dialog.dispose();
            } else if (CommonProps.AC_APPLY.equals(command)) {
                dialog.setApplyEnable(false);
            }
        }
    }

}
