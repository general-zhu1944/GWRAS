package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.util.CommonProps;

/**
 * 降水模式反射率色标，VCP：11、21。
 */
public class RefPrecipColor extends RefColor {

	public static float[] COLOR_VALUES = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_R, -1);

	public static RefPrecipColor color = new RefPrecipColor();

	public Color[] getColorCache() {
		return colorCache;
	}

	public float[] getColorValues() {
		return COLOR_VALUES;
	}

}
