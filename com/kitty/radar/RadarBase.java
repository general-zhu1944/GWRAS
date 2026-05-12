package com.kitty.radar;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.RadarUtils;

/**
 * 雷达产品显示基类。
 */
public class RadarBase {

    public static final byte MIN_ZOOM = 1; // 最小放大倍数

    public static final byte MAX_ZOOM = 16; // 最大放大倍数

    public String vcp = "21"; // VCP模式

    public short resolution = RadarData.DOPPLER_RESOLUTION_HIGH; // 速度分辨率

    public byte view = CommonProps.VIEW_PPI;

    public int cutNum = 0; // 选中的仰角

    public float level = 3; // CAPPI高度，单位：km

    private int width = 0; // 显示区宽度，单位：像素 MainPanel组件的宽度

    private int height = 0; // 显示区高度，单位：像素 MainPanel组件的高度

    private int zoom = 1; // 当前放大倍数

    public static float radius = RadarUtils.getRadarRadius(); // 显示区半径，单位：km

    private int center_X = 0; // 显示区中心X坐标，单位：像素 MainPanel组件中心X坐标

    private int center_Y = 0; // 显示区中心Y坐标，单位：像素 MainPanel组件中心Y坐标
    
    public static double latitude1 = 30.822000; // 雷达中心纬度，如：北京 39.808889

    public static double longitude1 = 106.078000; // 雷达中心经度，如：北京 116.471944

    private double latitude = latitude1;//30.822000; // 雷达中心纬度，如：北京 39.808889

    private double longitude =longitude1;// 106.078000; // 雷达中心经度，如：北京 116.471944

    private float antennaHeight = 0; // 雷达天线高度，单位：km

    public String radarName = ""; // 雷达站点名称

    public String siteCode;
    //每个MainPanel单独配置
    public int active_moment = RadarData.DBZ;

    //每个MainPanel单独配置
    public int currentMoment = CommonProps.MOMENT_R;

    private double scale_X = 0; // X方向比例，X显示距离（像素） / X实际距离（km）

    private double scale_Y = 0; // Y方向比例，Y显示距离（像素） / Y实际距离（km）
//
//    public static float minimum_r = 0;
//
//    public static float maximum_r = 70;
//
//    public static float minimum_v = -80;
//
//    public static float maximum_v = 80;
//
//    public static float minimum_w = 0;
//
//    public static float maximum_w = 20;
//
//    public static float minimum_t = 0;
//
//    public static float maximum_t = 20;
//
//    public static float minimum_l = 0;
//
//    public static float maximum_l = 10;
//
//    public static float minimum_p = 0;
//
//    public static float maximum_p = 100;

    public RadarData l2 = null;

    private int xoffset = 0; // 鼠标拖动后，X方向偏移量，单位：像素

    private int yoffset = 0; // 鼠标拖动后，Y方向偏移量，单位：像素

    public static int radarFormat = RadarData.RADAR_FORMAT_SC.getValue();

    public Object datas; // 保存图像数据

    public void setZoom(int zoomFactor) {
        if (zoomFactor != zoom) {
            setXoffset((int) Math.round(getXoffset() * zoomFactor / (double) zoom));
            setYoffset((int) Math.round(getYoffset() * zoomFactor / (double) zoom));
            zoom = zoomFactor;
            computeScale();
        }
    }

    public void computeScale() {
        setScale_X(getZoom() * (getWidth() - CommonProps.COLOR_WIDTH) / (double) (radius * 2));
        setScale_Y(-getScale_X());
    }

    public static void setRadarFormat(int radarFormat) {
        RadarBase.radarFormat = radarFormat;
        radius = RadarUtils.getRadarRadius();
        GUIManager.repaintAll();
//        computeScale();	//通过GUIManager.repaintAll重新调用computeScale
//        RHI.range_max = radius;
    }
    
    public static int getColorWidth() {
		return 46;
	}

	public int getCenter_X() {
		return center_X;
	}

	public void setCenter_X(int center_X) {
		this.center_X = center_X;
	}

	public int getCenter_Y() {
		return center_Y;
	}

	public void setCenter_Y(int center_Y) {
		this.center_Y = center_Y;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAntennaHeight() {
		return antennaHeight;
	}

	public void setAntennaHeight(float antennaHeight) {
		this.antennaHeight = antennaHeight;
	}

	public double getScale_Y() {
		return scale_Y;
	}

	public void setScale_Y(double scale_Y) {
		this.scale_Y = scale_Y;
	}

	public double getScale_X() {
		return scale_X;
	}

	public void setScale_X(double scale_X) {
		this.scale_X = scale_X;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getZoom() {
		return zoom;
	}

	public int getXoffset() {
		return xoffset;
	}

	public void setXoffset(int xoffset) {
		this.xoffset = xoffset;
	}

	public int getYoffset() {
		return yoffset;
	}

	public void setYoffset(int yoffset) {
		this.yoffset = yoffset;
	}

}
