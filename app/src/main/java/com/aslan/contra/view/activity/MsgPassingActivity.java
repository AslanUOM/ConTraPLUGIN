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

public class MsgPassingActivity extends AppCompatActivity {

    static final int SAY_BYE = 0;
    static final int SAY_SEE_YOU = 1;
    boolean mIsBinded;
    Messenger mMessenger;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            mIsBinded = false;
            mServiceConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            // TODO Auto-generated method stub
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
        bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);
        Button mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Message msg = Message.obtain(null, SAY_BYE, 0, 0);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        Button mButton2 = (Button) findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Message msg = Message.obtain(null, SAY_SEE_YOU, 0, 0);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

}
