package com.aslan.contra.wsclient;

import android.content.Context;

import com.aslan.contra.dto.common.Device;
import com.aslan.contra.dto.common.Person;
import com.aslan.contra.dto.ws.UserDevice;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Created by gobinath on 10/30/15.
 */
public class UserManagementServiceClient<T> extends ServiceClient<T> {
    private final String TAG = UserManagementServiceClient.class.getName();

    private final Context context;

    public UserManagementServiceClient(Context context) {
        this.context = context;
    }

    public void registerUser(String country, String phoneNumber, ServiceConnector.OnResponseListener<String> listener) {

        Device device = new Device();
        device.setDeviceID(Utility.getDeviceSerial(context));
        device.setApi(Utility.getDeviceAPI(context));
        device.setBluetoothMAC(Utility.getDeviceBtMAC(context));
        device.setManufacturer(Utility.getDeviceName(context));
        device.setToken(Utility.getDeviceToken(context));
        device.setWifiMAC(Utility.getDeviceWiFiMAC(context));
        device.setSensors(Utility.getDeviceSensors(context));
        device.setBatteryLevel(Utility.getBatteryLevel(context));

        UserDevice userDevice = new UserDevice();
        userDevice.setUserID(phoneNumber);
        userDevice.setDevice(device);

        Request<UserDevice> request = new Request<>();
        request.setEntity(userDevice);
        request.setHttpMethod(HttpMethod.POST);
        request.setUrl(Constants.WebServiceUrls.REGISTER_USER_SERVICE_URL);
        request.setUrlVariables(country);

        ServiceConnector<UserDevice, String> serviceConnector = new ServiceConnector<>(listener);
        serviceConnector.execute(request);
    }

    public void retrieveUserProfile(String userId, ServiceConnector.OnResponseListener<Map<String, Object>> listener) {
        Request<String> request = new Request<>();
        request.setHttpMethod(HttpMethod.GET);
        request.setUrl(Constants.WebServiceUrls.RETRIEVE_USER_PROFILE_URL);
        request.setUrlVariables(userId);

        ServiceConnector<String, Map<String, Object>> serviceConnector = new ServiceConnector<>(listener);
        serviceConnector.execute(request);
    }

    public void updateUserProfile(String userId, String name, String email, String[] otherNumbers, ServiceConnector.OnResponseListener<String> listener) {
        Person person = new Person();
        person.setUserID(userId);
        person.setName(name);
        person.setEmail(email);
        person.setPhoneNumbers(otherNumbers);

        Request<Person> request = new Request<>();
        request.setEntity(person);
        request.setHttpMethod(HttpMethod.POST);
        request.setUrl(Constants.WebServiceUrls.UPDATE_USER_PROFILE_URL);

        ServiceConnector<Person, String> serviceConnector = new ServiceConnector<>(listener);
        serviceConnector.execute(request);
    }

//    private void sendResult(T result) {
//        OnResponseListener<T> listener = getOnResponseListener();
//        if (listener != null) {
//            listener.onResponseReceived(result);
//        }
//    }

