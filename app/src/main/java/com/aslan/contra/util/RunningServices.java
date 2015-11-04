package com.aslan.contra.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Vishnuvathsasarma on 04-Nov-15.
 */
public class RunningServices {
    private static RunningServices instance;

    private RunningServices() {
    }

    public static RunningServices getInstance() {
        if (instance == null) {
            instance = new RunningServices();
        }
        return instance;
    }

    public boolean isLocationServiceRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.aslan.contra.services.LocationTrackingService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
