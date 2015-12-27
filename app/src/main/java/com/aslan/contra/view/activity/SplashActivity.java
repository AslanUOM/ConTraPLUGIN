package com.aslan.contra.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aslan.contra.R;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // Show the splash screen
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Constants.SPLASH_VISIBLE_TIME);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                boolean firstRun = Utility.isFirstRun(getApplicationContext());
                Log.i("Splash Activity", "" + firstRun);
                Class target;
                // TODO: Check first run and change the activity
                if (firstRun) {
                    target = RegisterActivity.class;
                } else if (Utility.isUserSignedIn(getApplicationContext()).equals(Constants.SIGNED_OUT)) {
                    //TODO should be login activity
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
                SplashActivity.this.finish();
            }
        }.start();
    }
}
