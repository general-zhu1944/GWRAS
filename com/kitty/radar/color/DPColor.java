package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

public class DPColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(0,60,255), new Color(0,239,239),
			new Color(0,186,189), new Color(0,130,123), new Color(0,138,57), new Color(0,182,41),
			new Color(0,219,8), new Color(0,255,0), new Color(255,255,57), new Color(255,243,0),
			new Color(255,199,0), new Color(255,166,0), new Color(255,113,0), new Color(255,28,0),
			new Color(198,0,0), new Color(214,0,173) };

	public static float[] COLOR_VALUES = { 264, 271, 278, 285, 292, 299, 306, 313, 320, 327, 334, 341,
			348, 355, 362, 369 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_DP,
			-1, 65536);

	public static DPColor color = new DPColor();

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
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], SA_SB.DP, -1);
		}
		return cacheValues;
	}

}
