package com.aslan.contra.util;

import android.app.ActivityManager;
import android.content.Context;

import com.aslan.contra.services.ActivityRecognitionService;
import com.aslan.contra.services.EnvironmentMonitorService;
import com.aslan.contra.services.LocationTrackingService;
import com.aslan.contra.services.NearbyTerminalTrackingService;

/**
 * Created by Vishnuvathsasarma on 04-Nov-15.
 */
public class RunningServices {
    private static RunningServices instance;

    private RunningServices() {
    }

    public static RunningServices getInstance() {
        if (instance == null) {
            synchronized (RunningServices.class) {
                if (instance == null) {
                    instance = new RunningServices();
                }
            }
        }
        return instance;
    }

    public boolean isLocationServiceRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationTrackingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isActivityRecognitionServiceRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ActivityRecognitionService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnvironmentMonitorServiceRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EnvironmentMonitorService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNearbyTerminalTrackingServiceRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NearbyTerminalTrackingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
