package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

public class SNRHColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(136, 16, 140), new Color(0, 172, 164), new Color(192, 192, 254),
			new Color(122, 114, 238), new Color(30, 38, 208), new Color(166, 252, 168), new Color(0, 234, 0),
			new Color(16, 146, 26), new Color(252, 244, 100), new Color(200, 200, 2), new Color(140, 140, 0),
			new Color(254, 172, 172), new Color(254, 100, 92), new Color(238, 2, 48), new Color(212, 142, 254),
			new Color(173, 36, 255) };

	public static float[] COLOR_VALUES = { -9.5f, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_SNRH,
			-1);

	public static SNRHColor color = new SNRHColor();

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
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], SA_SB.SNRH, -1);
		}
		return cacheValues;
	}

}
