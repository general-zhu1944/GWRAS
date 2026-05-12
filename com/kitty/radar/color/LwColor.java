package com.kitty.radar.color;

import java.awt.Color;

/**
 * 液水含量色标。
 */
public class LwColor extends RadarColor {

    public static final Color[] COLORS = new Color[] { new Color(0, 0, 240),
            new Color(0, 107, 253), new Color(9, 186, 253), new Color(111, 248, 255),
            new Color(0, 150, 50), new Color(0, 220, 0), new Color(180, 255, 180),
            new Color(196, 166, 0), new Color(238, 255, 0), new Color(139, 255, 0),
            new Color(255, 0, 0), new Color(255, 100, 100), new Color(255, 180, 180),
            new Color(150, 0, 180), new Color(200, 100, 155), new Color(241, 98, 153) };

    public static final float[] COLOR_VALUES = new float[] { 0.01f, 0.10f, 0.15f, 0.20f, 0.25f,
            0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 1f, 2f, 3f, 4f, 6f, 8f };

    public static LwColor color = new LwColor();

    private Color[] colorCache = createColorCache(COLORS, new int[] { 83, 118, 124, 128,
            132, 134, 137, 139, 141, 142, 153, 163, 169, 174, 180, 184 });

    public Color[] getColorCache() {
        return colorCache;
    }

    public float[] getColorValues() {
        return COLOR_VALUES;
    }

    public Color[] getColors() {
        return COLORS;
    }
    
    public int getScale() {
        return 2;
    }

}
