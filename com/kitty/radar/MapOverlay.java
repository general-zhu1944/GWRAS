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
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import javax.imageio.ImageIO;
import javax.swing.*;

public class MapOverlay {

    public BufferedImage image;
    public boolean update = true;

    public static boolean line_on = false;
    public static boolean grid_on = true;
    public static float polar_grid_spoke = 30;
    public static float polar_grid_ring = 50;

    public static boolean elevation_on = true;
    private static final int ELEVATION_SUBSAMPLE = 4;
    public double[] dateLon;
    public double[] dateLat;
    public short[][] elevationArr;
    private boolean elevationLoaded = false;
    private final Object elevationLock = new Object();

    public static boolean point_on = false;
    public static String pointFile = CommonUtils.appPath + "station.txt";
    public static boolean track_on = false;
    public static String trackFile = CommonUtils.appPath + "track.txt";
    public static boolean map_on = true;

    public static final Color COLOR_MAP_PROVINCE = new Color(255, 255, 255);
    public static final Color COLOR_MAP_CITY = new Color(0, 122, 122);
    public static final Color COLOR_MAP_TOWN = new Color(122, 122, 122);
    public static final Color COLOR_MAP_RIVER = new Color(0, 0, 255);
    public static final Color COLOR_MAP_TOWN_NAME = new Color(230, 230, 230);
    public static final byte MAP_PROVINCE = 1;
    public static final byte MAP_CITY = 2;
    public static final byte MAP_TOWN = 4;
    public static final byte MAP_RIVER = 8;
    public static final byte MAP_TOWNNAME = 16;
    public static final byte MAP_DETAILNAME = 32;

    private double latitude = 30.822000;
    private double longitude = 106.078000;
    public static byte mapMode = 49;
    public static String elevationFile = "F:\\projects\\Radar20231021\\gebco_2022_n35.0_s25.0_w100.0_e110.0.nc";
    private RadarBase radarBase;
    private byte[] mapFileBytes = null;

    static {
    }

    public MapOverlay(RadarBase radarBase) {
        this.radarBase = radarBase;
    }

