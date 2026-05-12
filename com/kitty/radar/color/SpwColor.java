package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

/**
 * 谱宽色标。
 */
public class SpwColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(0x9c, 0x9c, 0x9c), new Color(0x76, 0x76, 0x76),
			new Color(0xfa, 0xaa, 0xaa), new Color(0xee, 0x8c, 0x8c), new Color(0xc9, 0x70, 0x70) };

	public static float[] COLOR_VALUES = { 0, 2, 4, 5, 7 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_W,
			-1);

	public static SpwColor color = new SpwColor();

	public Color[] getColors() {
		return COLORS;
	}

	public Color[] getColorCache() {
		return colorCache;
	}

	public float[] getColorValues() {
		return COLOR_VALUES;
	}

	public static int[] createCacheValues(float[] colorValues) {
		int[] cacheValues = new int[colorValues.length];
		for (int i = 0; i < cacheValues.length; i++) {
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], RadarData.W, -1);
		}
		return cacheValues;
	}

}
