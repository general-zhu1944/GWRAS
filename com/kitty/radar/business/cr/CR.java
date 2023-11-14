package com.kitty.radar.business.cr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;

import javax.swing.JOptionPane;

import com.kitty.radar.RadarBase;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.GridValue;
import com.kitty.radar.domain.ResolutionOption;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class CR extends RadarBase {
    
    private static byte resolution = 1;

    private static float gridWidth = 1f; // 单元格宽度，单位：km
    private static float range = 230;

    public static void display(Graphics2D g,RadarBase radarBase) {
        radarBase.datas = null;
        RadarData l2 = radarBase.l2;
        if (l2 == null) { // 没有选中的文件
            return;
        }
        int w = PositionUtils.toLength(gridWidth,radarBase);
        int hw = w / 2;
        GridValue[][] grids = RadarUtils.getGridsXY(w, range,radarBase);
        RadarData rd = radarBase.l2;
        int moment = radarBase.active_moment;
        double binInterval = rd.getBinInterval(moment);
        double rangeToFirst = rd.getRangeToFirstBin(moment);
      //  JOptionPane.showMessageDialog(null, "消息提示tjjjjt：");
        Color[] colorCache = RadarUtils.getRadarColor(radarBase.currentMoment).getColorCache();
        NavigableMap map = rd.readFile(moment);
//        GUIManager.toolBarLabel.setText( " 站名 " +RadarBase.radarName + " - 时间 " + RadarUtils.getFileTime() + " - 文件 "
//                + l2.getSrcFileName() + " - " + RadarUtils.getMomentLabel());
        Map cache = new HashMap();
        //JOptionPane.showMessageDialog(null, "消息提示tjjjmmmmmjt："+map.keySet().toString()+"");//0,第一层
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                if (grids[i][j] != null) {
                    ARCoord c = PositionUtils.toARCoord(grids[i][j].x, grids[i][j].y,radarBase);
                    Iterator keys = map.keySet().iterator();                    
                    while (keys.hasNext()) {
                        Double e = (Double) keys.next();
                        Double cos = (Double) cache.get(e);
                        if (cos == null) {
                            cos = Double.valueOf(Math.cos(e.doubleValue() * Math.PI / 180.0));
                            cache.put(e, cos);
                        }
                        double range = PositionUtils.toRange2(c.r, cos.doubleValue());
                        short value = rd.getPointValue4I(map.get(e), c.azimuth, range, binInterval,
                                rangeToFirst);
                        if (value > grids[i][j].getShortValue()) {
                            Color color = colorCache[value];
                            if (color != null) {
                                grids[i][j].setColor(color);
                                grids[i][j].setShortValue(value);
                                grids[i][j].setARCoord(c);
                            }
                        }
                    }
                }
            }
        }
        radarBase.datas = grids;

        // 画出格点
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                if (grids[i][j] != null) {
                    Color color = grids[i][j].getColor();
                    if (color != null) {
                        g.setColor(color);
                        g.fillRect(grids[i][j].x - hw, grids[i][j].y - hw, w, w);
                    }
                }
            }
        }
    }

    public static float getPointValue(int x, int y,RadarBase radarBase) {
        if (radarBase.datas != null) {
            GridValue[][] grids = (GridValue[][]) radarBase.datas;
            int w = PositionUtils.toLength(gridWidth,radarBase);
            int i = (int) Math.floor((x + radarBase.xoffset - radarBase.center_X) / (double) w)
                    + grids.length / 2;
            int j = (int) Math.floor((y + radarBase.yoffset - radarBase.center_Y) / (double) w)
                    + grids.length / 2;
            if (i >= 0 && j >= 0 && i < grids.length && j < grids[i].length && grids[i][j] != null) {
                return SA_SB.binaryToMoment(grids[i][j].getShortValue(), radarBase.active_moment,
                        RadarBase.resolution);
            }
        }
        return RadarData.NO_DATA;
    }

    public static void setResolution(byte resolution) {
        CR.resolution = resolution;
        ResolutionOption[] options = RadarUtils.getResolution(CommonProps.MOMENT_CR);
        boolean flag = true;
        for (int i = 0; i < options.length; i++) {
            if (options[i].getValue() == resolution) {
                gridWidth = options[i].getResolution();
                range = options[i].getRange();
                flag = false;
                break;
            }
        }
        if (flag) {
            gridWidth = options[0].getResolution();
            range = options[0].getRange();
        }
    }

    public static byte getResolution() {
        return resolution;
    }
    public static String evaLabelText(RadarBase radarBase) {
        if(radarBase.l2 == null)
            return "";
        String label = "时间 " + RadarUtils.getFileTime(radarBase)
//                + " - 文件 " + radarBase.l2.getSrcFileName()
                + " - " + RadarUtils.getMomentLabel(radarBase);
        return label;
    }


}
