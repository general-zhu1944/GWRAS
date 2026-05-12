package com.kitty.radar.listener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

import com.kitty.radar.RadarParams;

/**
 * 自动删除文件处理类。注意：目前删除文件后未及时刷新文件列表，可能导致列表中部分文件已不存在，需要改进
 */
public class DeleteHandler extends TimerTask {

    public void run() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long t1 = cal.getTimeInMillis();
        try {
            File file = new File(RadarParams.filePath);
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String time = FileHandler.getTime(files[i].getName());
                    if (time != null) {
                        cal.setTime(format.parse(time.substring(0, 8)));
                        if ((t1 - cal.getTimeInMillis()) / 86400000.0 > RadarParams.deleteDays) {
                            files[i].delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
