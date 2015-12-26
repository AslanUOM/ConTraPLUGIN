package com.aslan.contra.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.listeners.OnWifiScanResultChangedListener;
import com.aslan.contra.sensor.WiFiSensor;
import com.aslan.contra.util.Constants;

import java.sql.Timestamp;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NearbyTerminalTrackingService extends IntentService {
    public static final String TAG = "NearbyTerminalTrackingService";
    public static boolean isIntentServiceRunning = false;
    public static Runnable runnable = null;
    private Handler handler = null;
    private Intent intent;
    private WiFiSensor wifiSensor;

    public NearbyTerminalTrackingService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO uncomment WiFi tracking when implemented in server side
        initialiseWifiTracker();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //TODO uncomment WiFi tracking when implemented in server side
                wifiSensor.start();
                Log.d("<<Nearby-onStart>>", "I am alive");
                Toast.makeText(getApplicationContext(), "Nearby STARTED", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, Constants.NearbyTerminalTracking.MIN_TIME_BW_UPDATES);
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
//        no need to stop separately as it stops itself on scanned result
//        wifiSensor.stop();
        stopSelf();
        Log.d("<<Nearby-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "Nearby DESTROYED", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
    }


    //TODO uncomment WiFi tracking when implemented in server side
    private void initialiseWifiTracker() {
        wifiSensor = new WiFiSensor(this);
        wifiSensor.setOnWifiScanResultChangedLsitener(new OnWifiScanResultChangedListener() {
            @Override
            public void onWifiScanResultsChanged(List<ScanResult> wifiList) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Log.e("TIME", timestamp.toString());
                for (ScanResult wifi : wifiList) {
                    Log.d("WIFI", wifi.toString());
                    Toast.makeText(getApplicationContext(), wifi.toString(), Toast.LENGTH_SHORT).show();
                }
                wifiSensor.stop();
            }
        });
    }
}
