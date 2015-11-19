package com.aslan.contra.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.util.Constants;
import com.aslan.contra.util.DatabaseHelper;
import com.aslan.contra.util.IntentCreator;
import com.aslan.contra.util.RunningServices;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataRetrievingServiceClient;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;

public class RemoteMessagingService extends Service implements ServiceConnection {
    private static final String TAG = "RemoteMessagingService";

    private Messenger receiver;
    private Messenger sender;
    private Intent intent;

    @Override
    public IBinder onBind(Intent arg0) {
        receiver = new Messenger(new IncomingMessageHandler());
        return receiver.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            this.intent = intent;

            Intent bindingIntent = new Intent();
            bindingIntent.setAction(Constants.FRIEND_FINDER_APP_ACTION_NAME); //TODO change the actual action name logical
            bindingIntent = IntentCreator.createExplicitFromImplicitIntent(getApplicationContext(), bindingIntent); //solution for failure above android 5.0
            bindService(bindingIntent, this, BIND_AUTO_CREATE);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("RemoteService", "onServiceConnected");
        Log.d("RemoteService", name.toString());
        Log.d("RemoteService", service.toString());
        sender = new Messenger(service);

        // Extract the information
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            switch (bundle.getString(Constants.BUNDLE_TYPE)) {
                case Constants.Type.NEARBY_FRIENDS:
                    sendNearbyFriends(bundle.getString(Constants.Type.NEARBY_FRIENDS));
                    break;
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("RemoteService", "onServiceConnected");
        Log.d("RemoteService", name.toString());
        sender = null;
    }


    public void sendNearbyFriends(String response) {
        Message msg = Message.obtain(null, Constants.MessagePassingCommands.NEARBY_FRIENDS_RECEIVED, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Type.NEARBY_FRIENDS, response);
        msg.setData(bundle);
        try {
            sender.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class IncomingMessageHandler extends Handler {

        private DatabaseHelper dbHelper;

        private Messenger resultSender;

        private IncomingMessageHandler() {
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        @Override
        public void handleMessage(final Message msg) {
            this.resultSender = msg.replyTo;

            // super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MessagePassingCommands.START_LOCATION_TRACKING:
                    if (!RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        Intent serviceIntent = new Intent(RemoteMessagingService.this, LocationTrackingService.class);
                        serviceIntent.addCategory(LocationTrackingService.TAG);
                        startService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Location Tracking Started @ PLUGIN", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Tracking service is already running", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MessagePassingCommands.STOP_LOCATION_TRACKING:
                    if (RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        Intent serviceIntent = new Intent(RemoteMessagingService.this, LocationTrackingService.class);
                        serviceIntent.addCategory(LocationTrackingService.TAG);
                        stopService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Location Tracking Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Tracking service is not running", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MessagePassingCommands.GET_ALL_CONTACTS:
                    SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(getApplicationContext());
                    service.setOnResponseListener(new OnResponseListener<String>() {
                        @Override
                        public void onResponseReceived(String response) {
                            if (response != null) {
                                // TODO handle received response from server for location changed
                                Toast.makeText(RemoteMessagingService.this, response, Toast.LENGTH_LONG).show();
                            } else {
                                // TODO: Replace by AlertDialog
                                Toast.makeText(RemoteMessagingService.this, "No nearby friends", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public Class getType() {
                            return String.class;
                        }
                    });
                    service.sendContacts();
                    break;
                case Constants.MessagePassingCommands.EXPORT_LOCATION_DATA_TO_SD_CARD:
                    if (!RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
                        dbHelper.exportToSdCard(getApplicationContext());
                    } else {
                        Toast.makeText(getApplicationContext(), "Stop Tracking service first and try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MessagePassingCommands.GET_NEARBY_FRIENDS:

                    SensorDataRetrievingServiceClient retrievalService = new SensorDataRetrievingServiceClient(getApplicationContext());
                    retrievalService.setOnResponseListener(new OnResponseListener<String>() {
                        @Override
                        public void onResponseReceived(String result) {
                            Log.i(TAG, "Result: " + result);

                            Message mes = Message.obtain(null, Constants.MessagePassingCommands.NEARBY_FRIENDS_RECEIVED);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.Type.NEARBY_FRIENDS, result);
                            mes.setData(bundle);
                            try {
                                resultSender.send(mes);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
//                            Intent resultIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.aslan.friendsfinder");
//                            startActivity(resultIntent);
//                            Log.d("remote service", resultIntent.toString());
                        }

                        @Override
                        public Class getType() {
                            return String.class;
                        }
                    });
                    retrievalService.getNearbyFriends();
                    break;
            }
        }
    }
}