package com.buzzware.nowapp.Models;

public class BusinessHomeGraphData {
    String hour, intensity;

    public BusinessHomeGraphData(String hour, String intensity) {
        this.hour = hour;
        this.intensity = intensity;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }
}
