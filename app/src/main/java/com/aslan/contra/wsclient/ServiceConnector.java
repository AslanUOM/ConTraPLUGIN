package com.aslan.contra.wsclient;

import android.os.AsyncTask;
import android.util.Log;

import com.aslan.contra.dto.common.Person;
import com.aslan.contra.dto.ws.Message;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;

/**
 * This class is used to send HTTP request to the service endoint and to return back the result.
 *
 * @author gobinath
 */
public class ServiceConnector<T, R> {
    /**
     * Type required by Gson to construct the Message\<R\> object.
     */
    private final Type type = new TypeToken<Message<R>>() {
    }.getType();

    /**
     * AsyncTask to run in separate thread.
     */
    private final BackgroundTask task = new BackgroundTask();

    private OnResponseListener<R> listener;

    public ServiceConnector(OnResponseListener<R> listener) {
        this.listener = listener;
    }

    public void execute(Request<T> request) {
        task.execute(request);
    }

    private class BackgroundTask extends AsyncTask<Request<T>, Void, Message<R>> {
        @Override
        protected Message<R> doInBackground(Request<T>... params) {
            Request<T> request = params[0];

            try {

                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<T> entity = new HttpEntity<>(request.getEntity(), requestHeaders);


                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);
                Gson gson = new Gson();
                Log.i("JSON", gson.toJson(entity));
                ResponseEntity<String> response = restTemplate.exchange(request.getUrl(), request.getHttpMethod(), entity, String.class, (Object[]) request.getUrlVariables());

                String body = response.getBody();

                Log.i("RESPONSE", body);
                // Construct the Message<R>

                return gson.fromJson(body, type);
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
                Message<R> message = new Message<>();
                message.setMessage(e.getMessage());

                return message;
            }
        }

        @Override
        protected void onPostExecute(Message<R> message) {
            if (listener != null) {
                listener.onResponseReceived(message);
            }
        }
    }


    public interface OnResponseListener<T> {
        void onResponseReceived(Message<T> result);
    }
}
