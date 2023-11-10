package com.kitty.radar.business.tops;

import java.awt.Color;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;

import javax.swing.JOptionPane;

import com.kitty.radar.RadarBase;
import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.color.RadarColor;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.GridValue;
import com.kitty.radar.domain.ResolutionOption;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class TOPS extends RadarBase {
    
    private static byte resolution = 1;

    private static float gridWidth = 1f; // 单元格宽度，单位：km
    
    private static float range = 150;
    private static NavigableMap map_old;
    private static NavigableMap map;
    private static String filetime;//用于判断当前雷达文件是否一致，一致就不用重新读取数据计算

    public static void display(Graphics2D g,RadarBase radarBase) {
        Instant start = Instant.now();

        radarBase.datas = null;
        if (radarBase.l2 == null) { // 没有选中的文件
            return;
        }
        int w = PositionUtils.toLength(gridWidth,radarBase);
        int hw = w / 2;     
        GridValue[][] grids = RadarUtils.getGridsXY(w, range,radarBase);
        RadarData rd = radarBase.l2;
        int moment = RadarData.DBZ;
        double binInterval = rd.getBinInterval(moment);
        double rangeToFirst = rd.getRangeToFirstBin(moment);
        if(!radarBase.l2.getFileTime().toString().equals(filetime))//避免同一文件多次读取数据
        {
         map = rd.readFile(moment);
         map_old=map;
         filetime=radarBase.l2.getFileTime().toString();
      //   JOptionPane.showMessageDialog(null, "消息提示tjjjjt：");
        }
        else
        {
        	map=map_old;
        }

        Map cache = new HashMap();
        for (int i = 0; i <grids.length ; i++) {
            for (int j = 0; j <grids[i].length ; j++) {
                if (grids[i][j] != null) {
                    ARCoord c = PositionUtils.toARCoord(grids[i][j].x, grids[i][j].y);                  
                    List<String> list = new ArrayList<>(map.keySet());
                    Collections.reverse(list);
                    ListIterator keys=list.listIterator();
                //   Iterator keys2 = map.keySet().iterator();
                    while (keys.hasNext()) {
                        Double e = (Double) keys.next();
                        Double cos = (Double) cache.get(e);
                        if (cos == null) {
                            cos = new Double(Math.cos(e.doubleValue() * Math.PI / 180.0));
                            cache.put(e, cos);
                        }
                        double range = PositionUtils.toRange2(c.r, cos.doubleValue());
                        short value = rd.getPointValue4I(map.get(e), c.azimuth, range, binInterval,
                                rangeToFirst);
                        
                        double dbz= Math.round(value/2-  33);   
                        
                        if(dbz > 20.5)
                        {
                        	double height=PositionUtils.getHeightNew(range,e);
                            grids[i][j].setDoubleValue(height);
                            grids[i][j].setARCoord(c);
                            break;
                        }                                              
                    }
                }
            }
        }

        radarBase.datas = grids;

        // 画出格点
		RadarColor radarColor = RadarUtils.getRadarColor(radarBase.currentMoment);
		Color[] colors = radarColor.getColors();
		float[] cvalues = radarColor.getColorValues();
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids[i].length; j++) {
				if (grids[i][j] != null) {
					double v = grids[i][j].getDoubleValue();
					for (int k = 0; k < cvalues.length; k++) {
						if (k != cvalues.length - 1) {
							if (v >= cvalues[k] && v < cvalues[k + 1]) {
								g.setColor(colors[k]);
								g.fillRect(grids[i][j].x - hw, grids[i][j].y
										- hw, w, w);
							}
						} else {
							if (v >= cvalues[k]) {
								g.setColor(colors[k]);
								g.fillRect(grids[i][j].x - hw, grids[i][j].y
										- hw, w, w);
							}
						}
					}
				}
			}
		}
        Duration duration = Duration.between(start, Instant.now());
       // JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+duration.toMillis());
    }

    public static float getPointValue(int x, int y,RadarBase radarBase) {
        if (radarBase.datas != null) {
            GridValue[][] grids = (GridValue[][])radarBase.datas;
            int w = PositionUtils.toLength(gridWidth,radarBase);
            int i = (int) Math.floor((x + radarBase.xoffset - radarBase.center_X) / (double) w)
                    + grids.length / 2;
            int j = (int) Math.floor((y + radarBase.yoffset - radarBase.center_Y) / (double) w)
                    + grids.length / 2;
            if (i >= 0 && j >= 0 && i < grids.length && j < grids[i].length && grids[i][j] != null) {
                return (float)grids[i][j].getDoubleValue();
            }
        }
        return RadarData.NO_DATA;
    }

    public static void setResolution(byte resolution) {
        TOPS.resolution = resolution;
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

}
