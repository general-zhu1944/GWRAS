package com.kitty.radar.color;

import java.awt.Color;

/**
 * 雷达色标基类。
 */
public abstract class RadarColor {

	/**
	 * 取得色标颜色
	 * 
	 * @return
	 */
	public abstract Color[] getColors();

	/**
	 * 取得色标值
	 * 
	 * @return
	 */
	public abstract float[] getColorValues();

	/**
	 * 取得色标缓存，长度为256的数组，数组下标与颜色对应
	 * 
	 * @return
	 */
	public abstract Color[] getColorCache();

	/**
	 * 取得色标显示精度
	 * 
	 * @return
	 */
	public int getScale() {
		return 0;
	}

	protected static Color[] createColorCache(Color[] colors, int[] cacheValues) {
		return createColorCache(colors, cacheValues, -1, -1);
	}

	protected static Color[] createColorCache(Color[] colors, int[] cacheValues, int moment, int resolution) {
		return createColorCache(colors, cacheValues, moment, resolution, 256);
	}
	
	/**
	 * 创建色标缓存
	 * 
	 * @param colors
	 * @param cacheValues 根据色标值转换的基数据值（0-255）
	 * @return
	 */
	protected static Color[] createColorCache(Color[] colors, int[] cacheValues, int moment, int resolution, int maxCacheValue) {
		int minValue = 0;
		int maxValue = maxCacheValue;
		Color[] colorCache = new Color[maxCacheValue];
		int nextValue;
		for (int i = 0; i < cacheValues.length; i++) {
			if (i == cacheValues.length - 1) {
				nextValue = maxCacheValue;
			} else {
				nextValue = cacheValues[i + 1];
			}
			for (int j = cacheValues[i]; j < nextValue; j++) {
				if (j >= minValue && j <= maxValue && j < colorCache.length) {
					colorCache[j] = colors[i];
				}
			}
		}
		return colorCache;
	}

}
