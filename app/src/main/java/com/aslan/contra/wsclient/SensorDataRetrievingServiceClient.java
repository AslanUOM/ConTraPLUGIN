package com.aslan.contra.wsclient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Vishnuvathsasarma on 11-Nov-15.
 */
public class SensorDataRetrievingServiceClient<T> extends ServiceClient<T> {
    private final Context context;

    public SensorDataRetrievingServiceClient(Context context) {
        this.context = context;
    }

    public void getNearbyFriends() {
        SensorDataRetrievingTask task = new SensorDataRetrievingTask();
        task.execute(Utility.getUserId(context));
    }

    private void sendResult(T result) {
        OnResponseListener<T> listener = getOnResponseListener();
        if (listener != null) {
            listener.onResponseReceived(result);
        }
    }

    /**
     * AsyncTask to GET the sensor data from server.
     */
    private class SensorDataRetrievingTask extends AsyncTask<String, Void, T> {
        @Override
        protected T doInBackground(String... params) {
            try {
                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Add the String message converter
//                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Make the HTTP GET request, marshaling the response to a String
                ResponseEntity<T> response = restTemplate.getForEntity(Constants.WebServiceUrls.GET_NEARBY_FRIENDS_DATA_URL, getOnResponseListener().getType(), params[0]);

                Log.d("SensorClient", response.toString());

                if (response.getStatusCode().value() == 200) {
                    // Return the response body to display to the user
                    Log.d("SensorDataRetrieving", response.toString());//TODO remove this line
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
