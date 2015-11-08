package com.aslan.contra.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import com.aslan.contra.util.Constants;
import com.aslan.contra.util.DatabaseHelper;
import com.aslan.contra.util.RunningServices;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;

public class RemoteService extends Service implements OnResponseListener<String> {
    Messenger mMessenger = new Messenger(new RemoteServiceHandler());

    @Override
    public IBinder onBind(Intent arg0) {
        return mMessenger.getBinder();
    }

    @Override
    public void onResponseReceived(String response) {
        if (response != null) {
            // TODO handle received response from server for location changed
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(this, "Unable to register the user", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Class getType() {
        return String.class;
    }

    private class RemoteServiceHandler extends Handler {

        private DatabaseHelper dbHelper;

        private RemoteServiceHandler() {
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MessagePassingCommands.START_LOCATION_TRACKING:
                    if (!RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        Intent serviceIntent = new Intent(RemoteService.this, LocationTrackingService.class);
                        serviceIntent.addCategory(LocationTrackingService.TAG);
                        startService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Location Tracking Started @ PLUGIN", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Tracking service is already running", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MessagePassingCommands.STOP_LOCATION_TRACKING:
                    if (RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        Intent serviceIntent = new Intent(RemoteService.this, LocationTrackingService.class);
                        serviceIntent.addCategory(LocationTrackingService.TAG);
                        stopService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Location Tracking Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Tracking service is not running", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MessagePassingCommands.GET_ALL_CONTACTS:
                    SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(getApplicationContext());
                    service.setOnResponseListener(RemoteService.this);
                    service.sendContacts();
                    break;
                case Constants.MessagePassingCommands.EXPORT_LOCATION_DATA_TO_SD_CARD:
                    if (!RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        dbHelper.exportToSdCard(getApplicationContext());
                    } else {
                        Toast.makeText(getApplicationContext(), "Stop Tracking service first and try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}