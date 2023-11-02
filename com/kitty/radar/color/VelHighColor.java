package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

/**
 * 高分辨率速度色标，Resolution：2（0.5米/秒）。
 */
public class VelHighColor extends VelColor {

	public static float[] COLOR_VALUES = { -33, -27, -20, -15, -10, -5, -1, 0, 1, 5, 10, 15, 20, 27 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_V,
			RadarData.DOPPLER_RESOLUTION_HIGH);

	public static VelHighColor color = new VelHighColor();

	public Color[] getColorCache() {
		return colorCache;
	}

	public float[] getColorValues() {
		return COLOR_VALUES;
	}

	public static int[] createCacheValues(float[] colorValues) {
		int[] cacheValues = new int[colorValues.length];
		for (int i = 0; i < cacheValues.length; i++) {
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], SA_SB.V, SA_SB.DOPPLER_RESOLUTION_HIGH);
		}
		return cacheValues;
	}

}
