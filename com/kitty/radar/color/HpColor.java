package com.kitty.radar.color;

import java.awt.Color;

public class HpColor extends RadarColor {

    public static final Color[] COLORS = new Color[] { new Color(240, 240, 240),
            new Color(0, 107, 253), new Color(9, 186, 253), new Color(111, 248, 255),
            new Color(0, 150, 50), new Color(0, 220, 0), new Color(180, 255, 180),
            new Color(196, 166, 0), new Color(238, 255, 0), new Color(139, 255, 0),
            new Color(255, 0, 0), new Color(255, 100, 100), new Color(255, 180, 180),
            new Color(150, 0, 180), new Color(200, 100, 155), new Color(241, 98, 153) };

    public static float[] COLOR_VALUES = { 0, 10, 20, 30, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85,
            90, 95 };
    
    public static HpColor color = new HpColor();

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
