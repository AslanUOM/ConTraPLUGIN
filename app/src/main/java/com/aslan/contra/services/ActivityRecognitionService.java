package com.aslan.contra.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.sensor.ActivitySensor;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognitionService extends IntentService {
    public static final String TAG = "ActivityRecognitionService";
    public static boolean isIntentServiceRunning = false;
    //    public static Runnable runnable = null;
//    private Handler handler = null;
    private Intent intent;
    private ActivitySensor activitySensor;

    public ActivityRecognitionService() {
        super(TAG);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        this.activitySensor = ActivitySensor.getInstance(getApplicationContext());
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                locationSensor.start();
//                Log.d("<<Location-onStart>>", "I am alive");
//                Toast.makeText(getApplicationContext(), "Location STARTED", Toast.LENGTH_SHORT).show();
//                handler.postDelayed(runnable, Constants.LocationTracking.MIN_TIME_BW_UPDATES);
//            }
//        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
//        handler.postDelayed(runnable, 1000);
        // Start service
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                activitySensor.start();
                Log.d("<<Activity-onStart>>", "I am ALIVE");
                return null;
            }
        }.execute();
//                    activitySensor.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isIntentServiceRunning = false;
//        handler.removeCallbacks(runnable);// Stop service
        activitySensor.stop();
        stopSelf();
        Log.d("<<Activity-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "Activity DESTROYED", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
    }
}
