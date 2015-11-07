package com.aslan.contra.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.aslan.contra.R;
import com.aslan.contra.util.IntentCreator;

public class MsgPassingActivity extends AppCompatActivity {

    static final int SAY_BYE = 0;
    static final int SAY_SEE_YOU = 1;
    boolean mIsBinded;
    Messenger mMessenger;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBinded = false;
            mServiceConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mIsBinded = true;
            mMessenger = new Messenger(arg1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_passing);
        Intent mIntent = new Intent();
        mIntent.setAction("aslan.app.RemoteService");
        mIntent = IntentCreator.createExplicitFromImplicitIntent(getApplicationContext(), mIntent); //solution for failure above android 5.0
        bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);
        Button mButton = (Button) findViewById(R.id.btnStart);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message msg = Message.obtain(null, SAY_BYE, 0, 0);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button mButton2 = (Button) findViewById(R.id.btnStop);
        mButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message msg = Message.obtain(null, SAY_SEE_YOU, 0, 0);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
