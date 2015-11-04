package com.aslan.contra.util;

import android.Manifest;

/**
 * Created by gobinath on 10/29/15.
 */
public class Constants {
    //public static final String FIRST_RUN = "first_run";

    public static final String USER_ID = "userId";

    public static final String DEVICE_TOKEN = "deviceToken";

    public static final String NAME = "name";

    public static final String EMAIL = "email";

    public static final String COMMAND = "command";

    public static final int SHOW_PROFILE = 0x1000;

    public static final long SPLASH_VISIBLE_TIME = 1000;

    public static final String CONTRA_SERVICE_URL = "http://10.0.2.2:8080/ConTra";

    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
    };
}
