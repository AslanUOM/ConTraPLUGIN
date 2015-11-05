package com.aslan.contra.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
public class SensorData implements Serializable {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4911835734262100228L;

    /**
     * Type of the event.
     */
    private String type;

    /**
     * Accuracy or reliability of the event.
     */
    private double accuracy;

    /**
     * Source used to capture the event.
     */
    private String source;

    /**
     * Time of the event in milliseconds from 1970 Jan 1st.
     */
    private long time;

    /**
     * Additional data related to this event.
     */
    private String[] data;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the accuracy
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * @param accuracy the accuracy to set
     */
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return the data
     */
    public String[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String text = String.format("Type: %s, Source: %s", type, source);
        return text;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(accuracy);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensorData other = (SensorData) obj;
        if (Double.doubleToLongBits(accuracy) != Double.doubleToLongBits(other.accuracy))
            return false;
        if (!Arrays.equals(data, other.data))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (time != other.time)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
