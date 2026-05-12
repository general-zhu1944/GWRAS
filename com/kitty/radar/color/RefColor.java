package com.kitty.radar.color;

import java.awt.Color;

import com.kitty.radar.data.RadarData;
import com.kitty.radar.data.SA_SB;

/**
 * 反射率色标基类。
 */
public abstract class RefColor extends RadarColor {

	public static final Color[] COLORS = new Color[] { new Color(0x63, 0x63, 0x63),
            new Color(0x00, 0xec, 0xec), new Color(0x00, 0xa0, 0xf6), new Color(0x00, 0x00, 0xf6),
            new Color(0x00, 0xff, 0x00), new Color(0x00, 0xc8, 0x00), new Color(0x00, 0x90, 0x00),
            new Color(0xff, 0xff, 0x00), new Color(0xe7, 0xc0, 0x00), new Color(0xff, 0x90, 0x00),
            new Color(0xff, 0x00, 0x00), new Color(0xd6, 0x00, 0x00), new Color(0xc0, 0x00, 0x00),
            new Color(0xff, 0x00, 0xff), new Color(0x99, 0x55, 0xc9), new Color(0xff, 0xff, 0xff) };

    public Color[] getColors() {
        return COLORS;
    }

    public static int[] createCacheValues(float[] colorValues) {
        int[] cacheValues = new int[colorValues.length];
        for (int i = 0; i < cacheValues.length; i++) {
            cacheValues[i] = SA_SB.momentToBinary(colorValues[i], RadarData.DBZ, -1);
        }
        return cacheValues;
    }

}
