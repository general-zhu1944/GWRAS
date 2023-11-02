package com.kitty.radar.color;

import java.awt.Color;

public class EtColor extends RadarColor {

    public static final Color[] COLORS = new Color[] { new Color(157, 157, 157),
            new Color(0, 224, 254), new Color(0, 176, 254), new Color(0, 114, 204),
            new Color(50, 0, 150), new Color(0, 250, 144), new Color(0, 186, 0),
            new Color(0, 238, 0), new Color(254, 190, 0), new Color(245, 254, 0),
            new Color(174, 0, 0), new Color(254, 0, 0), new Color(254, 254, 254),
            new Color(230, 0, 254) };

    public static float[] COLOR_VALUES = { 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20, 21 };
    
    public static EtColor color = new EtColor();

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
