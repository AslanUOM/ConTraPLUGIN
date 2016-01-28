package com.aslan.contra.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aslan.contra.R;
import com.aslan.contra.dto.ws.Message;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;
import com.aslan.contra.wsclient.ServiceConnector;
import com.aslan.contra.wsclient.UserManagementServiceClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterActivity extends AppCompatActivity implements ServiceConnector.OnResponseListener<String> {
    private static final String TAG = "RegisterActivity";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private String countryISOCode;
    private String countryZipCode;

    // UI components
    private Button btnSignIn;
    private EditText etCountry;
    private EditText etPhoneNumber;
    private ProgressDialog progressDialog;


    private AtomicInteger msgId = new AtomicInteger();
    private String deviceToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find the UI components
        this.btnSignIn = (Button) findViewById(R.id.btnSignIn);
        this.etCountry = (EditText) findViewById(R.id.etCountry);
        this.etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

        this.etCountry.setText("+" + getCountryZipCode());
        this.etPhoneNumber.requestFocus();

        // Set OnClickListener to the button
        this.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClicked();
            }
        });
    }

    public String getCountryZipCode() {
        String countryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        countryISOCode = manager.getSimCountryIso().toUpperCase().trim();
        countryZipCode = Utility.toCountryZIPCode(this, countryISOCode);

        return countryZipCode;
    }


    private void onSignInClicked() {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            if (checkPlayServices()) {

                // Validate the zip code for modification
                String modifiedZipCode = etCountry.getText().toString().replaceFirst("\\+", "");
                if (!modifiedZipCode.equals(countryZipCode)) {
                    String modifiedISOCode = Utility.toCountryISOCode(this, modifiedZipCode);
                    if (modifiedISOCode == null) {
                        Toast.makeText(this, "Invalid country code", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        countryISOCode = modifiedISOCode;
                        countryZipCode = modifiedZipCode;
                    }
                }

                // Show progress dialog while retrieving information from server
                this.progressDialog = ProgressDialog.show(this, "", "Please wait...");

                // Read the phone number
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                if (Utility.getUserId(getApplicationContext()) == null || !Utility.getUserId(getApplicationContext()).equals(phoneNumber)) {
                    new RegisterTask().execute(countryISOCode, phoneNumber);
                } else {
                    Utility.saveUserSignedIn(getApplicationContext(), true);
                    // Move to the MainActivity home fragment
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            askToEnableNetwork();
        }

//        if (Utility.isNetworkAvailable(getApplicationContext())) {
//            // Check device for Play Services APK.
//            if (checkPlayServices()) {
//                // If this check succeeds, proceed with normal processing.
//                // Otherwise, prompt user to get valid Play Services APK.
//                gcm = GoogleCloudMessaging.getInstance(context);
//                deviceToken = Utility.getDeviceToken(context);
//
//                if (deviceToken == null || deviceToken.isEmpty()) {
//                    registerInBackground();
//                }
//            } else {
//                Log.i(TAG, "No valid Google Play Services APK found.");
//            }
//        } else {
//            askToEnableNetwork();
//        }
    }

    @Override
    public void onResponseReceived(Message<String> message) {
        // Hide the progress dialog
        progressDialog.cancel();
        progressDialog.dismiss();
        progressDialog = null;

        if (message.isSuccess()) {
            // Save the user-id
            Utility.saveUserId(getApplicationContext(), message.getEntity());
            Utility.saveUserSignedIn(getApplicationContext(), true);

            // Move to the MainActivity permission fragment
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(Constants.COMMAND, Constants.SHOW_PROFILE);
            startActivity(intent);
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(this, "Unable to register the user", Toast.LENGTH_LONG).show();
        }
    }


    // Show alert dialog to confirm and enable the network
    private void askToEnableNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_request_msg)
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


//    /**
//     * Registers the application with GCM servers asynchronously.
//     * <p/>
//     * Stores the registration ID and app versionCode in the application's
//     * shared preferences.
//     */
//    private void registerInBackground() {
//        new MyHttpAsyncTask().execute(null, null, null);
//    }

//    /**
//     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
//     * or CCS to send messages to your app. Not needed for this demo since the
//     * device sends upstream messages to a server that echoes back the message
//     * using the 'from' address in the message.
//     */
//    private void sendRegistrationIdToBackend(String deviceToken) {
//        // TODO ADD gcm token sending. Your implementation here.
//
//        // Read the phone number
//        String phoneNumber = etPhoneNumber.getText().toString();
//        String deviceName = Utility.getDeviceName(getApplicationContext());
//        String deviceSerial = Utility.getDeviceSerial(getApplicationContext());
//
//
//        UserManagementServiceClient service = new UserManagementServiceClient(context);
//        service.setOnResponseListener(this);
//
//        // Country is hardcoded as Sri Lanka
//        service.registerUser("lk", phoneNumber, deviceName, deviceSerial);
//    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Google Play Service", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private class RegisterTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String token = null;
            try {
                token = Utility.regDeviceToken(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserManagementServiceClient service = new UserManagementServiceClient(getApplicationContext());
            //service.setOnResponseListener(this);
            // Country is derived automatically from current location using google reverse geo-coding API
            service.registerUser(params[0], params[1], RegisterActivity.this);
            return null;
        }
    }

//    private class MyHttpAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            String msg = "";
//            try {
//                if (gcm == null) {
//                    gcm = GoogleCloudMessaging.getInstance(context);
//                }
//                deviceToken = gcm.register(SENDER_ID);
//                msg = "Device registered, registration ID=" + deviceToken;
//
//            } catch (IOException ex) {
//                msg = "Error :" + ex.getMessage();
//                // If there is an error, don't just keep trying to register.
//                // Require the user to click a button again, or perform
//                // exponential back-off.
//            }
//            return msg;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null) {
//
//                // You should send the registration ID to your server over HTTP,
//                // so it can use GCM/HTTP or CCS to send messages to your app.
//                // The request to your server should be authenticated if your app
//                // is using accounts.
//                sendRegistrationIdToBackend(deviceToken);
//
//                // For this demo: we don't need to send it because the device
//                // will send upstream messages to a server that echo back the
//                // message using the 'from' address in the message.
//
//                // Persist the regID - no need to register again.
//                Utility.saveDeviceToken(context, deviceToken);
//
////                Intent openMainActivity = new Intent(RegisterActivity.this, MainActivity.class);
////                Log.i("LOGIN", "Call to Main");
////                startActivity(openMainActivity);
////                RegisterActivity.this.finish();
//            } else {
//                // invoked when no data received due to error in internet
//                // connection
//                AlertDialog.Builder builder = new AlertDialog.Builder(
//                        RegisterActivity.this);
//                builder.setMessage(R.string.internet_error_msg)
//                        .setTitle("Unable to retrive data from internet")
//                        .setCancelable(false)
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int id) {
//                                        RegisterActivity.this.finish();
//                                    }
//                                });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        }
//
//    }
}
