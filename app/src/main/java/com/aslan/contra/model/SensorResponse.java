package com.aslan.contra.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
public class SensorResponse implements Serializable, Iterable<SensorData> {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1772758831865056066L;

    /**
     * ID of the user.
     */
    private String userID;

    /**
     * ID of the device.
     */
    private String deviceID;

    /**
     * Data recorded in this sensor data.
     */
    private List<SensorData> sensorDatas;

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * @param deviceID the deviceID to set
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * @return the sensorDatas
     */
    public List<SensorData> getSensorDatas() {
        return sensorDatas;
    }

    /**
     * @param sensorDatas the sensorDatas to set
     */
    public void setSensorDatas(List<SensorData> sensorDatas) {
        this.sensorDatas = sensorDatas;
    }

    public void addSensorData(SensorData data) {
        if (this.sensorDatas == null) {
            this.sensorDatas = new ArrayList<>();
        }
        this.sensorDatas.add(data);
    }

    public void removeSensorData(SensorData data) {
        if (this.sensorDatas != null) {
            this.sensorDatas.remove(data);
        }
    }

    @Override
    public Iterator<SensorData> iterator() {
        if (this.sensorDatas == null) {
            return null;
        } else {
            return this.sensorDatas.iterator();
        }
    }

    @Override
    public String toString() {
        String text = String.format("User ID: %s, Device ID: %s, No of data: %d", userID, deviceID, sensorDatas.size());
        return text;
    }

}
