package com.aslan.contra.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.aslan.contra.dto.common.Environment;
import com.aslan.contra.dto.common.Time;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;
import com.aslan.contra.wsclient.ServiceConnector;

/**
 * Created by vishnuvathsan on 25-Dec-15.
 */
public class EnvironmentSensor implements SensorEventListener {
    private final String TAG = "EnvironmentSensor";
    ServiceConnector.OnResponseListener<String> listener;
    private Context context;
    private SensorManager mSensorManager;
    private Sensor mAmbientTemp;
    private Sensor mLight;
    private Sensor mPressure;
    private Sensor mRelativeHumidity;
    private int sensorCount = 0;


    public EnvironmentSensor(Context context, ServiceConnector.OnResponseListener<String> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Environment environment = new Environment();
        switch (event.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                Log.i(TAG, "Amb Temp: " + event.values[0]);
                environment.setTemperature(event.values[0]);
                sensorCount--;
                mSensorManager.unregisterListener(this, mAmbientTemp);
                break;
            case Sensor.TYPE_LIGHT:
                Log.i(TAG, "Light: " + event.values[0]);
                environment.setIlluminance(event.values[0]);
                sensorCount--;
                mSensorManager.unregisterListener(this, mLight);
                break;
            case Sensor.TYPE_PRESSURE:
                Log.i(TAG, "Pressure: " + event.values[0]);
                environment.setPressure(event.values[0]);
                sensorCount--;
                mSensorManager.unregisterListener(this, mPressure);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                Log.i(TAG, "Humidity: " + event.values[0]);
                environment.setHumidity(event.values[0]);
                sensorCount--;
                mSensorManager.unregisterListener(this, mRelativeHumidity);
                break;
        }
        if (sensorCount == 0) {
            SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(context);
            service.sendEnvironment(environment, Time.now(), listener);
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
            case Sensor.TYPE_PRESSURE:
                Log.i(TAG, "Pressure: ");
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                Log.i(TAG, "Humidity: ");
                break;
        }
    }

    public void start() {
        if (mSensorManager == null) {
            // Get an instance of the sensor service, and use that to get an instance of
            // a particular sensor.
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

            mAmbientTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            mRelativeHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }


        if (mAmbientTemp != null) {
            mSensorManager.registerListener(this, mAmbientTemp, mAmbientTemp.getMinDelay());
            sensorCount++;
        } else {
            Log.i(TAG, "Amb Temp not available");
        }

        if (mLight != null) {
            mSensorManager.registerListener(this, mLight, mLight.getMinDelay());
            sensorCount++;
        } else {
            Log.i(TAG, "Light not available");
        }

        if (mPressure != null) {
            mSensorManager.registerListener(this, mPressure, mPressure.getMinDelay());
            sensorCount++;
        } else {
            Log.i(TAG, "Pressure not available");
        }

        if (mRelativeHumidity != null) {
            mSensorManager.registerListener(this, mRelativeHumidity, mRelativeHumidity.getMinDelay());
            sensorCount++;
        } else {
            Log.i(TAG, "Hum not available");
        }
    }

    public void stop() {
        sensorCount = 0;
        mSensorManager.unregisterListener(this, mAmbientTemp);
        mSensorManager.unregisterListener(this, mLight);
        mSensorManager.unregisterListener(this, mPressure);
        mSensorManager.unregisterListener(this, mRelativeHumidity);
    }
}
