package com.kitty.radar;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Set;
import com.kitty.radar.color.ElevationColor;
import com.kitty.radar.color.RadarColor;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.color.RadarColor;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.PositionUtils;
import org.apache.commons.io.FileUtils;

import com.kitty.radar.business.area.Area;
import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.RadarUtils;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import ucar.nc2.NetcdfFileWriter.Version;
import javax.swing.*;

public class MapOverlay  {

    public BufferedImage image;

    public boolean update = true;
    // ===================== Line Variables ========================

    public static boolean line_on = false;
    // ===================== Grid Variables ========================

    public static boolean grid_on = true;

    public static float polar_grid_spoke = 30;

    public static float polar_grid_ring = 50;
    // ===================== Elevation Variables ========================

    public static boolean elevation_on = true;

    public static double[] dateLon;

    public static double[] dateLat;

    public static short[][] elevationArr;

    // ===================== Point Variables ========================

    public static boolean point_on = false;

    public static String pointFile = CommonUtils.appPath + "station.txt";

    // ===================== Track Variables ========================

    public static boolean track_on = false;

    public static String trackFile = CommonUtils.appPath + "track.txt";

    // ===================== Map Variables ========================

    public static boolean map_on = true;

    public static final Color COLOR_MAP_PROVINCE = new Color(255, 255, 255); // 白色

    public static final Color COLOR_MAP_CITY = new Color(0, 122, 122); // 暗青色

    public static final Color COLOR_MAP_TOWN = new Color(122, 122, 122); // 灰色

    public static final Color COLOR_MAP_RIVER = new Color(0, 0, 255); // 蓝色

    public static final Color COLOR_MAP_TOWN_NAME = new Color(230, 230, 230); // 灰色

    public static final byte MAP_PROVINCE = 1;

    public static final byte MAP_CITY = 2;

    public static final byte MAP_TOWN = 4;

    public static final byte MAP_RIVER = 8;

    public static final byte MAP_TOWNNAME = 16;

    public static final byte MAP_DETAILNAME = 32; // 是否显示详细的地名，该值为true并且放大4倍以上时显示

    public static byte mapMode = 49;

    public static String mapFile =  "D:\\province.map"; //CommonUtils.appPath +
    public static String elevationFile="D:\\gebco_2022_n35.0_s25.0_w100.0_e110.0.nc";
    private RadarBase radarBase;

