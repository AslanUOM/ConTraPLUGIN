package com.aslan.contra.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.listeners.OnLocationChangedListener;
import com.aslan.contra.listeners.OnWifiScanResultChangedListener;
import com.aslan.contra.sensor.LocationSensor;
import com.aslan.contra.sensor.WiFiSensor;
import com.aslan.contra.util.DatabaseHelper;

import java.sql.Timestamp;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationTrackingService extends IntentService {

    public static final String TAG = "LocationTrackingService";
    public static boolean isIntentServiceRunning = false;
    public static Runnable runnable = null;
    // The minimum distance to change location Updates in meters
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;
    // The minimum time between location updates in milliseconds, here 30 minutes
    private final long MIN_TIME_BW_UPDATES = 1000 * 60 * 30;
    public Handler handler = null;
    //    private final long TIME_INTERVAL = 1800000L;
    private DatabaseHelper dbHelper;
    private LocationSensor locationSensor;
    private Location currentBestLocation;
    private int counter = 0;
    private WiFiSensor wifiSensor;

    private Intent intent;

    public LocationTrackingService() {
        super("LocationTrackingService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DatabaseHelper(this);

        initialiseLocationTracker();
        initialiseWifiTracker();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                onHandleIntent(LocationTrackingService.this.intent);
                locationSensor.start();
                wifiSensor.start();
                Log.d("<<Tracking-onStart>>", "I am alive");
                Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, MIN_TIME_BW_UPDATES);
            }
        };
    }

    @Override
    public void onStart(Intent intent, int startId) {
        this.intent = intent;
        handler.postDelayed(runnable, 1000);
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
        locationSensor.stop();
//        wifiSensor.stop();
        stopSelf();
        Log.e("<<Tracking-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "DESTROYED", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
        this.intent = intent;
    }

    private void initialiseLocationTracker() {
        locationSensor = new LocationSensor(this, 0, 0);
        locationSensor.setOnLocationChangedListener(new OnLocationChangedListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (counter < 5) {
                    Log.d("Counter: " + counter, location.toString());
                    if (locationSensor.isBetterLocation(location, currentBestLocation)) {
                        currentBestLocation = location;
                        counter++;
                        if (counter == 5) {
                            counter = 0;
                            if (dbHelper.insertLocation(currentBestLocation)) {
                                String loc = currentBestLocation.toString();
                                Log.d("LOCATION", loc);
                                Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_SHORT).show();
                                locationSensor.stop();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initialiseWifiTracker() {
        wifiSensor = new WiFiSensor(this);
        wifiSensor.setOnWifiScanResultChangedLsitener(new OnWifiScanResultChangedListener() {
            @Override
            public void onWifiScanResultsChanged(List<ScanResult> wifiList) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Log.e("TIME", timestamp.toString());
                for (ScanResult wifi : wifiList) {
                    if (dbHelper.insertWifi(wifi, timestamp.toString())) {
                        Log.d("WIFI", wifi.toString());
                        Toast.makeText(getApplicationContext(), wifi.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                wifiSensor.stop();
            }
        });
    }
}
