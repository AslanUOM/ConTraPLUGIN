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
    public static String TAG = "EnvironmentMonitorService";
    public static Runnable runnable = null;
    public Handler handler = null;
    private Intent intent;
    private EnvironmentSensor environmentSensor;

    public EnvironmentMonitorService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public EnvironmentMonitorService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        environmentSensor = new EnvironmentSensor(getApplicationContext());

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                onHandleIntent(EnvironmentMonitorService.this.intent);
                Log.d("<<Env-onStart>>", "I am STARTED");
                environmentSensor.start();
                Toast.makeText(getApplicationContext(), "ENV", Toast.LENGTH_SHORT).show();
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
        handler.removeCallbacks(runnable);
        environmentSensor.stop();
        Log.d("<<Env-onDestroy>>", "I am DESTROYED");
        stopSelf();
        super.onDestroy();
    }
}
