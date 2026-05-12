package com.kitty.radar.color;

import java.awt.Color;

/**
 * 速度色标基类。
 */
public abstract class VelColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(123, 227, 255), new Color(0, 227, 255),
			new Color(0, 178, 181), new Color(0, 255, 0), new Color(0, 199, 0), new Color(0, 130, 0),
			new Color(255, 255, 255), new Color(255, 255, 255), new Color(255, 0, 0), new Color(255, 89, 90),
			new Color(255, 178, 181), new Color(255, 125, 0), new Color(255, 211, 0), new Color(255, 255, 0) };

	//new Color(123, 0, 123) RF
	
	public Color[] getColors() {
		return COLORS;
	}

}
