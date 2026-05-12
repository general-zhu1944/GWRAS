package com.kitty.radar.business.cappi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.kitty.component.third.TreeMapExt;
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

public class CAPPI extends RadarBase {
    
    private static byte resolution = 1;

    private static float gridWidth = 1f; // 单元格宽度，单位：km
    
    private static float range = 230;
    private static double R = 6371.0 * 1000.0 * 4.0 / 3.0;     // effective radius of earth in meters.
//    private static float h= antennaHeight*1000;
    private static float z=3*1000;
    
    public static List<Double> fixedElevation = new ArrayList<>();
    
    public static void display(Graphics2D g,RadarBase radarBase) { 
    	float h= radarBase.getAntennaHeight()*1000;
    	radarBase.datas = null;
        if (radarBase.l2 == null) { // ??óD???Dμ????t
            return;
        }
        int w = PositionUtils.toLength(gridWidth,radarBase.getScale_X());
        int hw = w / 2;
        GridValue[][] grids = RadarUtils.getGridsXY(w, range,radarBase);
        RadarData rd = radarBase.l2;
        int moment = radarBase.active_moment;
        double binInterval = rd.getBinInterval(moment);
        double rangeToFirst = rd.getRangeToFirstBin(moment);
        Color[] colorCache = RadarUtils.getRadarColor(radarBase.currentMoment, radarBase).getColorCache();
        NavigableMap map = rd.readFile(moment);      
        Iterator it =map.keySet().iterator();
        while(it.hasNext()) {
        	 Object e =it.next(); //取出元素
            fixedElevation.add(Double.parseDouble(e.toString()));
        }

        for (int i = 0; i <grids.length ; i++) {
            for (int j = 0; j < grids[i].length; j++) {
            	 if (grids[i][j] != null) {

                    ARCoord c = PositionUtils.toARCoord(grids[i][j].x, grids[i][j].y,radarBase);
                   // double ranges = Math.sqrt(Math.pow(c.r*1000, 2) + Math.pow(h + z, 2) );
                    double ranges = Math.sqrt(Math.pow(R + h, 2) + Math.pow(R + z, 2) - 2 * (R + h) * (R + z) *Math.cos((c.r)*1000  / R));//斜距
                    double elevation = (Math.acos(((R + h) * (R + h) + ranges * ranges - (R + z) * (R + z)) /(2 * (R + h) * ranges)) - Math.PI / 2) * 180. / Math.PI;//仰角
                    short value=0;
                    int[] scanIdx = getScanIndices(elevation);
                    if (scanIdx[0] < 0) {
                    	value=0;
                    }                    
                    	 int ei1 = scanIdx[0];
                         int ei2 = scanIdx[1];
                         if(ei1!=-1)
                         {
                           float v =  rd.getPointValue4I(map.get(fixedElevation.get(ei1)), c.azimuth, ranges/1000, binInterval,rangeToFirst);                  
                           if (ei2 != ei1) {
                             float v2 = rd.getPointValue4I(map.get(fixedElevation.get(ei2)), c.azimuth, ranges/1000, binInterval,
                                     rangeToFirst);

                             if (Float.isNaN(v)) {
                                 v = v2;
                             } else {
                                 if (!Float.isNaN(v2)) {
                                     v = v +(float)((v2 - v) * (elevation - fixedElevation.get(ei1)) / (fixedElevation.get(ei2) -
                                             fixedElevation.get(ei1)));
                                 }
                             }
                             if (!Float.isNaN(v)) {
                                 value=(short)v;                          
                            }
                         }
                         else
                         {
                         	 if (!Float.isNaN(v)) {
                                 value=(short)v;                           
                                 }                   	 
                         } 

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
            int w = PositionUtils.toLength(gridWidth,radarBase.getScale_X());
            int i = (int) Math.floor((x + radarBase.getXoffset() - radarBase.getCenter_X()) / (double) w)
                    + grids.length / 2;
            int j = (int) Math.floor((y + radarBase.getYoffset() - radarBase.getCenter_Y()) / (double) w)
                    + grids.length / 2;
            if (i >= 0 && j >= 0 && i < grids.length && j < grids[i].length && grids[i][j] != null) {
                return SA_SB.binaryToMoment(grids[i][j].getShortValue(), radarBase.active_moment,
                		radarBase.resolution);
            }
        }
        return RadarData.NO_DATA;
    }

    public static void setResolution(byte resolution) {
        CAPPI.resolution = resolution;
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
    /**
     * Get scan indices
     *
     * @param e Elevation value
     * @return Scan indices - 2 elements
     */
    public  static int[] getScanIndices( double e) {
        if (e < fixedElevation.get(0) || e > fixedElevation.get(fixedElevation.size() - 1)) {
            return new int[]{-1, -1};
        } else if (e == fixedElevation.get(0)) {
            return new int[]{0, 0};
        } else if (e == fixedElevation.get(fixedElevation.size() - 1)) {
            return new int[]{fixedElevation.size() - 1, fixedElevation.size() - 1};
        }

        for (int i = 1; i < fixedElevation.size(); i++) {
            if (e <= fixedElevation.get(i)) {
                return new int[]{i - 1, i};
            }
        }

        return new int[]{-1, -1};
    }
    public static byte getResolution() {
        return resolution;
    }
    public static String evaLabelText(RadarBase radarBase) {
        if(radarBase.l2 == null)
            return "";
        String labelText = "ê±?? " + RadarUtils.getFileTime(radarBase)
//                + " - ???t " + radarBase.l2.getSrcFileName()
                + " - " + RadarUtils.getMomentLabel(radarBase);
        return labelText;
    }

}