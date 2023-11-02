package com.kitty.radar.util;

import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.kitty.radar.MapOverlay;
import com.kitty.radar.RHI;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.RadarParams;
import com.kitty.radar.business.area.Area;
import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.business.area.CircleArea;
import com.kitty.radar.business.area.EllipseArea;
import com.kitty.radar.business.area.RectangleArea;
import com.kitty.radar.business.cr.CR;
import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.gui.ExportSetDialog;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.WelcomeDialog;

import javax.swing.*;

public class ConfigInfo {

    public static void readConfigInfo() {
        PageFormat pf = PrinterJob.getPrinterJob().defaultPage();
        File file = new File(CommonUtils.getUserHomeDir(), "radarsc_cfg.properties");
        if (file.exists()) {
            InputStream is = null;
            try {
                Properties props = new Properties();
                is = new FileInputStream(file);
                props.load(is);
                Radar.showRightPanel = Boolean.parseBoolean(props.getProperty("view.control.bar"));
//                Radar.showStatus = Boolean.parseBoolean(props.getProperty("view.status.bar"));
//                Radar.showToolBar = Boolean.parseBoolean(props.getProperty("view.tool.bar"));
                GUIManager.syncCut = Boolean.parseBoolean(props.getProperty("sync.cut"));
                GUIManager.syncTime = Boolean.parseBoolean(props.getProperty("sync.time"));
                GUIManager.syncTool = Boolean.parseBoolean(props.getProperty("sync.tool"));
                RadarParams.enableDelete = Boolean.parseBoolean(props.getProperty("auto.delete"));
                RadarParams.deleteDays = Integer.parseInt(props.getProperty("delete.days"));
                RadarBase.latitude = Double.parseDouble(props.getProperty("latitude"));
                RadarBase.longitude = Double.parseDouble(props.getProperty("longitude"));
                RadarBase.radarName = props.getProperty("radar.name");
                RadarBase.level = Float.parseFloat(props.getProperty("level"));
                RadarBase.setRadarFormat(Byte.parseByte(props.getProperty("radar.format")));
                MapOverlay.grid_on = Boolean.parseBoolean(props.getProperty("grid.on"));
                MapOverlay.map_on = Boolean.parseBoolean(props.getProperty("map.on"));
                MapOverlay.point_on = Boolean.parseBoolean(props.getProperty("point.on"));
                MapOverlay.track_on = Boolean.parseBoolean(props.getProperty("track.on"));
                RadarParams.timerRate = Float.parseFloat(props.getProperty("timer.rate"));
                RadarParams.filePath = props.getProperty("file.path");
                RadarParams.lastSavePath = props.getProperty("last.save.path");
                MapOverlay.mapMode = Byte.parseByte(props.getProperty("map.mode"));
//                RadarBase.minimum_r = Float.parseFloat(props.getProperty("minimum.r"));
//                RadarBase.maximum_r = Float.parseFloat(props.getProperty("maximum.r"));
//                RadarBase.minimum_v = Float.parseFloat(props.getProperty("minimum.v"));
//                RadarBase.maximum_v = Float.parseFloat(props.getProperty("maximum.v"));
//                RadarBase.minimum_w = Float.parseFloat(props.getProperty("minimum.w"));
//                RadarBase.maximum_w = Float.parseFloat(props.getProperty("maximum.w"));
//                RadarBase.minimum_t = Float.parseFloat(props.getProperty("minimum.t"));
//                RadarBase.maximum_t = Float.parseFloat(props.getProperty("maximum.t"));
//                RadarBase.minimum_l = Float.parseFloat(props.getProperty("minimum.l"));
//                RadarBase.maximum_l = Float.parseFloat(props.getProperty("maximum.l"));
//                RadarBase.minimum_p = Float.parseFloat(props.getProperty("minimum.p"));
//                RadarBase.maximum_p = Float.parseFloat(props.getProperty("maximum.p"));
                RHI.range_min = Float.parseFloat(props.getProperty("minimum.rhir"));
                RHI.range_max = Float.parseFloat(props.getProperty("maximum.rhir"));
                MapOverlay.polar_grid_spoke = Float.parseFloat(props
                        .getProperty("polar.grid.spoke"));
                MapOverlay.polar_grid_ring = Float.parseFloat(props.getProperty("polar.grid.ring"));
                String value = props.getProperty("print.orientation");
                if (value != null) {
                    pf.setOrientation(Integer.parseInt(value));
                } else {
                    pf.setOrientation(PageFormat.LANDSCAPE);
                }
                String value1 = props.getProperty("print.width");
                String value2 = props.getProperty("print.height");
                String value3 = props.getProperty("print.image.x");
                String value4 = props.getProperty("print.image.y");
                String value5 = props.getProperty("print.image.width");
                String value6 = props.getProperty("print.image.height");
                if (value1 != null && value2 != null && value3 != null && value4 != null
                        && value5 != null && value6 != null) {
                    Paper paper = pf.getPaper();
                    paper.setSize(Double.parseDouble(value1), Double.parseDouble(value2));
                    paper.setImageableArea(Double.parseDouble(value3), Double.parseDouble(value4),
                            Double.parseDouble(value5), Double.parseDouble(value6));
                    pf.setPaper(paper);
                }
                readAreaList(props);
                WelcomeDialog.showWelcome = Boolean.parseBoolean(props.getProperty("show.welcome"));
                RHI.bounds = new Rectangle(Integer.parseInt(props.getProperty("rhi.location.x")),
                        Integer.parseInt(props.getProperty("rhi.location.y")), Integer
                                .parseInt(props.getProperty("rhi.width")), Integer.parseInt(props
                                .getProperty("rhi.height")));
                initVersionParams(props.getProperty("app.version"));
                value = props.getProperty("cr.resolution");
                if (value == null) {
                    value = String.valueOf(CR.getResolution());
                }
                CR.setResolution(Byte.parseByte(value));
                value = props.getProperty("vil.resolution");
                if (value == null) {
                    value = String.valueOf(VIL.getResolution());
                }
                VIL.setResolution(Byte.parseByte(value));
                ExportSetDialog.coordType = Integer.parseInt(props.getProperty("export.coord.type"));
            } catch (Exception e) {


            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            pf.setOrientation(PageFormat.LANDSCAPE);
        }
        RadarParams.pageFormat = pf;
    }

    private static void initVersionParams(String version) {
        if (version == null) {
            RadarParams.filePath = CommonUtils.appPath + "data" + File.separator;
            RadarBase.radarFormat = RadarData.RADAR_FORMAT_SC.getValue();
        }
    }

    public static void writeConfiguration() {
        File file = new File(CommonUtils.getUserHomeDir(), "radarsc_cfg.properties");
        OutputStream os = null;
        try {
            Properties props = new Properties();
            props.setProperty("view.control.bar", String.valueOf(Radar.showRightPanel));
//            props.setProperty("view.status.bar", String.valueOf(Radar.showStatus));
//            props.setProperty("view.tool.bar", String.valueOf(Radar.showToolBar));
            props.setProperty("sync.cut", String.valueOf(GUIManager.isSyncCut()));
            props.setProperty("sync.time", String.valueOf(GUIManager.isSyncTime()));
            props.setProperty("sync.tool", String.valueOf(GUIManager.isSyncTool()));
            props.setProperty("auto.delete", String.valueOf(RadarParams.enableDelete));
            props.setProperty("delete.days", String.valueOf(RadarParams.deleteDays));
            props.setProperty("latitude", String.valueOf(RadarBase.latitude));
            props.setProperty("longitude", String.valueOf(RadarBase.longitude));
            props.setProperty("radar.name", RadarBase.radarName);
            props.setProperty("radar.format", String.valueOf(RadarBase.radarFormat));
            props.setProperty("level", String.valueOf(RadarBase.level));
            props.setProperty("grid.on", String.valueOf(MapOverlay.grid_on));
            props.setProperty("map.on", String.valueOf(MapOverlay.map_on));
            props.setProperty("point.on", String.valueOf(MapOverlay.point_on));
            props.setProperty("track.on", String.valueOf(MapOverlay.track_on));
            props.setProperty("timer.rate", String.valueOf(RadarParams.timerRate));
            props.setProperty("file.path", RadarParams.filePath);
            props.setProperty("last.save.path", RadarParams.lastSavePath);
            props.setProperty("map.mode", String.valueOf(MapOverlay.mapMode));
//            props.setProperty("minimum.r", String.valueOf(RadarBase.minimum_r));
//            props.setProperty("maximum.r", String.valueOf(RadarBase.maximum_r));
//            props.setProperty("minimum.v", String.valueOf(RadarBase.minimum_v));
//            props.setProperty("maximum.v", String.valueOf(RadarBase.maximum_v));
//            props.setProperty("minimum.w", String.valueOf(RadarBase.minimum_w));
//            props.setProperty("maximum.w", String.valueOf(RadarBase.maximum_w));
//            props.setProperty("minimum.t", String.valueOf(RadarBase.minimum_t));
//            props.setProperty("maximum.t", String.valueOf(RadarBase.maximum_t));
//            props.setProperty("minimum.l", String.valueOf(RadarBase.minimum_l));
//            props.setProperty("maximum.l", String.valueOf(RadarBase.maximum_l));
//            props.setProperty("minimum.p", String.valueOf(RadarBase.minimum_p));
//            props.setProperty("maximum.p", String.valueOf(RadarBase.maximum_p));
//            props.setProperty("minimum.rhir", String.valueOf(RHI.range_min));
//            props.setProperty("maximum.rhir", String.valueOf(RHI.range_max));
            props.setProperty("polar.grid.spoke", String.valueOf(MapOverlay.polar_grid_spoke));
            props.setProperty("polar.grid.ring", String.valueOf(MapOverlay.polar_grid_ring));
            props.setProperty("print.orientation", String.valueOf(RadarParams.pageFormat
                    .getOrientation()));
            Paper paper = RadarParams.pageFormat.getPaper();
            props.setProperty("print.width", String.valueOf(paper.getWidth()));
            props.setProperty("print.height", String.valueOf(paper.getHeight()));
            props.setProperty("print.image.x", String.valueOf(paper.getImageableX()));
            props.setProperty("print.image.y", String.valueOf(paper.getImageableY()));
            props.setProperty("print.image.width", String.valueOf(paper.getImageableWidth()));
            props.setProperty("print.image.height", String.valueOf(paper.getImageableHeight()));
            writeAreaList(props);
            props.setProperty("show.welcome", String.valueOf(WelcomeDialog.showWelcome));
            if (RHI.rhi != null) {
                RHI.bounds = CommonUtils.getParentDialog(RHI.rhi).getBounds();
            }
            if (RHI.bounds != null) {
                props.setProperty("rhi.width", String.valueOf(RHI.bounds.width));
                props.setProperty("rhi.height", String.valueOf(RHI.bounds.height));
                props.setProperty("rhi.location.x", String.valueOf(RHI.bounds.x));
                props.setProperty("rhi.location.y", String.valueOf(RHI.bounds.y));
            }
            props.setProperty("app.version", Radar.APP_VERSION);
            props.setProperty("cr.resolution", String.valueOf(CR.getResolution()));
            props.setProperty("vil.resolution", String.valueOf(VIL.getResolution()));
            props.setProperty("export.coord.type", String.valueOf(ExportSetDialog.coordType));
            os = new FileOutputStream(file);
            props.store(os, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void readAreaList(Properties props) {
        String value = props.getProperty("area.list");
//        if (value != null) {
//            String[] v = value.split(";");
//            for (int i = 0; i < v.length; i++) {
//                if (v[i].startsWith("C")) {
//                    CircleArea area = new CircleArea();
//                    area.deSerialize(v[i]);
//                    AreaDialog.tableModel.add(area, -1);
//                } else if (v[i].startsWith("R")) {
//                    RectangleArea area = new RectangleArea();
//                    area.deSerialize(v[i]);
//                    AreaDialog.tableModel.add(area, -1);
//                } else if (v[i].startsWith("E")) {
//                    EllipseArea area = new EllipseArea();
//                    area.deSerialize(v[i]);
//                    AreaDialog.tableModel.add(area, -1);
//                }
//            }
//        }
    }

    private static void writeAreaList(Properties props) {
        List list = AreaDialog.tableModel.getAreaList();
        if (list.size() > 0) {
            StringBuffer sb = new StringBuffer();
            Iterator areas = list.iterator();
            boolean first = true;
            while (areas.hasNext()) {
                if (!first) {
                    sb.append(";");
                    first = false;
                }
                sb.append(((Area) areas.next()).serialize());
            }
            props.setProperty("area.list", sb.toString());
        }
    }

}
