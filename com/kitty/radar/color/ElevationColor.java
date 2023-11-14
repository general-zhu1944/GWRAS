package com.kitty.radar.color;

import java.awt.Color;

public class ElevationColor extends RadarColor {

    public static final Color[] COLORS = new Color[] { 
            new Color(40, 40, 40), new Color(60, 60, 60), new Color(80, 80, 80),
            new Color(100, 100, 100), new Color(120, 120, 120), new Color(140, 140, 140),
            new Color(160, 160, 160), new Color(180, 180, 180), new Color(200, 200, 200),
            new Color(220, 220, 220), new Color(240, 240, 240),new Color(250, 250, 250),};

    public static float[] COLOR_VALUES = { 100, 500,900,1300, 1700, 2100, 2500, 2900, 3300, 3700,4100,6500};
    
    public static ElevationColor color = new ElevationColor();

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
