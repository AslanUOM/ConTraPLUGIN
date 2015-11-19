package com.aslan.contra.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aslan.contra.util.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by gobinath on 11/19/15.
 */
public class ActivityRecognitionService extends IntentService {

    private static final String TAG = "ActivityRecogService";


    public ActivityRecognitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da : detectedActivities) {
            int type = da.getType();
            // TODO: Send the type directly to the server and do the convertion in server
            String strType = convertToString(type);
            int confidence = da.getConfidence();
            Log.i(TAG, strType + " - " + type + " with confidence: " + confidence + "%");
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
