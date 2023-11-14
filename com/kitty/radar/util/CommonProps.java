package com.kitty.radar.util;

public interface CommonProps {

    // ============================ Radar Base ============================

    public static final byte VIEW_PPI = 1;

    public static final byte VIEW_CAPPI = 2;

    public static final byte MOMENT_R = 0;

    public static final byte MOMENT_V = 1;

    public static final byte MOMENT_W = 2;

    public static final byte MOMENT_ET = 3;

    public static final byte MOMENT_LW = 4;

    public static final byte MOMENT_HP = 5;
    
    public static final byte MOMENT_VIL = 6;
    public static final byte MOMENT_CAPPI = -4;
    
    public static final byte MOMENT_CR = -2;

    public static final byte MOMENT_DBT = 7;
    public static final byte MOMENT_ZDR = 8;
    public static final byte MOMENT_KDP = 9;
    public static final byte MOMENT_DP = 10;
    public static final byte MOMENT_CC = 11;
    public static final byte MOMENT_SNRH = 12;

    public static final short COLOR_WIDTH = 50;

    public static final double D = 0.000058869;

    public static final double RC = 6378137; // 赤道半径（单位：米）

    public static final double RJ = 6356725; // 极半径（单位：米）

    public static final double RE = 6371.229; // 地球半径（单位：km）

    public static final String FILE_SEPARATOR = "     ";

    public static final String FILE_RETURN = "\r\n";

    // ============================ Action Commands ============================

    public static final String AC_NEW = "New";

    public static final String AC_SAVE_AS = "Save As";

    public static final String AC_EXPORT_PPI = "Export PPI";

    public static final String AC_EXPORT_CAPPI = "Export CAPPI";

    public static final String AC_EXPORT_RHI = "Export RHI";

    public static final String AC_PAGE_SETUP = "Page Setup";

    public static final String AC_PRINT_PREVIEW = "Print Preview";

    public static final String AC_PREVIEW_PRINT = "Preview Print";

    public static final String AC_PREVIEW_PAGE_SETUP = "Preview Page Setup";

    public static final String AC_PRINT = "Print";

    public static final String AC_EXIT_APP = "Exit App";

    public static final String AC_SHOW_TOOL_BAR = "Show Tool Bar";

    public static final String AC_SHOW_STATUS = "Show Status";

    public static final String AC_SYNC_TOOL = "Sync Tool";

    public static final String AC_SYNC_TIME = "Sync Time";

    public static final String AC_SYNC_CUT = "Sync Cut";

    public static final String AC_SHOW_RIGHT_PANEL = "Show Right Panel";

    public static final String AC_RHI = "RHI";

    public static final String AC_GRID = "Grid";
    public static final String AC_SURFACE="Surface";

    public static final String AC_DOWNLOAD_RADAR="Download Radar";//弹出下载对话框

    public static final String AC_QUERY_RDAR="QUERY Radar";//执行下载

    public static final String AC_QUERY_RAIN = "Query Rain";

    public static final String AC_QUERY_TEMPER = "Query Temper";

    public static final String AC_QUERY_TD = "Query Td";

    public static final String AC_QUERY_WINDMAX = "Query Windmax";

    public static final String AC_MAP = "Map";
    public static final String AC_TERRAIN="terrain";

    public static final String AC_POINT = "Point";

    public static final String AC_TRACK = "Track";

    public static final String AC_AREA = "Area";

    public static final String AC_REFLECTIVITY = "Reflectivity";

    public static final String AC_VELOCITY = "Velocity";

    public static final String AC_SPECTRUM_WIDTH = "Spectrum Width";
    
    public static final String AC_ZDR = "ZDR";
    public static final String AC_KDP = "KDP";
    public static final String AC_DP = "DP";
    public static final String AC_CC = "CC";
    public static final String AC_SNRH = "SNRH";
    public static final String AC_DBT = "DBT";

    public static final String AC_LIQUID_WATER = "Liquid Water";

    public static final String AC_VERTICAL_LIQUID_WATER = "Vertical Liquid Water";
    public static final String AC_CAPPI_new="Cappi";

    public static final String AC_ECHO_TOPS = "Echo Tops";

    public static final String AC_HAIL_PROBABILITY = "Hail Probability";

    public static final String AC_ABOUT = "About";

    public static final String AC_HELP = "Help";

    public static final String AC_SITE_INFO = "Site Info";
    public static final String AC_TQ_INFO = "TianQing Info";

    public static final String AC_MOMENT_RANGE = "Moment Range";

    public static final String AC_CONFIRM = "Confirm";

    public static final String AC_CANCEL = "Cancel";

    public static final String AC_APPLY = "Apply";

    public static final String AC_BACKGROUND = "Background";

    public static final String AC_HAILPROPS = "HailProps";
    
    public static final String AC_RESOLUTION = "Resolution";

    public static final String AC_T_CURSOR = "T Cursor";

    public static final String AC_T_HAND = "T Hand";

    public static final String AC_T_RESET = "T Reset";

    public static final String AC_T_ZOOM_IN = "T Zoom In";

    public static final String AC_T_ZOOM_OUT = "T Zoom Out";

    public static final String AC_T_MEASURE = "T Measure";

    public static final String AC_T_VCS = "T VCS";

    public static final String AC_T_VCS_MENU = "T VCS MENU";
    
    public static final String AC_T_WIND = "T WIND";

    public static final String AC_T_GRID = "T Grid";

    public static final String AC_T_MAP = "T Map";

    public static final String AC_T_3D = "T 3D";

    public static final String AC_T_CROSS = "T Cross";

    public static final String AC_PREVIOUS_FILE = "Previous File";

    public static final String AC_NEXT_FILE = "Next File";

    public static final String AC_LOOP_FILE = "Loop File";

    public static final String AC_STOP_LOOP = "Stop Loop";

    public static final String AC_CAPPI = "CAPPI";

	public static final String AC_EXPORT_SET = "Export Set";

	public static final String AC_EXPORT_CURRENT = "Export Current";
    public static final String AC_DiscreteData="discreteData";

}
