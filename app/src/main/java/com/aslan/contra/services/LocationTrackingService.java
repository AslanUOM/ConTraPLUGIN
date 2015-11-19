package com.aslan.contra.services;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.listeners.OnLocationChangedListener;
import com.aslan.contra.sensor.LocationSensor;
import com.aslan.contra.sensor.WiFiSensor;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.DatabaseHelper;
import com.aslan.contra.util.IntentCreator;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class LocationTrackingService extends IntentService implements OnResponseListener<String> {

    public static final String TAG = "LocationTrackingService";
    public static boolean isIntentServiceRunning = false;
    public static Runnable runnable = null;
    public Handler handler = null;
    boolean mIsBinded;
    Messenger mMessenger;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBinded = false;
            mServiceConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mIsBinded = true;
            mMessenger = new Messenger(arg1);
        }
    };
    //    private final long TIME_INTERVAL = 1800000L;
    private DatabaseHelper dbHelper;
    private LocationSensor locationSensor;
    private Location currentBestLocation;
    private int counter = 0;
    private WiFiSensor wifiSensor;
    private Intent intent;

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

        Intent mIntent = new Intent();
        mIntent.setAction(Constants.FRIEND_FINDER_APP_ACTION_NAME); //TODO change the actual action name logical
        mIntent = IntentCreator.createExplicitFromImplicitIntent(getApplicationContext(), mIntent); //solution for failure above android 5.0
        bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);

        dbHelper = new DatabaseHelper(getApplicationContext());

        initialiseLocationTracker();
        //TODO uncomment WiFi tracking when implemented in server side
//        initialiseWifiTracker();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                onHandleIntent(LocationTrackingService.this.intent);
                locationSensor.start();
                //TODO uncomment WiFi tracking when implemented in server side
//                wifiSensor.start();
                Log.d("<<Tracking-onStart>>", "I am alive");
                Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, Constants.LocationTracking.MIN_TIME_BW_UPDATES);
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

    @Override
    public void onResponseReceived(String response) {
        if (response != null) {
            // TODO handle received response from server for location changed
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            Log.d(TAG, response);
            Message msg = Message.obtain(null, Constants.MessagePassingCommands.NEARBY_FRIENDS_RECEIVED, 0, 0);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.NEARBY_FRIENDS, response);
            msg.obj = bundle;
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(this, "No nearby friends", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Class getType() {
        return String.class;
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
                            Location previousLocation = dbHelper.getLastLocation();
                            //TODO location data is not stored in SQLite if the change is not significant
                            //less than 100m according to Constants.MIN_DISTANCE_FOR_LOCATION_CHANGE
                            if (locationSensor.isLocationChangedSignificantly(currentBestLocation, previousLocation)) {
                                if (dbHelper.insertLocation(currentBestLocation)) {
                                    String loc = currentBestLocation.toString();
                                    Log.d("LOCATION", loc);
                                    Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_SHORT).show();

                                    SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(getApplicationContext());
                                    service.setOnResponseListener(LocationTrackingService.this);
                                    service.sendLocation(currentBestLocation);

                                }
                            }
                            locationSensor.stop();
                        }
                    }
                }
            }
        });
    }

    //TODO uncomment WiFi tracking when implemented in server side
//    private void initialiseWifiTracker() {
//        wifiSensor = new WiFiSensor(this);
//        wifiSensor.setOnWifiScanResultChangedLsitener(new OnWifiScanResultChangedListener() {
//            @Override
//            public void onWifiScanResultsChanged(List<ScanResult> wifiList) {
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                Log.e("TIME", timestamp.toString());
//                for (ScanResult wifi : wifiList) {
//                    if (dbHelper.insertWifi(wifi, timestamp.toString())) {
//                        Log.d("WIFI", wifi.toString());
//                        Toast.makeText(getApplicationContext(), wifi.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                wifiSensor.stop();
//            }
//        });
//    }
}
