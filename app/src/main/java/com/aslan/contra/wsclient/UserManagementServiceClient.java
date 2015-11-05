package com.aslan.contra.wsclient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by gobinath on 10/30/15.
 */
public class UserManagementServiceClient<T> extends ServiceClient<T> {
    private final Context context;

    public UserManagementServiceClient(Context context) {
        this.context = context;
    }

    public void registerUser(String country, String phoneNumber, String deviceName, String deviceSerial) {
        UserRegistrationTask task = new UserRegistrationTask();
        task.execute(country, phoneNumber, deviceName, deviceSerial);
    }

    public void retrieveUserProfile(String userId) {
        ProfileRetrievalTask task = new ProfileRetrievalTask();
        task.execute(userId);
    }

    public void updateUserProfile(String userId, String name, String email) {
        ProfileUpdateTask task = new ProfileUpdateTask();
        task.execute(userId, name, email);
    }

    /**
     * Register this device and return the device token. This method must be invoked inside an AsyncTask.
     *
     * @return
     */
    private String deviceToken() throws IOException {
        String deviceToken = null;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        deviceToken = gcm.register(Constants.SENDER_ID);
        // Persist the regID - no need to register again.
        if (deviceToken != null) {
            Utility.saveDeviceToken(context, deviceToken);
        }
        return deviceToken;
    }

    private void sendResult(T result) {
        OnResponseListener<T> listener = getOnResponseListener();
        if (listener != null) {
            listener.onResponseReceived(result);
        }
    }

    /**
     * AsyncTask to register the user.
     */
    private class UserRegistrationTask extends AsyncTask<String, Void, T> {
        @Override
        protected T doInBackground(String... params) {
            try {
                // Get the device token
                String deviceToken = deviceToken();

                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add("country", params[0]);
                formData.add("phone", params[1]);
                formData.add("deviceName", params[2]);
                formData.add("deviceSerial", params[3]);
                // TODO: Update the GCM token here
                formData.add("deviceToken", deviceToken);


                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<T> response = restTemplate.exchange(Constants.WebServiceUrls.REGISTER_USER_SERVICE_URL, HttpMethod.POST, requestEntity,
                        getOnResponseListener().getType());

                if (response.getStatusCode().value() == 201) {
                    // Return the response body to display to the user
                    return response.getBody();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(T result) {
            sendResult(result);
        }
    }

    /**
     * AsyncTask to derive the name and email of the user.
     */
    private class ProfileRetrievalTask extends AsyncTask<String, Void, T> {
        @Override
        protected T doInBackground(String... params) {
            try {
                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Add the String message converter
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Make the HTTP GET request, marshaling the response to a String
                ResponseEntity<T> response = restTemplate.getForEntity(Constants.WebServiceUrls.RETRIEVE_USER_PROFILE_URL, getOnResponseListener().getType(), params[0]);
                if (response.getStatusCode().value() == 201) {
                    // Return the response body to display to the user
                    return response.getBody();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(T result) {
            sendResult(result);
        }

    }

    /**
     * AsyncTask to update the name and email of the user.
     */
    private class ProfileUpdateTask extends AsyncTask<String, Void, T> {
        @Override
        protected T doInBackground(String... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add(Constants.USER_ID, params[0]);
                formData.add(Constants.NAME, params[1]);
                formData.add(Constants.EMAIL, params[2]);

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<T> response = restTemplate.exchange(Constants.WebServiceUrls.UPDATE_USER_PROFILE_URL, HttpMethod.POST, requestEntity,
                        getOnResponseListener().getType());

                if (response.getStatusCode().value() == 201) {
                    // Return the response body to display to the user
                    return response.getBody();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(T result) {
            sendResult(result);
        }

    }

}
