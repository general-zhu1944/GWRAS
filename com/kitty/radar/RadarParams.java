package com.kitty.radar;

import java.awt.print.PageFormat;
import java.io.File;
import java.util.Timer;

import com.kitty.radar.gui.PrintPreviewDialog;
import com.kitty.radar.util.CommonUtils;

public class RadarParams {

    public static float timerRate = 3;

    public static int deleteDays = 3;

    public static boolean enableDelete = false;

    public static String filePath = CommonUtils.appPath + "data" + File.separator; // 文件的目录路径

    public static String lastSavePath = CommonUtils.appPath; // 最后的保存目录路径

    public static boolean auto_update = false;

    public static PageFormat pageFormat;

    protected Timer timer;

    protected PrintPreviewDialog previewDialog;

    protected Timer deleteTimer;

}
