package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.util.CommonProps;

/**
 * 晴空模式反射率色标，VCP：31、32。
 */
public class RefClearColor extends RefColor {

	public static final float[] COLOR_VALUES = { -32, -28, -24, -20, -16, -12, -8, -4, 0, 4, 8, 12, 16, 20, 24, 28 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_R, -1);

	public static RefClearColor color = new RefClearColor();

	public Color[] getColorCache() {
		return colorCache;
	}

	public float[] getColorValues() {
		return COLOR_VALUES;
	}

}
