package com.aslan.contra.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aslan.contra.R;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Show the splash screen
        new Thread() {
            @Override
            public void run() {
                boolean firstRun = Utility.isFirstRun(getApplicationContext());
                Log.i("Splash Activity", "" + firstRun);
                Class target;
                // TODO: Check first run and change the activity
                if (firstRun) {
                    target = RegisterActivity.class;
                } else {
                    target = MainActivity.class;
                }

                Intent intent = new Intent(getApplicationContext(), target);
                // Wait for a while
                try {
                    Thread.sleep(Constants.SPLASH_VISIBLE_TIME);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Sleep is interrupted", e);
                }
                startActivity(intent);
            }
        }.start();
    }
}