    public BufferedImage drawMap() {
        if (image == null) {
            update = true;
        }
        if (update) {
            image = new BufferedImage(this.radarBase.getWidth(), this.radarBase.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) image.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            g.fillRect(0, 0, this.radarBase.getWidth(), this.radarBase.getHeight());
            g.dispose();
            try {
                if (map_on) {
                    if (getMapFileBytes())
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

    private static final Map<String, byte[]> mapMap = new HashMap<String, byte[]>();

    private boolean getMapFileBytes() {
        String fileKey = "map/" + this.radarBase.siteCode + ".map";
        if (mapMap.containsKey(fileKey)) {
            mapFileBytes = mapMap.get(fileKey);
            return true;
        } else {
            File f = new File(fileKey);
            if (f.exists()) {
                try {
                    mapFileBytes = FileUtils.readFileToByteArray(f);
                    mapMap.put(fileKey, mapFileBytes);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("map file not found:" + this.radarBase.siteCode + ".map");
            }
        }
        return false;
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

    private void loadElevationData() {
        if (elevationLoaded) return;
        synchronized (elevationLock) {
            if (elevationLoaded) return;
            NetcdfFile ncFile = null;
            try {
                File f = new File(elevationFile);
                if (!f.exists()) return;
                ncFile = NetcdfFile.open(elevationFile);
                Variable lonVar = ncFile.findVariable("lon");
                dateLon = (double[]) lonVar.read().copyTo1DJavaArray();
                Variable latVar = ncFile.findVariable("lat");
                dateLat = (double[]) latVar.read().copyTo1DJavaArray();
                int lonLen = dateLon.length;
                int latLen = dateLat.length;
                int step = ELEVATION_SUBSAMPLE;
                Variable heightVar = ncFile.findVariable("elevation");
                int subLatLen = latLen / step;
                int subLonLen = lonLen / step;
                elevationArr = new short[subLatLen][subLonLen];
                for (int i = 0; i < subLatLen; i++) {
                    int[] origin = new int[] { i * step, 0 };
                    int[] shape = new int[] { 1, lonLen };
                    short[] fullRow = (short[]) heightVar.read(origin, shape).get1DJavaArray(Short.TYPE);
                    for (int j = 0; j < subLonLen; j++) {
                        elevationArr[i][j] = fullRow[j * step];
                    }
                }
                elevationLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ncFile != null) {
                    try { ncFile.close(); } catch (Exception e) {}
                }
            }
        }
    }

    private void displayElevation(BufferedImage image2) {
        Graphics2D g = (Graphics2D) image2.createGraphics();
        try {
            loadElevationData();
            if (elevationArr != null && dateLon != null && dateLat != null) {
                int subLatLen = elevationArr.length;
                int subLonLen = elevationArr[0].length;
                int dateLatL = dateLat.length;
                double m_RadLa = dateLat[dateLatL - 1] * Math.PI / 180.0;
                double am_RadLa = dateLat[0] * Math.PI / 180.0;
                double aEc = CommonProps.RJ + (CommonProps.RC - CommonProps.RJ)
                        * (90 - radarBase.getLatitude()) / 90.0;
                double dy = (m_RadLa - am_RadLa) * aEc;
                double gridWidth = (dy) / dateLatL;
                int w = PositionUtils.toLength(gridWidth / 1000, radarBase.getScale_X()) * 5;
                if (w == 0) w = 1;
                int hw = w / 2;
                if (hw == 0) hw = 1;
                RadarColor radarColor = ElevationColor.color;
                Color[] colors = radarColor.getColors();
                float[] cvalues = radarColor.getColorValues();
                for (int i = 0; i < subLatLen; i++) {
                    for (int j = 0; j < subLonLen; j++) {
                        double v = elevationArr[i][j];
                        for (int k = 0; k < cvalues.length; k++) {
                            if (k != cvalues.length - 1) {
                                if (v >= cvalues[k] && v < cvalues[k + 1]) {
                                    g.setColor(colors[k]);
                                    XYCoord rc = PositionUtils.toXYCoord2(
                                        dateLon[j * ELEVATION_SUBSAMPLE],
                                        dateLat[i * ELEVATION_SUBSAMPLE], radarBase);
                                    g.fillRect(rc.x - hw, rc.y - hw, w, w);
                                }
                            } else {
                                if (v >= cvalues[k]) {
                                    g.setColor(colors[k]);
                                    XYCoord rc = PositionUtils.toXYCoord2(
                                        dateLon[j * ELEVATION_SUBSAMPLE],
                                        dateLat[i * ELEVATION_SUBSAMPLE], radarBase);
                                    g.fillRect(rc.x - hw, rc.y - hw, w, w);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayGrid() {
        Graphics2D g = (Graphics2D) image.createGraphics();
        int pixel = this.radarBase.getCenter_X() - this.radarBase.getXoffset();
        int scanl = this.radarBase.getCenter_Y() - this.radarBase.getYoffset();
        g.setPaint(new Color(214, 177, 159));
        for (double x = polar_grid_ring; x <= radarBase.radius; x += polar_grid_ring) {
            int r = (int) Math.round(x * this.radarBase.getScale_X());
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
            g.drawLine(pixel, scanl,
                (int) Math.round(radarBase.radius * RadarUtils.cos(x) * this.radarBase.getScale_X() + pixel),
                (int) Math.round(radarBase.radius * RadarUtils.sin(x) * this.radarBase.getScale_Y() + scanl));
        }
        g.dispose();
    }

    private void displayMap() {
        if (null != mapFileBytes) {
            Graphics2D g = (Graphics2D) image.createGraphics();
            int mapRadius = (int) Math.round(460 * this.radarBase.getScale_X());
            int pixel = this.radarBase.getCenter_X() - this.radarBase.getXoffset() - mapRadius;
            int scanl = this.radarBase.getCenter_Y() - this.radarBase.getYoffset() - mapRadius;
            double mapTimes = 1.0 / 8.0;
            DataInputStream file = null;
            try {
                ByteArrayInputStream s = new ByteArrayInputStream(mapFileBytes);
                file = new DataInputStream(s);
                file.skipBytes(4);
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
                }
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
                    try { file.close(); } catch (Exception e) { e.printStackTrace(); }
                }
            }
            g.dispose();
        }
    }

    private void displayMapMsg(Graphics2D g, byte[] fileBytes, MapHead mapHead,
            double mapTimes, int pixel, int scanl) throws IOException {
        byte[] databyte = new byte[mapHead.length];
        System.arraycopy(fileBytes, mapHead.startpos, databyte, 0, databyte.length);
        DataInputStream file = new DataInputStream(new ByteArrayInputStream(databyte));
        int x0, y0, x1, y1, msgLength;
        MapMsgHead mapMsgHead = new MapMsgHead();
        int readLen = 0;
        while (readLen < mapHead.length) {
            mapMsgHead.flag1 = file.readUnsignedShort();
            readLen += 2;
            if (mapMsgHead.flag1 != 3619) continue;
            mapMsgHead.flag2 = file.readUnsignedShort();
            readLen += 2;
            mapMsgHead.x1 = file.readShort();
            mapMsgHead.y1 = file.readShort();
            mapMsgHead.bytes = file.readUnsignedShort();
            readLen += 6;
            x0 = (int) Math.round(mapMsgHead.x1 * mapTimes * this.radarBase.getScale_X()) + pixel;
            y0 = (int) Math.round(mapMsgHead.y1 * mapTimes * this.radarBase.getScale_X()) + scanl;
            msgLength = mapMsgHead.bytes / 4;
            for (int j = 0; j < msgLength; j++) {
                x1 = (int) Math.round(file.readShort() * mapTimes * this.radarBase.getScale_X()) + pixel;
                y1 = (int) Math.round(file.readShort() * mapTimes * this.radarBase.getScale_X()) + scanl;
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
        int x0, y0;
        int r = 2;
        int d = 2 * r;
        Set nameSet = new HashSet();
        MapTownName townName = new MapTownName();
        boolean draw = ((mapMode & MAP_TOWNNAME) != 0);
        int readLen = 0;
        while (readLen < mapHead.length) {
            townName.flag1 = file.readUnsignedShort();
            townName.flag2 = file.readUnsignedShort();
            townName.flag3 = file.readUnsignedShort();
            townName.x1 = file.readShort();
            townName.y1 = file.readShort();
            townName.bytes = file.readUnsignedShort();
            readLen += 12;
            if (townName.bytes != 6) {
                if (this.radarBase.getZoom() >= 4 && (mapMode & MAP_DETAILNAME) != 0) {
                    draw = true;
                    continue;
                } else {
                    break;
                }
            }
            for (int i = 0; i < townName.bytes; i++) {
                townName.name[i] = file.readByte();
            }
            readLen += townName.bytes;
            String name = new String(townName.name, "GBK").trim();
            if (draw) {
                x0 = (int) Math.round(townName.x1 * mapTimes * this.radarBase.getScale_X()) + pixel;
                y0 = (int) Math.round(townName.y1 * mapTimes * this.radarBase.getScale_X()) + scanl;
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

    public static class MapMsgHead {
        public int flag1;
        public int flag2;
        public short x1;
        public short y1;
        public int bytes;
        public short[] xy;

        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            DataOutputStream dous = new DataOutputStream(ous);
            dous.writeShort(this.flag1);
            dous.writeShort(this.flag2);
            dous.writeShort(this.x1);
            dous.writeShort(this.y1);
            dous.writeShort(bytes);
            for (int i = 0; i < xy.length; i = i + 2) {
                dous.writeShort(xy[i]);
                dous.writeShort(xy[i + 1]);
            }
            return ous.toByteArray();
        }
    }

    public static class MapTownName {
        public int flag1;
        public int flag2;
        public int flag3;
        public short x1;
        public short y1;
        public int bytes;
        public byte[] name = new byte[6];

        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            DataOutputStream dous = new DataOutputStream(ous);
            dous.writeShort(this.flag1);
            dous.writeShort(this.flag2);
            dous.writeShort(this.flag3);
            dous.writeShort(this.x1);
            dous.writeShort(this.y1);
            dous.writeShort(bytes);
            dous.write(name);
            return ous.toByteArray();
        }
    }

    public static class MapHead {
        public int mapID;
        public int flag1;
        public int flag2;
        public int length;
        public int startpos;
        public byte[] unused = new byte[202];

        public byte[] toByteArray(int startpos, int length) throws IOException {
            this.startpos = startpos;
            this.length = length;
            return this.toByteArray();
        }

        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            DataOutputStream dous = new DataOutputStream(ous);
            dous.writeShort(this.mapID);
            dous.writeShort(this.flag1);
            dous.writeShort(this.flag2);
            dous.writeInt(this.length);
            dous.writeShort(this.startpos);
            dous.write(this.unused);
            return ous.toByteArray();
        }
    }
}
