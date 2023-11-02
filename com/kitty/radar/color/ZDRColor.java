package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

public class ZDRColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(66, 69, 66), new Color(107, 109, 107),
			new Color(148, 150, 148), new Color(206, 203, 206), new Color(222, 243, 222), new Color(0, 195, 33),
			new Color(0, 235, 8), new Color(33, 255, 33), new Color(255, 255, 24), new Color(255, 231, 0),
			new Color(255, 190, 0), new Color(255, 154, 0), new Color(255, 93, 0), new Color(247, 12, 0),
			new Color(189, 0, 57), new Color(255, 0, 255) };

	public static float[] COLOR_VALUES = { -4, -3, -2, -1, 0, 0.2f, 0.5f, 0.8f, 1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 5 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_ZDR,
			-1);

	public static ZDRColor color = new ZDRColor();

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
        return 1;
    }

	public static int[] createCacheValues(float[] colorValues) {
		int[] cacheValues = new int[colorValues.length];
		for (int i = 0; i < cacheValues.length; i++) {
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], SA_SB.ZDR, -1);
		}
		return cacheValues;
	}

}
