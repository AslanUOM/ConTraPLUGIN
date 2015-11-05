package com.aslan.contra.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import com.aslan.contra.util.Msg;
import com.aslan.contra.util.RunningServices;

public class RemoteService extends Service {
    Messenger mMessenger = new Messenger(new RemoteServiceHandler());

    @Override
    public IBinder onBind(Intent arg0) {
        return mMessenger.getBinder();
    }

    private class RemoteServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Msg.START_LOCATION_TRACKING:
                    if (!RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        Intent serviceIntent = new Intent(RemoteService.this, LocationTrackingService.class);
                        serviceIntent.addCategory(LocationTrackingService.TAG);
                        startService(serviceIntent);
                    }
                    Toast.makeText(getApplicationContext(), "Location Tracking Started @ PLUGIN", Toast.LENGTH_LONG).show();
                    break;
                case Msg.STOP_LOCATION_TRACKING:
                    if (RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        Intent serviceIntent = new Intent(RemoteService.this, LocationTrackingService.class);
                        serviceIntent.addCategory(LocationTrackingService.TAG);
                        stopService(serviceIntent);
                    }
                    Toast.makeText(getApplicationContext(), "Location Tracking Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}