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

import java.util.List;

public class RemoteMessagingService extends Service implements OnResponseListener<String> {
    private Messenger receiver;
    private Messenger sender;
    private boolean senderIsBinded;
    private ServiceConnection messengerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("RemoteService", "onServiceConnected");
            Log.d("RemoteService", name.toString());
            Log.d("RemoteService", service.toString());
            sender = new Messenger(service);
            senderIsBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("RemoteService", "onServiceConnected");
            Log.d("RemoteService", name.toString());
            sender = null;
            senderIsBinded = false;
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        receiver = new Messenger(new IncomingMessageHandler());
        return receiver.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            switch (bundle.getString(Constants.BUNDLE_TYPE)) {
                case Constants.Type.NEARBY_FRIENDS:
                    sendNearbyFriends(bundle.getString(Constants.Type.NEARBY_FRIENDS));
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendNearbyFriends(String response) {
        Intent mIntent = new Intent();
        mIntent.setAction(Constants.FRIEND_FINDER_APP_ACTION_NAME); //TODO change the actual action name logical
        mIntent = IntentCreator.createExplicitFromImplicitIntent(getApplicationContext(), mIntent); //solution for failure above android 5.0
        bindService(mIntent, messengerServiceConnection, BIND_AUTO_CREATE);

        Message msg = Message.obtain(null, Constants.MessagePassingCommands.NEARBY_FRIENDS_RECEIVED, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Type.NEARBY_FRIENDS, response);
        msg.setData(bundle);
        try {
            if (senderIsBinded)
                sender.send(msg);
            else
                Log.d("RemoteService", "sender messenger is NULL");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class IncomingMessageHandler extends Handler {

        private DatabaseHelper dbHelper;

        private IncomingMessageHandler() {
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
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
                    service.setOnResponseListener(RemoteMessagingService.this);
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
                    Log.d("remote service req", msg.toString());
                    SensorDataRetrievingServiceClient retrievalService = new SensorDataRetrievingServiceClient(getApplicationContext());
                    retrievalService.setOnResponseListener(new OnResponseListener<String>() {
                        @Override
                        public void onResponseReceived(String result) {
                            Message mes = Message.obtain(null, Constants.MessagePassingCommands.NEARBY_FRIENDS_RECEIVED);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.Type.NEARBY_FRIENDS, result);
                            mes.setData(bundle);
                            try {
                                msg.replyTo.send(mes);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
//                            Intent resultIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.aslan.friendsfinder");
//                            startActivity(resultIntent);
//                            Log.d("remote service", resultIntent.toString());
                            Log.d("remote service", result);
                        }

                        @Override
                        public Class getType() {
                            return List.class;
                        }
                    });
                    retrievalService.getNearbyFriends();
                    break;
            }
        }
    }


    @Override
    public void onResponseReceived(String response) {
        if (response != null) {
            // TODO handle received response from server for location changed
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(this, "No nearby friends", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Class getType() {
        return String.class;
    }
}