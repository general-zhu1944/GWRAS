package com.kitty.radar.domain;

public class MomentValue extends BinValue {

    private float momentValue;

    public MomentValue(double range, float momentValue) {
        this.range = range;
        this.momentValue = momentValue;
    }

    public float getMomentValue() {
        return momentValue;
    }

    public void setMomentValue(float momentValue) {
        this.momentValue = momentValue;
    }

}
