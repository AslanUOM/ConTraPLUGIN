package com.aslan.contra.model;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
public class WiFi extends Sensor {
    private String SSID;
    private String BSSID;
    private int level;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String sSID) {
        SSID = sSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String bSSID) {
        BSSID = bSSID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return SSID + "\n" + BSSID + "\n" + level + "\n" + super.getRoundedTimeStamp().toString();
    }
}

