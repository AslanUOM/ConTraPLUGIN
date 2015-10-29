package com.aslan.contra.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.aslan.contra.R;
import com.aslan.contra.util.Utility;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        Toast.makeText(this, countryCodeValue, Toast.LENGTH_LONG).show();
        //List<String> emails = Utility.getAccountEmail(getApplicationContext());

    }
}
