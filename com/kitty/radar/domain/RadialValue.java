package com.kitty.radar.domain;

import java.util.ArrayList;
import java.util.List;

public class RadialValue {
    
    private double azimuth;
    
    private List binValues = new ArrayList();
    
    public RadialValue(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public List getBinValues() {
        return binValues;
    }
    
    public void addBinValue(BinValue bv) {
        this.binValues.add(bv);
    }

}
