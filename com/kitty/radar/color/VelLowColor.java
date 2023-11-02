package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;
import com.kitty.radar.util.CommonProps;

/**
 * 低分辨率速度色标，Resolution：4（1米/秒）。
 */
public class VelLowColor extends VelColor {

	public static float[] COLOR_VALUES = { -33, -26, -19, -14, -11, -5, -1, 0, 5, 11, 14, 19, 26, 33 };

	private Color[] colorCache = createColorCache(COLORS, createCacheValues(COLOR_VALUES), CommonProps.MOMENT_V,
			RadarData.DOPPLER_RESOLUTION_LOW);

	public static VelLowColor color = new VelLowColor();

	public Color[] getColorCache() {
		return colorCache;
	}

	public float[] getColorValues() {
		return COLOR_VALUES;
	}

	public static int[] createCacheValues(float[] colorValues) {
		int[] cacheValues = new int[colorValues.length];
		for (int i = 0; i < cacheValues.length; i++) {
			cacheValues[i] = SA_SB.momentToBinary(colorValues[i], SA_SB.V, SA_SB.DOPPLER_RESOLUTION_LOW);
		}
		return cacheValues;
	}

}
