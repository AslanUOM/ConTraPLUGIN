package com.aslan.contra.util;

import android.Manifest;

/**
 * Created by gobinath on 10/29/15.
 */
public class Constants {
    public static final String USER_ID = "userId";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String NEARBY_FRIENDS = "nearby friends";
    /**
     * GCM service project number
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = "986180772600";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String COMMAND = "command";
    public static final String ALL_PERMISSIONS_GRANTED = "granted";
    public static final int SHOW_PROFILE = 0x1000;
    public static final long SPLASH_VISIBLE_TIME = 1000;
    public static final double MIN_DISTANCE_FOR_LOCATION_CHANGE = 100;     //in meters
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
    };

    //public static final String FIRST_RUN = "first_run";
    private Constants() {
    }

    public static final class LocationTracking {
        // The maximum validity period of a location, here 30 minute
        public static final int MAX_VALIDITY_PERIOD = 1000 * 60 * 2;

        // The minimum distance to change location Updates in meters
        public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;

        // The minimum time between location updates in milliseconds, here 30 minutes
        public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 30;
    }

    public static final class Type {
        /**
         * Type LOCATION.
         */
        public static final String LOCATION = "location";

        /**
         * Type AVAILABLE_WIFI.
         */
        public static final String AVAILABLE_WIFI = "networks";

        /**
         * Type CONTACTS.
         */
        public static final String CONTACTS = "contacts";
    }

    public static final class WebServiceUrls {
        /**
         * URL for ConTra service(server).
         */
        public static final String CONTRA_SERVICE_URL = "http://contra.projects.mrt.ac.lk:8080/ConTra";

        /**
         * URL Sensor Data.
         */
        public static final String SEND_SENSOR_DATA_URL = CONTRA_SERVICE_URL + "/sensordatareceiver/save";

        /**
         * URL Register User.
         */
        public static final String REGISTER_USER_SERVICE_URL = CONTRA_SERVICE_URL + "/user/register";

        /**
         * URL Get User Profile.
         */
        public static final String RETRIEVE_USER_PROFILE_URL = CONTRA_SERVICE_URL + "/user/profile/{query}";

        /**
         * URL Update User.
         */
        public static final String UPDATE_USER_PROFILE_URL = CONTRA_SERVICE_URL + "/user/updateprofile";
    }

    public static final class MessagePassingCommands {
        //constants for message passing and identifying
        public static final int START_LOCATION_TRACKING = 0;
        public static final int STOP_LOCATION_TRACKING = 1;
        public static final int GET_ALL_CONTACTS = 2;
        public static final int EXPORT_LOCATION_DATA_TO_SD_CARD = 3;
        public static final int NEARBY_FRIENDS_RECEIVED = 4;
    }
}
