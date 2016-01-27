package com.aslan.contra.wsclient;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aslan.contra.dto.common.Time;
import com.aslan.contra.dto.ws.UserLocation;
import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.sensor.ContactsSensor;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.List;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
public class SensorDataSendingServiceClient<T> extends ServiceClient<T> {
    private final String TAG = SensorDataSendingServiceClient.class.getName();

    private final Context context;

    public SensorDataSendingServiceClient(Context context) {
        this.context = context;
    }

    public void sendLocation(Location currentBestLocation, ServiceConnector.OnResponseListener<String> listener) {
        com.aslan.contra.dto.common.Location location = new com.aslan.contra.dto.common.Location();
        location.setName(currentBestLocation.getProvider());
        location.setLatitude(currentBestLocation.getLatitude());
        location.setLongitude(currentBestLocation.getLongitude());

        UserLocation userLocation = new UserLocation();
        userLocation.setLocation(location);
        userLocation.setAccuracy(currentBestLocation.getAccuracy());
        userLocation.setTime(Time.valueOf(currentBestLocation.getTime()));
        userLocation.setUserID(Utility.getUserId(context));
        userLocation.setDeviceID(Utility.getDeviceSerial(context));

        Request<UserLocation> request = new Request<>();
        request.setEntity(userLocation);
        request.setHttpMethod(HttpMethod.POST);
        request.setUrl(Constants.WebServiceUrls.SEND_LOCATION_SENSOR_DATA_URL);

        ServiceConnector<UserLocation, String> serviceConnector = new ServiceConnector<>(listener);
        serviceConnector.execute(request);

//        SensorResponse response = new SensorResponse();
//        SensorData locData = new SensorData();
//        locData.setType(Constants.Type.LOCATION);
//        locData.setSource(currentBestLocation.getProvider());
//        locData.setTime(currentBestLocation.getTime());
//        locData.setAccuracy(currentBestLocation.getAccuracy());
//        locData.setData(new String[]{
//                currentBestLocation.getLatitude() + "",
//                currentBestLocation.getLongitude() + ""});
//        response.addSensorData(locData);
//        response.setUserID(Utility.getUserId(context));

//        SensorDataSendingTask task = new SensorDataSendingTask();
//        task.execute(response);
    }

    public void sendActivity(int activity, int confidence) {
        SensorResponse response = new SensorResponse();
        SensorData locData = new SensorData();
        locData.setType(Constants.Type.ACTIVITY);
        locData.setSource("Google Play Services");
        locData.setTime(System.currentTimeMillis());
        locData.setAccuracy(confidence);
        locData.setData(new String[]{Integer.toString(activity)});
        response.addSensorData(locData);
        response.setUserID(Utility.getUserId(context));

//        SensorDataSendingTask task = new SensorDataSendingTask();
//        task.execute(response);
    }

    public void sendContacts() {
//        ContactCollectionAsyncTask task = new ContactCollectionAsyncTask();
//        task.execute();
    }

//    private void sendResult(T result) {
//        OnResponseListener<T> listener = getOnResponseListener();
//        if (listener != null) {
//            listener.onResponseReceived(result);
//        }
//    }

    /**
     * AsyncTask to collect contacts from user.
     */
    private class ContactCollectionAsyncTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            ContactsSensor contactsSensor = new ContactsSensor(context);
            List<String> contacts = contactsSensor.collect();
            return contacts;
        }

        @Override
        protected void onPostExecute(List<String> contacts) {
            super.onPostExecute(contacts);
            Toast.makeText(context, "Contacts requested @ PLUGIN\nFound " + contacts.size() + " contacts", Toast.LENGTH_LONG).show();
            SensorResponse response = new SensorResponse();
            SensorData contactData = new SensorData();
            contactData.setType(Constants.Type.CONTACTS);
            contactData.setTime(new Date().getTime());
            contactData.setData(contacts.toArray(new String[0]));

            response.addSensorData(contactData);
            response.setUserID(Utility.getUserId(context));

//            SensorDataSendingTask task = new SensorDataSendingTask();
//            task.execute(response);
        }
    }

//    /**
//     * AsyncTask to send the sensor data to server.
//     */
//    private class SensorDataSendingTask extends AsyncTask<SensorResponse, Void, T> {
//        @Override
//        protected T doInBackground(SensorResponse... params) {
//            try {
//                // Create HttpHeaders
//                HttpHeaders requestHeaders = new HttpHeaders();
//
//                // Set the content type
//                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//
//                // Populate the serialized sensorResponse and headers in an HttpEntity object to use for the request
//                HttpEntity<SensorResponse> requestEntity = new HttpEntity<SensorResponse>(params[0], requestHeaders);
//
//                // Create a new RestTemplate instance
//                RestTemplate restTemplate = new RestTemplate(true);
//
//                // Make the network request, posting the message, expecting a String in response from the server
//                ResponseEntity<T> response = restTemplate.exchange(Constants.WebServiceUrls.SEND_SENSOR_DATA_URL, HttpMethod.POST, requestEntity,
//                        getOnResponseListener().getType());
//
//                // TODO: After updating in server compare the status with Constants.HTTP_OK
//                if (response.getStatusCode().value() == 201) {
//                    // Return the response body to display to the user
//                    return response.getBody();
//                }
//            } catch (Exception e) {
//                Log.e(this.getClass().getName(), e.getMessage(), e);
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(T result) {
//            sendResult(result);
//        }
//    }
}
