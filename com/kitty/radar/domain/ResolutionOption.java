package com.kitty.radar.domain;

import com.kitty.component.gui.domain.Option;

public class ResolutionOption extends Option {

    private float resolution;

    private float range;

    public ResolutionOption(String resolution, String range, int value) {
        super(resolution + "km*" + resolution + "km " + range + "km", value);
        this.resolution = Float.parseFloat(resolution);
        this.range = Float.parseFloat(range);
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getResolution() {
        return resolution;
    }

    public void setResolution(float resolution) {
        this.resolution = resolution;
    }

}
