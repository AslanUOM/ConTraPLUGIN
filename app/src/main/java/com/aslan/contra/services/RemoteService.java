package com.aslan.contra.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class RemoteService extends Service {
    static final int SAY_HI = 0;
    static final int SAY_HELLO = 1;
    Messenger mMessenger = new Messenger(new RemoteServiceHandler());

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mMessenger.getBinder();
    }

    private class RemoteServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case SAY_HI:
                    Toast.makeText(getApplicationContext(), "Hi from APP @ PLUGIN", Toast.LENGTH_LONG).show();
                    break;
                case SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "Hello from APP @ PLUGIN", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}