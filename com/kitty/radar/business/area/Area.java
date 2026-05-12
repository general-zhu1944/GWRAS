package com.kitty.radar.business.area;

import java.awt.Graphics2D;
import java.awt.Shape;

import com.kitty.radar.RadarBase;

/**
 * 区域基类。
 */
public abstract class Area {
	

    private RadarBase radarBase;
    
    

    protected boolean visible = true;

    /**
     * 取得区域名称，如：圆形、矩形、椭圆形
     * 
     * @return
     */
    public abstract String getAreaName();
    
    
    public Area(RadarBase radarBase) {
    	this.setRadarBase(radarBase);
	}

    /**
     * 区域是否显示
     * 
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * 设置区域是否显示
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * 向Graphics画出区域
     * 
     * @param g
     */
    public abstract void display(Graphics2D g);

    /**
     * 将区域转换为Shape对象。
     * 通过Shape.contains(double x, double y)方法可以判定指定的(x, y)点是否在区域内
     * 
     * @return
     */
    public abstract Shape toShape();

    /**
     * 将区域对象序列化为字符串，用于保存到配置文件
     * 
     * @return
     */
    public abstract String serialize();

    /**
     * 从字符串反序列化区域对象
     * 
     * @param value
     */
    public abstract void deSerialize(String value);


	public RadarBase getRadarBase() {
		return radarBase;
	}


	public void setRadarBase(RadarBase radarBase) {
		this.radarBase = radarBase;
	}

}
