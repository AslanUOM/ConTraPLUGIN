package com.aslan.contra.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by gobinath on 10/29/15.
 */
public class Utility {
    private static String userId;
    private static String deviceToken;

    public static SharedPreferences getSharedPreference(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("com.aslan.contra", Context.MODE_PRIVATE);
        return preferences;
    }

    public static boolean isFirstRun(Context ctx) {
        String userId = getUserId(ctx);
        // If user-id is null, this is the first run
        boolean firstRun = userId == null;
        return firstRun;
    }

    public static List<String> getAccountEmail(Context ctx) {
        List<String> emails = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emails.add(possibleEmail);
            }
        }
        return emails;
    }

    public static String getUserId(Context ctx) {
        if(userId == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            userId = preferences.getString(Constants.USER_ID, null);
        }
        return userId;
    }

    public static void saveUserId(Context ctx, String userId) {
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.USER_ID, userId).commit();
    }

    public static String getDeviceName(Context ctx) {
        String deviceName = "";
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            deviceName = model;
        }
        deviceName = manufacturer + "-" + model;
        return deviceName;
    }

    /**
     * A unique serial code of the device.
     *
     * @param ctx
     * @return
     */
    public static String getDeviceSerial(Context ctx) {
        String deviceId = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public static String getDeviceToken(Context ctx) {
        if (deviceToken == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            deviceToken = preferences.getString(Constants.DEVICE_TOKEN, null);
        }
        return deviceToken;
    }

    public static void saveDeviceToken(Context ctx, String deviceToken) {
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.DEVICE_TOKEN, deviceToken).commit();
    }
}
