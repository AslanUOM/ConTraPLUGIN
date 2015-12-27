package com.aslan.contra.model;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
//TODO delete this class if not necessary
public class Location extends Sensor {
    private String provider;
    private String latitude;
    private String longitude;
    private double accuracy;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return provider + "\n" + latitude + ", " + longitude + "\n" + accuracy + "\n" + super.getRoundedTimeStamp().toString();
    }

}
