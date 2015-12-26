package com.aslan.contra.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.sensor.EnvironmentSensor;
import com.aslan.contra.util.Constants;

/**
 * Created by vishnuvathsan on 25-Dec-15.
 */
public class EnvironmentMonitorService extends IntentService {
    public static final String TAG = "EnvironmentMonitorService";
    public static boolean isIntentServiceRunning = false;
    public static Runnable runnable = null;
    private Handler handler = null;
    private Intent intent;
    private EnvironmentSensor environmentSensor;

    public EnvironmentMonitorService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        environmentSensor = new EnvironmentSensor(getApplicationContext());

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d("<<Env-onStart>>", "I am ALIVE");
                environmentSensor.start();
                Toast.makeText(getApplicationContext(), "Environment STARTED", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, Constants.EnvironmentMonitoring.MIN_TIME_BW_UPDATES);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        handler.postDelayed(runnable, 1000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isIntentServiceRunning = false;
        handler.removeCallbacks(runnable);
        environmentSensor.stop();
        Log.d("<<Env-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "Environment DESTROYED", Toast.LENGTH_SHORT).show();
        stopSelf();
        super.onDestroy();
    }
}
