package com.kitty.radar.domain;

public class BinaryValue extends BinValue {

    private short binaryValue;

    public BinaryValue(double range, short binaryValue) {
        this.range = range;
        this.binaryValue = binaryValue;
    }

    public short getBinaryValue() {
        return binaryValue;
    }

    public void setBinaryValue(short binaryValue) {
        this.binaryValue = binaryValue;
    }

}
