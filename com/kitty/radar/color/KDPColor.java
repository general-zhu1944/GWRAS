package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

public class KDPColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(0,255,255), new Color(0,239,239),
			new Color(0,170,173), new Color(181,182,181), new Color(181,182,181), new Color(0,195,33),
			new Color(0,235,8), new Color(33,255,33), new Color(255,255,24), new Color(255,231,0),
			new Color(255,190,0), new Color(255,154,0), new Color(255,93,0), new Color(247,12,0),
			new Color(189,0,57), new Color(255,0,255) };

	public static float[] COLOR_VALUES = { -0.8f, -0.4f, -0.2f, -0.1f, 0.1f, 0.15f, 0.22f, 0.33f, 0.5f, 0.75f, 1.1f, 1.7f,
			2.4f, 3.1f, 7, 20 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_KDP,
			-1);

	public static KDPColor color = new KDPColor();

	public Color[] getColors() {
		return COLORS;
	}

	public Color[] getColorCache() {
		return colorCache;
	}

	public float[] getColorValues() {
		return COLOR_VALUES;
	}
	
	public int getScale() {
        return 2;
    }

	public static int[] createCacheValues(float[] colorValues) {
		int[] cacheValues = new int[colorValues.length];
		for (int i = 0; i < cacheValues.length; i++) {
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], SA_SB.KDP, -1);
		}
		return cacheValues;
	}

}
