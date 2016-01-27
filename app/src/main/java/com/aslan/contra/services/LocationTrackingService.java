package com.aslan.contra.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.dto.ws.Message;
import com.aslan.contra.listeners.OnLocationChangedListener;
import com.aslan.contra.sensor.LocationSensor;
import com.aslan.contra.util.DatabaseHelper;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;
import com.aslan.contra.wsclient.ServiceConnector;

import static com.aslan.contra.util.Constants.LocationTracking;
import static com.aslan.contra.util.Constants.ServiceTAGs;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class LocationTrackingService extends IntentService implements ServiceConnector.OnResponseListener<String> {

    public static final String TAG = ServiceTAGs.LOCATION_TRACKING;
    public static boolean isIntentServiceRunning = false;
    public static Runnable runnable = null;
    private Handler handler = null;
    private Intent intent;
    private DatabaseHelper dbHelper;
    private LocationSensor locationSensor;
    private Location currentBestLocation;
    private int counter = 0;

    public LocationTrackingService() {
        super(TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DatabaseHelper(getApplicationContext());

        initialiseLocationTracker();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                locationSensor.start();
                Log.d("<<Location-onStart>>", "I am ALIVE");
                Toast.makeText(getApplicationContext(), "Location STARTED", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, LocationTracking.MIN_TIME_BW_UPDATES);
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
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
    }

    @Override
    public void onDestroy() {
        isIntentServiceRunning = false;
        handler.removeCallbacks(runnable);
        locationSensor.stop();
//        no need to stop separately as it stops itself on scanned result
//        wifiSensor.stop();
        stopSelf();
        Log.d("<<Location-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "Location DESTROYED", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

//    @Override
//    public void onResponseReceived(String response) {
//        if (response != null) {
//            // TODO handle received response from server for location changed
//            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
//            Log.d(TAG, response);
//            Intent serviceIntent = new Intent(this, RemoteMessagingService.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(BundleType.BUNDLE_TYPE, BundleType.NEARBY_FRIENDS);
//            bundle.putString(BundleType.NEARBY_FRIENDS, response);
//            serviceIntent.putExtras(bundle);
//            startService(serviceIntent);
//
//        } else {
//            // TODO: Replace by AlertDialog
//            Toast.makeText(this, "No nearby friends", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public Class getType() {
//        return String.class;
//    }

    private void initialiseLocationTracker() {
        locationSensor = new LocationSensor(this);
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
                            Location previousLocation = dbHelper.getLastLocation();
                            //TODO location data is not stored in SQLite if the change is not significant
                            //less than 100m according to Constants.MIN_DISTANCE_FOR_LOCATION_CHANGE
                            if (locationSensor.isLocationChangedSignificantly(currentBestLocation, previousLocation)) {
                                if (dbHelper.insertLocation(currentBestLocation)) {
                                    String loc = currentBestLocation.toString();
                                    Log.d("LOCATION", loc);
                                    Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_SHORT).show();

                                    SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(getApplicationContext());
//                                    service.setOnResponseListener(LocationTrackingService.this);
                                    service.sendLocation(currentBestLocation, LocationTrackingService.this);

                                }
                            }
                            locationSensor.stop();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResponseReceived(Message<String> result) {
        if (result != null && result.isSuccess()) {
            Toast.makeText(getApplicationContext(), "Location Sent", Toast.LENGTH_SHORT).show();
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(getApplicationContext(), "Unable to send location", Toast.LENGTH_SHORT).show();
        }
    }
}
