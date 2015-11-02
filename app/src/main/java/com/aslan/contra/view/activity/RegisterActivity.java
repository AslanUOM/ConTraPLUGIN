package com.aslan.contra.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aslan.contra.R;
import com.aslan.contra.wsclient.UserManagementServiceClient;

public class RegisterActivity extends AppCompatActivity {
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
        UserManagementServiceClient service = new UserManagementServiceClient();
        service.registerUser("lk", "0770780210");
    }
}
