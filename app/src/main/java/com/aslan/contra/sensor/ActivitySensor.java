package com.aslan.contra.sensor;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aslan.contra.R;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by gobinath on 11/19/15.
 */
public class ActivitySensor implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    /**
     * Tag to log the events.
     */
    private static final String TAG = "ActivitySensor";

    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    private static final long DETECTION_INTERVAL_IN_MILLISECONDS = 180000;   // 3 minutes
    /**
     * Singleton instance.
     */
    private static ActivitySensor instance;
    /**
     * Google API client to track the activity. Create only one instance of this client.
     */
    private GoogleApiClient googleApiClient;
    /**
     * Application context.
     */
    private Context context;
    /**
     * Flag indicating whether the sensor is running or not.
     */
    private boolean running;

    /**
     * Service PendingIntent used to receive the events.
     */
    private PendingIntent pendingIntent;

    /**
     * Private constructor to enforce the singleton behaviour.
     *
     * @param context
     */
    private ActivitySensor(Context context) {
        this.context = context;

        // Create Google API client
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

    }

    /**
     * Singleton factor method.
     *
     * @param context
     * @return
     */
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

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
//        int icon = R.mipmap.ic_launcher;
//        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent =
//                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//
//        //TODO resolve deprecated method call
////        notification.setLatestEventInfo(context, title, message, intent);
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0, notification);


// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        mBuilder.setAutoCancel(true);

        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    /**
     * Start tracking activities.
     */
    public void start() {
        if (this.running) {
            // Already running
            return;
        }
        Log.i(TAG, "Starting activity tracker.");
        this.running = true;
        if (!googleApiClient.isConnected()) {
            googleApiClient.blockingConnect();
        }
    }

    /**
     * Stop tracking activities.
     */
    public void stop() {
        if (!this.running) {
            // Already stopped
            return;
        }
        Log.i(TAG, "Stopping activity tracker.");
        this.running = false;

        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                googleApiClient,
                pendingIntent
        ).setResultCallback(this);

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Indicate whether this senosr is up and running.
     *
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(context, ActivityRecognitionReceiverService.class);

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

    /**
     * Called when the Google client is connected. It starts the tracking.
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (running) {
            this.pendingIntent = getActivityDetectionPendingIntent();
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    googleApiClient,
                    DETECTION_INTERVAL_IN_MILLISECONDS,
                    pendingIntent
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

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Define this service in Manifest.
     */
    public static class ActivityRecognitionReceiverService extends IntentService {

        private final DescendingConfidenceComparator DESC_CONF_COMPARATOR;
        private SensorDataSendingServiceClient<Object> sensorDataSendingServiceClient;

        public ActivityRecognitionReceiverService() {
            super("ActivityRecognitionReceiverService");
            this.DESC_CONF_COMPARATOR = new DescendingConfidenceComparator();
        }

        @Override
        public void onCreate() {
            super.onCreate();
            this.sensorDataSendingServiceClient = new SensorDataSendingServiceClient<>(getApplicationContext());
            this.sensorDataSendingServiceClient.setOnResponseListener(new OnResponseListener<Object>() {
                @Override
                public void onResponseReceived(Object result) {
                    // Do nothing
                }

                @Override
                public Class getType() {
                    return Object.class;
                }
            });
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.i(TAG, "Activities detected");

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
            if (detectedActivities.size() > 0) {
                Collections.sort(detectedActivities, DESC_CONF_COMPARATOR);

                DetectedActivity highConfActivity = detectedActivities.get(0);
                int type = highConfActivity.getType();
                int confidence = highConfActivity.getConfidence();
                final String msg = "Type: " + getActivityName(type) + " with confidence: " + confidence + "%";
                Log.i(TAG, msg);

                // Show a notification
                generateNotification(getApplicationContext(),msg);
                // Send the data to the server
                sensorDataSendingServiceClient.sendActivity(type, confidence);
            }

        }

        //Get the activity name
        private String getActivityName(int type) {
            switch (type) {
                case DetectedActivity.IN_VEHICLE:
                    return "In Vehicle";
                case DetectedActivity.ON_BICYCLE:
                    return "On Bicycle";
                case DetectedActivity.ON_FOOT:
                    return "On Foot";
                case DetectedActivity.WALKING:
                    return "Walking";
                case DetectedActivity.STILL:
                    return "Still";
                case DetectedActivity.TILTING:
                    return "Tilting";
                case DetectedActivity.RUNNING:
                    return "Running";
                case DetectedActivity.UNKNOWN:
                    return "Unknown";
            }
            return "N/A";
        }

        /**
         * Comparator to sort the DetectedActivities based on their confidence.
         */
        private class DescendingConfidenceComparator implements Comparator<DetectedActivity> {

            @Override
            public int compare(DetectedActivity lhs, DetectedActivity rhs) {
                int confLhs = lhs.getConfidence();
                int confRhs = rhs.getConfidence();
                // Compare in the reverse order
                return confLhs - confRhs;
            }
        }

    }
}
