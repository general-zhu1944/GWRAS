package com.kitty.radar;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.RadarUtils;

/**
 * 雷达产品显示基类。
 */
public class RadarBase {

    public static final byte MIN_ZOOM = 1; // 最小放大倍数

    public static final byte MAX_ZOOM = 16; // 最大放大倍数

    public static String vcp = "21"; // VCP模式

    public static short resolution = RadarData.DOPPLER_RESOLUTION_HIGH; // 速度分辨率

    public static byte view = CommonProps.VIEW_PPI;

    public int cutNum = 0; // 选中的仰角

    public static float level = 3; // CAPPI高度，单位：km

    public int width = 0; // 显示区宽度，单位：像素

    public int height = 0; // 显示区高度，单位：像素

    public int zoom = 1; // 当前放大倍数

    public static float radius = RadarUtils.getRadarRadius(); // 显示区半径，单位：km

    public int center_X = 0; // 显示区中心X坐标，单位：像素

    public int center_Y = 0; // 显示区中心Y坐标，单位：像素

    public static double latitude = 30.822000; // 雷达中心纬度，如：北京 39.808889

    public static double longitude = 106.078000; // 雷达中心经度，如：北京 116.471944

    public static float antennaHeight = 0; // 雷达天线高度，单位：km

    public static String radarName = ""; // 雷达站点名称
    public static String siteCode;
    //每个MainPanel单独配置
    public int active_moment = RadarData.DBZ;

    //每个MainPanel单独配置
    public int currentMoment = CommonProps.MOMENT_R;

    public double scale_X = 0; // X方向比例，X显示距离（像素） / X实际距离（km）

    public double scale_Y = 0; // Y方向比例，Y显示距离（像素） / Y实际距离（km）
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

    public int xoffset = 0; // 鼠标拖动后，X方向偏移量，单位：像素

    public int yoffset = 0; // 鼠标拖动后，Y方向偏移量，单位：像素

    public static int radarFormat = RadarData.RADAR_FORMAT_SC.getValue();

    public Object datas; // 保存图像数据

    public void setZoom(int zoomFactor) {
        if (zoomFactor != zoom) {
            xoffset = (int) Math.round(xoffset * zoomFactor / (double) zoom);
            yoffset = (int) Math.round(yoffset * zoomFactor / (double) zoom);
            zoom = zoomFactor;
            computeScale();
        }
    }

    public void computeScale() {
        scale_X = zoom * (width - CommonProps.COLOR_WIDTH) / (double) (radius * 2);
        scale_Y = -scale_X;
    }

    public static void setRadarFormat(int radarFormat) {
        RadarBase.radarFormat = radarFormat;
        radius = RadarUtils.getRadarRadius();
//        computeScale();	//通过GUIManager.repaintAll重新调用computeScale
//        RHI.range_max = radius;
    }
    
    public static int getColorWidth() {
		return 46;
	}

}