	private static byte[] mapFileBytes = null;
  //  private static byte[] elevationFileBytes = null;
    public static NetcdfFile ofile; // 保存地形数据
	static {
        File f = new File(mapFile);
        if (f.exists()) {
        	try {
				mapFileBytes = FileUtils.readFileToByteArray(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
	}

    static {
        File f = new File(elevationFile);
        if (f.exists()) {
            try {
                ofile=NetcdfFile.open(elevationFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    
    public MapOverlay(RadarBase radarBase) {
    	this.radarBase = radarBase;
	}

	public BufferedImage drawMap() {
        if (image == null) {
            image = new BufferedImage(this.radarBase.width, this.radarBase.height, BufferedImage.TYPE_INT_ARGB);
            update = true;
        }
        if (update) {
            Graphics2D g = (Graphics2D) image.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            g.fillRect(0, 0, this.radarBase.width, this.radarBase.height);
            g.dispose();

            try {
                if (map_on) {
                    displayMap();
                }
                if (grid_on) {
                    displayGrid();
                }
              
                displayArea();

                update = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
    public BufferedImage drawTerrain(BufferedImage image2) {
        try {
            if (elevation_on) {
                displayElevation(image2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image2;
    }

    private void displayArea() {
        Graphics2D g = (Graphics2D) image.createGraphics();
        Iterator areas = AreaDialog.tableModel.getAreaList().iterator();
        while (areas.hasNext()) {
            ((Area) areas.next()).display(g);
        }
        g.dispose();
    }

    private void displayElevation(BufferedImage image2) {
        Graphics2D g = (Graphics2D) image2.createGraphics();

        try {
            if( ofile!=null) {

                //System.out.println(ofile.getVariables());
                //获取经度
                Variable lon = ofile.findVariable("lon");
                double[] dateLon = (double[]) lon.read().copyTo1DJavaArray();
                //获取纬度
                Variable lat = ofile.findVariable("lat");
                double[] dateLat = (double[]) lat.read().copyTo1DJavaArray();
                //获取地形高度变量
                Variable height = ofile.findVariable("elevation");
                //获取经纬度数组的长度
                int dateLonL = dateLon.length;
                int dateLatL = dateLat.length;
                //将地形高度变量放到二维数组中
                short[][] heightArr = (short[][]) height.read().copyToNDJavaArray();
                //double m_RadLo =dateLon[dateLonL-1] * Math.PI / 180.0;
                double m_RadLa = dateLat[dateLatL - 1] * Math.PI / 180.0;
                //double am_RadLo = dateLon[0] * Math.PI / 180.0;
                double am_RadLa = dateLat[0] * Math.PI / 180.0;
                double aEc = CommonProps.RJ + (CommonProps.RC - CommonProps.RJ)
                        * (90 - RadarBase.latitude) / 90.0;
                //double aEd = aEc * Math.cos(am_RadLa);
                //double dx = (m_RadLo - am_RadLo) * aEd;
                double dy = (m_RadLa - am_RadLa) * aEc;
                double gridWidth = (dy) / dateLatL;
                int w = PositionUtils.toLength(gridWidth / 1000,GUIManager.activeMainPanel.getRadarBase()) * 5;//像素单元格宽度
                int hw = w / 2;
                RadarColor radarColor = ElevationColor.color;
                Color[] colors = radarColor.getColors();
                float[] cvalues = radarColor.getColorValues();
                XYCoord raincoord = null;

                for (int i = 0; i < heightArr.length; i = i + 4) {
                    for (int j = 0; j < heightArr[i].length; j = j + 4) {
                        double v = heightArr[i][j];
                        for (int k = 0; k < cvalues.length; k++) {
                            if (k != cvalues.length - 1) {
                                if (v >= cvalues[k] && v < cvalues[k + 1]) {
                                    g.setColor(colors[k]);
                                    raincoord = PositionUtils.toXYCoord2(dateLon[j], dateLat[i],GUIManager.activeMainPanel.getRadarBase());
                                    g.fillRect(raincoord.x - hw, raincoord.y
                                            - hw, w, w);
                                }
                            } else {
                                if (v >= cvalues[k]) {
                                    g.setColor(colors[k]);
                                    g.fillRect(raincoord.x - hw, raincoord.y
                                            - hw, w, w);
                                }
                            }
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void displayGrid() {
        Graphics2D g = (Graphics2D) image.createGraphics();
        int pixel = this.radarBase.center_X - this.radarBase.xoffset;
        int scanl = this.radarBase.center_Y - this.radarBase.yoffset;
        g.setPaint(new Color(214, 177, 159));

        for (double x = polar_grid_ring; x <= RadarBase.radius; x += polar_grid_ring) {
            int r = (int) Math.round(x * this.radarBase.scale_X);
            int d = 2 * r;
            g.drawOval(pixel - r, scanl - r, d, d);
            String num = CommonUtils.format(x, 0);
            int length = 3 - num.length();
            for (int i = 0; i < length; i++) {
                num = "  " + num;
            }
            g.drawString(num, pixel - 22, scanl - r - 3);
        }

        for (double x = 0; x < 360.0; x += polar_grid_spoke) {
            g.drawLine(pixel, scanl, (int) Math.round(RadarBase.radius * RadarUtils.cos(x)
                    * this.radarBase.scale_X + pixel), (int) Math.round(RadarBase.radius * RadarUtils.sin(x)
                    * this.radarBase.scale_Y + scanl));
        }
        g.dispose();
    }

    private void displayMap() {
//        File f = new File(mapFile);
        if (null != mapFileBytes) {
            Graphics2D g = (Graphics2D) image.createGraphics();
            int mapRadius = (int) Math.round( 460* this.radarBase.scale_X); // 地图半径为150km RadarUtils.getRadarRadius()
            int pixel = this.radarBase.center_X - this.radarBase.xoffset - mapRadius;
            int scanl = this.radarBase.center_Y - this.radarBase.yoffset - mapRadius;
            double mapTimes = 1.0 / 8.0; // 地图数据放大了8倍
//            RandomAccessFile file = null;
            DataInputStream file = null;

            try {
//            	fileBytes = FileUtils.readFileToByteArray(f);
            	ByteArrayInputStream s = new ByteArrayInputStream(mapFileBytes);
            	file = new DataInputStream(s);
                file.skipBytes(4);
//                file = new RandomAccessFile(f, "r");
//                file.seek(4); // 跳过开头4字节位置
                MapHead[] mapHead = new MapHead[5];
                for (int i = 0; i < 5; i++) {
                    mapHead[i] = new MapHead();
                    mapHead[i].mapID = file.readUnsignedShort();
                    mapHead[i].flag1 = file.readUnsignedShort();
                    mapHead[i].flag2 = file.readUnsignedShort();
                    mapHead[i].length = ((int) file.readUnsignedByte() << 24)
                            + ((int) file.readUnsignedByte() << 16)
                            + ((int) file.readUnsignedByte() << 8) + file.readUnsignedByte();
                    mapHead[i].startpos = (int) file.readUnsignedShort() << 8;
                	file.read(mapHead[i].unused, 0, 202);
//                    for (int j = 0; j < 202; j++) {
//                        mapHead[i].unused[j] = file.readByte();
//                    }
                }

                // 5种地图
                for (int i = 0; i < 5; i++) {
                    if (mapHead[i].mapID == 156) {
                        if ((mapMode & MAP_PROVINCE) != 0) {
                            g.setPaint(COLOR_MAP_PROVINCE);
                            displayMapMsg(g, mapFileBytes, mapHead[i], mapTimes, pixel, scanl);
                        }
                    } else if (mapHead[i].mapID == 198) {
                        if ((mapMode & MAP_CITY) != 0) {
                            g.setPaint(COLOR_MAP_CITY);
                            displayMapMsg(g, mapFileBytes, mapHead[i], mapTimes, pixel, scanl);
                        }
                    } else if (mapHead[i].mapID == 152) {
                        if ((mapMode & MAP_TOWN) != 0) {
                            g.setPaint(COLOR_MAP_TOWN);
                            displayMapMsg(g, mapFileBytes, mapHead[i], mapTimes, pixel, scanl);
                        }
                    } else if (mapHead[i].mapID == 144) {
                        if ((mapMode & MAP_RIVER) != 0) {
                            g.setPaint(COLOR_MAP_RIVER);
                            displayMapMsg(g, mapFileBytes, mapHead[i], mapTimes, pixel, scanl);
                        }
                    } else if (mapHead[i].mapID == 196) {
                        if ((mapMode & MAP_TOWNNAME) != 0 || (mapMode & MAP_DETAILNAME) != 0) {
                            g.setPaint(COLOR_MAP_TOWN_NAME);
                            displayMapName(g, mapFileBytes, mapHead[i], mapTimes, pixel, scanl);
                        }
                    }
                }
            } catch (EOFException e) {

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            g.dispose();
        }
    }

    private void displayMapMsg(Graphics2D g, byte[] fileBytes, MapHead mapHead,
            double mapTimes, int pixel, int scanl) throws IOException {
//        file.skipBytes(mapHead.startpos);
    	byte[] databyte = new byte[mapHead.length];
    	System.arraycopy(fileBytes, mapHead.startpos, databyte, 0, databyte.length);
    	DataInputStream file = new DataInputStream(new ByteArrayInputStream(databyte));
//    	file.seek(mapHead.startpos);
        int length = mapHead.startpos + mapHead.length;
        int x0, y0, x1, y1, msgLength;
        MapMsgHead mapMsgHead = new MapMsgHead();
        int readLen = 0;
//        while (file.getFilePointer() < length) {
      while (readLen < mapHead.length) {
            mapMsgHead.flag1 = file.readUnsignedShort();	//2
            readLen += 2;
            if (mapMsgHead.flag1 != 3619) {
                continue;
            }
            mapMsgHead.flag2 = file.readUnsignedShort();	//2
            readLen += 2;
            mapMsgHead.x1 = file.readShort();				//2
            mapMsgHead.y1 = file.readShort();				//2
            mapMsgHead.bytes = file.readUnsignedShort();	//2
            readLen += 6;

            x0 = (int) Math.round(mapMsgHead.x1 * mapTimes * this.radarBase.scale_X) + pixel;
            y0 = (int) Math.round(mapMsgHead.y1 * mapTimes * this.radarBase.scale_X) + scanl;
            msgLength = mapMsgHead.bytes / 4;
            for (int j = 0; j < msgLength; j++) {
                x1 = (int) Math.round(file.readShort() * mapTimes * this.radarBase.scale_X) + pixel;	//2
                y1 = (int) Math.round(file.readShort() * mapTimes * this.radarBase.scale_X) + scanl;	//2
                readLen += 4;
                g.drawLine(x0, y0, x1, y1);
                x0 = x1;
                y0 = y1;
            }
        }
    }

    private void displayMapName(Graphics2D g, byte[] fileBytes, MapHead mapHead,
            double mapTimes, int pixel, int scanl) throws IOException {
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHints(qualityHints);
        

    	byte[] databyte = new byte[mapHead.length];
    	System.arraycopy(fileBytes, mapHead.startpos, databyte, 0, databyte.length);
    	DataInputStream file = new DataInputStream(new ByteArrayInputStream(databyte));
//        file.seek(mapHead.startpos);
        int length = mapHead.startpos + mapHead.length;
        int x0, y0;
        int r = 2;
        int d = 2 * r;
        Set nameSet = new HashSet(); // 过滤掉重复的地名
        MapTownName townName = new MapTownName();
        boolean draw = ((mapMode & MAP_TOWNNAME) != 0 ? true : false);
        int readLen = 0;
//        while (file.getFilePointer() < length) {
        while (readLen < mapHead.length) {
            townName.flag1 = file.readUnsignedShort();	//2
            townName.flag2 = file.readUnsignedShort();	//2
            townName.flag3 = file.readUnsignedShort();	//2
            townName.x1 = file.readShort();	//2
            townName.y1 = file.readShort();	//2
            townName.bytes = file.readUnsignedShort();	//2
            readLen += 12;
            if (townName.bytes != 6) {
                if (this.radarBase.zoom >= 4 && (mapMode & MAP_DETAILNAME) != 0) {
                    draw = true;
                    continue;
                } else {
                    break;
                }
            }
            for (int i = 0; i < townName.bytes; i++) {
                townName.name[i] = file.readByte();	//1
            }
            readLen += townName.bytes;
            String name = new String(townName.name, "GBK").trim();
            if (draw) {
                x0 = (int) Math.round(townName.x1 * mapTimes * this.radarBase.scale_X) + pixel;
                y0 = (int) Math.round(townName.y1 * mapTimes * this.radarBase.scale_X) + scanl;
                if (!nameSet.contains(name)) {
                    g.drawOval(x0 - r, y0 - r, d, d);
                    g.drawString(name, x0 + 5, y0 + 4);
                    nameSet.add(name);
                }
            } else {
                nameSet.add(name);
            }
        }
    }

    // Cinrad地图结构
    private class MapMsgHead { // 地图信息头

        int flag1; // 标志位1

        int flag2; // 标志位2

        short x1; // 起始点x

        short y1; // 起始点y

        int bytes; // 字节数

    }

    private class MapTownName { // 地名数据

        int flag1; // 标志位1

        int flag2; // 标志位2

        int flag3; // 标志位3

        short x1; // 起始点x

        short y1; // 起始点y

        int bytes; // 字节数

        byte[] name = new byte[6];

    }

    private class MapHead { // 地图文件头

        int mapID; // 地图的类别ID

        int flag1; // 标志位1 (00或01)

        int flag2; // 标志位2 (01或17)

        int length;

        int startpos; // 数据偏移量

        byte[] unused = new byte[202]; // 未用

    }

}