package com.aslan.contra.wsclient;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Utility;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
public class SensorDataSendingServiceClient<T> extends ServiceClient<T> {

    private final String SEND_SENSOR_DATA_URL = "http://10.0.2.2:8080/ConTra/sensordatareceiver/save";
    private final Context context;

    public SensorDataSendingServiceClient(Context context) {
        this.context = context;
    }

    public void sendLocation(Location currentBestLocation) {
        SensorResponse response = new SensorResponse();
        SensorData locData = new SensorData();
        locData.setType("location");
        locData.setSource(currentBestLocation.getProvider());
        locData.setTime(currentBestLocation.getTime());
        locData.setAccuracy(currentBestLocation.getAccuracy());
        locData.setData(new String[]{
                currentBestLocation.getLatitude() + "",
                currentBestLocation.getLongitude() + ""});
        response.addSensorData(locData);
        response.setUserID(Utility.getUserId(context));

        SensorDataSendingTask task = new SensorDataSendingTask();
        task.execute(response);
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
    private class SensorDataSendingTask extends AsyncTask<SensorResponse, Void, T> {
        @Override
        protected T doInBackground(SensorResponse... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);


                // Populate the serialized sensorResponse and headers in an HttpEntity object to use for the request
                HttpEntity<SensorResponse> requestEntity = new HttpEntity<SensorResponse>(params[0], requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<T> response = restTemplate.exchange(SEND_SENSOR_DATA_URL, HttpMethod.POST, requestEntity,
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
