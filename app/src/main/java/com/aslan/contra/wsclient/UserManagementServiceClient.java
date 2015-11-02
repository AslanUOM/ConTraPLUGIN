package com.aslan.contra.wsclient;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by gobinath on 10/30/15.
 */
public class UserManagementServiceClient extends ServiceClient<String, String> {
    // URL of the service
    private static final String SERVICE_URL = "http://10.0.2.2:8080/ConTra/user/register";

    public void registerUser(String country, String phoneNumber) {
        HttpRequestTask task = new HttpRequestTask();
        task.execute();
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add("phone", "0770780210");
                formData.add("country", "lk");

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<String> response = restTemplate.exchange(SERVICE_URL, HttpMethod.POST, requestEntity,
                        String.class);

                // Return the response body to display to the user
                return response.getBody();
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String greeting) {
            System.out.println(greeting);
        }

    }
}
