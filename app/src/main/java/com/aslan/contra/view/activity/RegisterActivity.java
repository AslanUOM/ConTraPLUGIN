package com.aslan.contra.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aslan.contra.R;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.UserManagementServiceClient;

public class RegisterActivity extends AppCompatActivity implements OnResponseListener<String> {
    // UI components
    private Button btnSignIn;
    private EditText etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find the UI components
        this.btnSignIn = (Button) findViewById(R.id.btnSignIn);
        this.etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

        // TODO: Automatically get the phone number and fill the EdiText

        // Set OnClickListener to the button
        this.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClicked();
            }
        });
    }

    private void onSignInClicked() {
        // TODO: Check the internet conection

        // Read the phone number
        String phoneNumber = etPhoneNumber.getText().toString();
        String deviceName = Utility.getDeviceName(getApplicationContext());
        String deviceSerial = Utility.getDeviceSerial(getApplicationContext());


        UserManagementServiceClient service = new UserManagementServiceClient();
        service.setOnResponseListener(this);

        // Country is hardcoded as Sri Lanka
        service.registerUser("lk", phoneNumber, deviceName, deviceSerial);
    }

    @Override
    public void onResponseReceived(String userID) {
        if (userID != null) {
            // Save the user-id
            Utility.saveUserId(getApplicationContext(), userID);

            // Move to the MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(Constants.COMMAND, Constants.SHOW_PROFILE);
            startActivity(intent);
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(this, "Unable to register the user", Toast.LENGTH_LONG).show();
        }
    }
}