    /**
     * AsyncTask to register the user.
     private class UserRegistrationTask extends AsyncTask<String, Void, T> {
    @Override protected T doInBackground(String... params) {
    try {
    // Get the device token
    String deviceToken = deviceToken();
    String country = params[0];
    String phoneNumber = params[1];

    // Create HttpHeaders
    HttpHeaders requestHeaders = new HttpHeaders();

    // Set the content type
    //                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

    // Create the parameters
    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("country", params[0]);
    formData.add("phone", params[1]);
    formData.add("deviceName", params[2]);
    formData.add("deviceSerial", params[3]);
    // TODO: Update the GCM token here
    formData.add("deviceToken", deviceToken);

    Device device = new Device();
    device.setDeviceID(Utility.getDeviceSerial(context));
    device.setApi(Utility.getDeviceAPI(context));
    device.setBluetoothMAC(Utility.getDeviceBtMAC(context));
    device.setManufacturer(Utility.getDeviceName(context));
    device.setToken(deviceToken);
    device.setWifiMAC(Utility.getDeviceWiFiMAC(context));
    device.setSensors(Utility.getDeviceSensors(context).toArray(new String[0]));

    UserDevice userDevice = new UserDevice();
    userDevice.setUserID(phoneNumber);
    userDevice.setDevice(device);

    //TODO update request to new form
    // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
    formData, requestHeaders);

    // Create a new RestTemplate instance
    RestTemplate restTemplate = new RestTemplate(true);

    // Make the network request, posting the message, expecting a String in response from the server
    ResponseEntity<Message<String>> response = restTemplate.exchange(Constants.WebServiceUrls.REGISTER_USER_SERVICE_URL, HttpMethod.POST, new HttpEntity<UserDevice>(userDevice, requestHeaders),
    getOnResponseListener().getType(), country);

    if (response.getStatusCode().value() == Constants.HTTP_CREATED) {
    // Return the response body to display to the user
    return response.getBody();
    }


    //                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
    //                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
    //                        formData, requestHeaders);
    //
    //                // Create a new RestTemplate instance
    //                RestTemplate restTemplate = new RestTemplate(true);
    //
    //                // Make the network request, posting the message, expecting a String in response from the server
    //                ResponseEntity<T> response = restTemplate.exchange(Constants.WebServiceUrls.REGISTER_USER_SERVICE_URL, HttpMethod.POST, requestEntity,
    //                        getOnResponseListener().getType(), country);
    //
    //                if (response.getStatusCode().value() == Constants.HTTP_CREATED) {
    //                    // Return the response body to display to the user
    //                    return response.getBody();
    //                }
    } catch (Exception e) {
    Log.e(this.getClass().getName(), e.getMessage(), e);
    }

    return null;
    }

    @Override protected void onPostExecute(T result) {
    sendResult(result);
    }
    }*/

    /**
     * AsyncTask to derive the name and email of the user.
     */
//    private class ProfileRetrievalTask extends AsyncTask<String, Void, T> {
//        @Override
//        protected T doInBackground(String... params) {
//            try {
//                // Create a new RestTemplate instance
//                RestTemplate restTemplate = new RestTemplate();
//
//                // Add the String message converter
//                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//                // Make the HTTP GET request, marshaling the response to a String
//                ResponseEntity<T> response = restTemplate.getForEntity(Constants.WebServiceUrls.RETRIEVE_USER_PROFILE_URL, getOnResponseListener().getType(), params[0]);
//                if (response.getStatusCode().value() == Constants.HTTP_OK) {
//                    // Return the response body to display to the user
//                    return response.getBody();
//                }
//            } catch (Exception e) {
//                Log.e(this.getClass().getName(), e.getMessage(), e);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(T result) {
//            sendResult(result);
//        }
//
//    }
//
//    /**
//     * AsyncTask to update the name and email of the user.
//     */
//    private class ProfileUpdateTask extends AsyncTask<String, Void, T> {
//        @Override
//        protected T doInBackground(String... params) {
//            try {
//                // Create HttpHeaders
//                HttpHeaders requestHeaders = new HttpHeaders();
//
//                // Set the content type
//                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//                // Create the parameters
//                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//                formData.add(Constants.USER_ID, params[0]);
//                formData.add(Constants.NAME, params[1]);
//                formData.add(Constants.EMAIL, params[2]);
//
//                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
//                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
//                        formData, requestHeaders);
//
//                // Create a new RestTemplate instance
//                RestTemplate restTemplate = new RestTemplate(true);
//
//                // Make the network request, posting the message, expecting a String in response from the server
//                ResponseEntity<T> response = restTemplate.exchange(Constants.WebServiceUrls.UPDATE_USER_PROFILE_URL, HttpMethod.POST, requestEntity,
//                        getOnResponseListener().getType());
//
//                if (response.getStatusCode().value() == Constants.HTTP_OK) {
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
//
//    }

}
