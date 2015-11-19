package com.aslan.contra.sensor;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by gobinath on 11/19/15.
 */
public class ActivitySensor implements ResultCallback<Status> {
    private static final String TAG = "ActivitySensor";

    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    private static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    private GoogleApiClient googleApiClient;

    private Context context;

    private static ActivitySensor instance;

    private boolean running;

    private ActivitySensor(Context context) {
        this.context = context;

        // Create Google API client
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i(TAG, "Connected to GoogleApiClient");

                        Log.i(TAG, "Registering for activity events.");
                        if (running) {
                            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                                    googleApiClient,
                                    DETECTION_INTERVAL_IN_MILLISECONDS,
                                    getActivityDetectionPendingIntent()
                            ).setResultCallback(ActivitySensor.this);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // The connection to Google Play services was lost for some reason. We call connect() to
                        // attempt to re-establish the connection.
                        Log.i(TAG, "Connection suspended");
                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
                    }
                })
                .addApi(ActivityRecognition.API)
                .build();

    }

    public static ActivitySensor getInstance(Context context) {
        if (instance == null) {
            synchronized (ActivitySensor.class) {
                if (instance == null) {
                    instance = new ActivitySensor(context);
                }
            }
        }

        return instance;
    }

    public void start() {
        Log.i(TAG, "Starting activity tracker.");
        this.running = true;

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
//
////        if (!googleApiClient.isConnected()) {
////            Log.w(TAG, "Google API client is not connected.");
////            return;
////        }
//        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
//                googleApiClient,
//                DETECTION_INTERVAL_IN_MILLISECONDS,
//                getActivityDetectionPendingIntent()
//        ).setResultCallback(this);
    }

    public void stop() {
        Log.i(TAG, "Stopping activity tracker.");
        this.running = false;

//        if (!googleApiClient.isConnected()) {
//            Log.w(TAG, "Google API client is not connected.");
//            return;
//        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                googleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    public void destroy() {
        stop();

    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(context, ActivityRecognitionService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            Log.i(TAG, "Google API client is triggered successfully.");

        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    public static class ActivityRecognitionService extends IntentService {

        public ActivityRecognitionService() {
            super("ActivityRecognitionService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            // Log each activity.
            Log.i(TAG, "Activities detected");
            for (DetectedActivity da : detectedActivities) {
                int type = da.getType();
                // TODO: Send the type directly to the server and do the convertion in server
                String strType = convertToString(type);
                int confidence = da.getConfidence();
                String msg = strType + " - " + type + " with confidence: " + confidence + "%";
                Log.i(TAG, msg);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }

            // Broadcast the list of detected activities.
            //Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
            //localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

        private String convertToString(int type) {
            String strType;
            switch (type) {
                case DetectedActivity.IN_VEHICLE:
                    strType = "IN_VEHICLE";
                case DetectedActivity.ON_BICYCLE:
                    strType = "ON_BICYCLE";
                case DetectedActivity.ON_FOOT:
                    strType = "ON_FOOT";
                case DetectedActivity.RUNNING:
                    strType = "RUNNING";
                case DetectedActivity.STILL:
                    strType = "STILL";
                case DetectedActivity.TILTING:
                    strType = "TILTING";
                case DetectedActivity.UNKNOWN:
                    strType = "UNKNOWN";
                case DetectedActivity.WALKING:
                    strType = "WALKING";
                default:
                    strType = "UNABLE TO DEFINE";
            }
            return strType;
        }
    }


}
