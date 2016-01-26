package com.aslan.contra.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Vishnuvathsasarma on 26-Jan-16.
 */
public class XMLreader {
    private String TAG;
    private String result;

    public void setTAG(String tAG) {
        TAG = tAG;
    }

    /**
     * read the HTTP response and convert the XML to get the intended data
     * specified by the user
     *
     * @param url
     * @return
     */
    public String readURL(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                boolean isStarted = false;
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory
                            .newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(reader);
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (TAG.equalsIgnoreCase(xpp.getName())) {
                                    isStarted = true;
                                }
                                break;
                            case XmlPullParser.TEXT:
                                if (isStarted) {
                                    result = xpp.getText();
                                    isStarted = false;
                                    //Log.i("^^^^^^^^^^^^", result);
                                }
                                break;
                        }
                        eventType = xpp.next();
                    }
                } catch (XmlPullParserException ex) {

                } catch (IOException ex) {
                }
            } else {
                //Log.e(MainActivity.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }

        return result;
    }

}
