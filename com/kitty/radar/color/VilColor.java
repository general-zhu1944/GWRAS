package com.kitty.radar.color;

import java.awt.Color;

/**
 * 液水含量色标。
 */
public class VilColor extends RadarColor {

    public static final Color[] COLORS = new Color[] { new Color(156, 156, 156),
            new Color(118, 118, 118), new Color(250, 170, 170), new Color(238, 140, 140),
            new Color(200, 112, 112), new Color(0, 250, 144), new Color(0, 186, 0),
            new Color(254, 254, 112), new Color(208, 208, 96), new Color(254, 96, 96),
            new Color(218, 0, 0), new Color(174, 0, 0), new Color(0, 0, 254),
            new Color(254, 254, 254), new Color(230, 0, 254) };

    public static float[] COLOR_VALUES = { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70 };

    public static VilColor color = new VilColor();

    @Override
    public Color[] getColorCache() {
        return null;
    }

    @Override
    public float[] getColorValues() {
        return COLOR_VALUES;
    }

    @Override
    public Color[] getColors() {
        return COLORS;
    }

}
