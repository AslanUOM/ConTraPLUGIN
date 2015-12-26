package com.aslan.contra.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by vishnuvathsan on 25-Dec-15.
 */
public class EnvironmentSensor implements SensorEventListener {
    private final String TAG = "EnvironmentSensor";
    private SensorManager mSensorManager;
    private Sensor mAmbientTemp;
    private Sensor mLight;
    private Sensor mProximity;
    private Sensor mPressure;
    private Sensor mRelativeHumidity;

    public EnvironmentSensor(Context context) {
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mAmbientTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mRelativeHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        float millibars_of_pressure = event.values[0];
        switch (event.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                Log.i(TAG, "Amb Temp: " + event.values[0]);
                mSensorManager.unregisterListener(this, mAmbientTemp);
                break;
            case Sensor.TYPE_LIGHT:
                Log.i(TAG, "Light: " + event.values[0]);
                mSensorManager.unregisterListener(this, mLight);
                break;
            case Sensor.TYPE_PROXIMITY:
                Log.i(TAG, "Proximity: " + event.values[0]);
                mSensorManager.unregisterListener(this, mProximity);
                break;
            case Sensor.TYPE_PRESSURE:
                Log.i(TAG, "Pressure: " + event.values[0]);
                mSensorManager.unregisterListener(this, mPressure);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                Log.i(TAG, "Humidity: " + event.values[0]);
                mSensorManager.unregisterListener(this, mRelativeHumidity);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                Log.i(TAG, "Amb Temp: ");
                break;
            case Sensor.TYPE_LIGHT:
                Log.i(TAG, "Light: ");
                break;
            case Sensor.TYPE_PROXIMITY:
                Log.i(TAG, "Proximity: ");
                break;
            case Sensor.TYPE_PRESSURE:
                Log.i(TAG, "Pressure: ");
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                Log.i(TAG, "Humidity: ");
                break;
        }
    }

    public void start() {
        if (mAmbientTemp != null) {
            mSensorManager.registerListener(this, mAmbientTemp, mAmbientTemp.getMinDelay());
        } else {
            Log.i(TAG, "Amb Temp not available");
        }

        if (mLight != null) {
            mSensorManager.registerListener(this, mLight, mLight.getMinDelay());
        } else {
            Log.i(TAG, "Light not available");
        }

        if (mProximity != null) {
            mSensorManager.registerListener(this, mProximity, mProximity.getMinDelay());
        } else {
            Log.i(TAG, "Proximity not available");
        }

        if (mPressure != null) {
            mSensorManager.registerListener(this, mPressure, mPressure.getMinDelay());
        } else {
            Log.i(TAG, "Pressure not available");
        }

        if (mRelativeHumidity != null) {
            mSensorManager.registerListener(this, mRelativeHumidity, mRelativeHumidity.getMinDelay());
        } else {
            Log.i(TAG, "Hum not available");
        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this, mAmbientTemp);
        mSensorManager.unregisterListener(this, mLight);
        mSensorManager.unregisterListener(this, mProximity);
        mSensorManager.unregisterListener(this, mPressure);
        mSensorManager.unregisterListener(this, mRelativeHumidity);
    }
}
