package com.aslan.contra.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;

import com.aslan.contra.R;
import com.aslan.contra.commons.App;
import com.aslan.contra.commons.Feature;
import com.aslan.contra.services.ActivityRecognitionService;
import com.aslan.contra.services.EnvironmentMonitorService;
import com.aslan.contra.services.LocationTrackingService;
import com.aslan.contra.services.NearbyTerminalTrackingService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by gobinath on 10/29/15.
 */
public class Utility {
    private static String userId;
    private static List<String> otherNumbers;
    private static String signed_in_status;
    private static String deviceToken;

    public static SharedPreferences getSharedPreference(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("com.aslan.contra", Context.MODE_PRIVATE);
        return preferences;
    }

    public static boolean isFirstRun(Context ctx) {
        String userId = getUserId(ctx);
        String signed_in = isUserSignedIn(ctx);
        // If user-id is null, this is the first run
        boolean firstRun = userId == null || signed_in == null;
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
        if (userId == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            userId = preferences.getString(Constants.USER_ID, null);
        }
        return userId;
    }

    public static void saveUserId(Context ctx, String userID) {
        userId = userID;
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.USER_ID, userId).commit();
    }

    public static List<String> getOtherNumbers(Context ctx) {
        if (otherNumbers == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            try {
                otherNumbers = (ArrayList<String>) deserialize(preferences.getString(Constants.OTHER_NUMBERS, serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return otherNumbers;
    }

    public static void saveOtherNumbers(Context ctx, List<String> numbers) throws IOException {
        otherNumbers = numbers;
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.OTHER_NUMBERS, serialize((Serializable) otherNumbers)).apply();
    }

    public static String isUserSignedIn(Context ctx) {
        if (signed_in_status == null) {
            SharedPreferences preferences = getSharedPreference(ctx);
            signed_in_status = preferences.getString(Constants.SIGNED_IN_STATUS, null);
        }
        return signed_in_status;
    }

    public static void saveUserSignedIn(Context ctx, boolean status) {
        if (status) {
            signed_in_status = Constants.SIGNED_IN;
        } else {
            signed_in_status = Constants.SIGNED_OUT;
        }
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putString(Constants.SIGNED_IN_STATUS, signed_in_status).commit();
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

    /**
     * WiFi MAC address of the device.
     *
     * @param ctx
     * @return
     */
    public static String getDeviceWiFiMAC(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String deviceWiFiMAC = wInfo.getMacAddress();
        return deviceWiFiMAC;
    }

    /**
     * Bluetooth MAC address of the device.
     *
     * @param ctx
     * @return
     */
    public static String getDeviceBtMAC(Context ctx) {
        String deviceBtMAC = BluetoothAdapter.getDefaultAdapter().getAddress();
        return deviceBtMAC;
    }

    /**
     * API/SDK/OS version of the device.
     *
     * @param ctx
     * @return
     */
    public static int getDeviceAPI(Context ctx) {
        return Build.VERSION.SDK_INT;
    }

    /**
     * List of sensors of the device.
     *
     * @param ctx
     * @return
     */
    public static String[] getDeviceSensors(Context ctx) {
        SensorManager mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorList = new ArrayList<>();
        final int noOfSensors = sensors.size();
        String[] sensorNames = new String[noOfSensors];
        for (int i = 0; i < noOfSensors; i++) {
            sensorNames[i] = sensors.get(i).getName();
        }
        return sensorNames;
    }
//    public static String[] getDeviceSensors(Context ctx) {
//        SensorManager mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        String[] sensorList = new String[sensors.size()];
//        for (int i = 0; i < sensors.size(); i++) {
//            sensorList[i] = sensors.get(i).toString();
//        }
//        return sensorList;
//    }

    public static boolean getTrackingServiceState(Context ctx, String key) {
        SharedPreferences preferences = getSharedPreference(ctx);
        return preferences.getBoolean(key, false);
    }

    public static void saveTrackingServiceState(Context ctx, String key, boolean status) {
        SharedPreferences preferences = getSharedPreference(ctx);
        preferences.edit().putBoolean(key, status).commit();
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


    public static float getBatteryLevel(Context ctx) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = ctx.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercent = 100 * level / (float) scale;
        Log.i("BatteryLevel", "" + level);
        Log.i("BatteryScale", "" + scale);
        Log.i("Battery%", "" + batteryPercent);

        return batteryPercent;
    }

    //TODO remove this method when you get the actual app data from server
    public static App[] getAllApps(Context ctx) {
        Resources resources = ctx.getResources();
        TypedArray appTitles = resources.obtainTypedArray(R.array.app_titles);
        TypedArray appIcons = resources.obtainTypedArray(R.array.app_icons);
        TypedArray appMarketUrls = resources.obtainTypedArray(R.array.app_market);

        int noOfFeatures = appTitles.length();
        App[] apps = new App[noOfFeatures];

        for (int i = 0; i < noOfFeatures; i++) {
            App app = new App();
            app.setTitle(appTitles.getString(i));
            app.setIcon(appIcons.getDrawable(i));
            app.setUrl(appMarketUrls.getString(i));
            apps[i] = app;
        }

        appTitles.recycle();
        appMarketUrls.recycle();
        appIcons.recycle();

        return apps;
    }

    /**
     * Check whether the network access is available or not.
     *
     * @param ctx
     * @return
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Feature[] getAllFeatures(Context ctx) {
        Resources resources = ctx.getResources();
        TypedArray featureNames = resources.obtainTypedArray(R.array.feature_names);
        TypedArray featureDescriptions = resources.obtainTypedArray(R.array.feature_descriptions);
        TypedArray featureIcons = resources.obtainTypedArray(R.array.feature_icons);
        TypedArray featurePermission = resources.obtainTypedArray(R.array.feature_permissions);

        int noOfFeatures = featureNames.length();
        Feature[] features = new Feature[noOfFeatures];

        for (int i = 0; i < noOfFeatures; i++) {
            Feature feature = new Feature();
            feature.setName(featureNames.getString(i));
            feature.setDescription(featureDescriptions.getString(i));
            feature.setIcon(featureIcons.getDrawable(i));
            feature.setPermissions(featurePermission.getString(i).split(","));
            features[i] = feature;
        }

        featureNames.recycle();
        featureDescriptions.recycle();
        featureIcons.recycle();
        featurePermission.recycle();

        return features;
    }

    public static String[] nonGrantedPermissions(Context ctx) {
        List<String> nonGrantedPermissions = new ArrayList<>();

        // Android 6.0 or latest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources resources = ctx.getResources();
            TypedArray featurePermission = resources.obtainTypedArray(R.array.feature_permissions);
            int size = featurePermission.length();

            for (int i = 0; i < size; i++) {
                String str = featurePermission.getString(i);
                // There can be more than one permissions associated with the feature
                String[] permissions = str.split(",");
                for (String permission : permissions) {
                    if (ctx.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        nonGrantedPermissions.add(permission);
                    }
                }
            }

            featurePermission.recycle();
        }

        return nonGrantedPermissions.toArray(new String[0]);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Start the sensors to collect the data.
     */
    public static void startSensors(Context ctx, boolean isFirstRun) {
        /**
         * Start the sensors based on user saved preference to collect the data.
         */
        if (!isFirstRun) {
            //TODO comment when you don't want to auto start the tracking service at app start
            if (Utility.getTrackingServiceState(ctx, LocationTrackingService.TAG) && !RunningServices.getInstance().isLocationServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, LocationTrackingService.class);
                serviceIntent.addCategory(LocationTrackingService.TAG);
                ctx.startService(serviceIntent);
            }
            if (Utility.getTrackingServiceState(ctx, ActivityRecognitionService.TAG) && !RunningServices.getInstance().isActivityRecognitionServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, ActivityRecognitionService.class);
                serviceIntent.addCategory(ActivityRecognitionService.TAG);
                ctx.startService(serviceIntent);
            }
            if (Utility.getTrackingServiceState(ctx, EnvironmentMonitorService.TAG) && !RunningServices.getInstance().isEnvironmentMonitorServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, EnvironmentMonitorService.class);
                serviceIntent.addCategory(EnvironmentMonitorService.TAG);
                ctx.startService(serviceIntent);
            }
            if (Utility.getTrackingServiceState(ctx, NearbyTerminalTrackingService.TAG) && !RunningServices.getInstance().isNearbyTerminalTrackingServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, NearbyTerminalTrackingService.class);
                serviceIntent.addCategory(NearbyTerminalTrackingService.TAG);
                ctx.startService(serviceIntent);
            }
        } else {

            //TODO comment when you don't want to auto start the tracking service at app start
            if (!RunningServices.getInstance().isLocationServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, LocationTrackingService.class);
                serviceIntent.addCategory(LocationTrackingService.TAG);
                ctx.startService(serviceIntent);
            }
            if (!RunningServices.getInstance().isActivityRecognitionServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, ActivityRecognitionService.class);
                serviceIntent.addCategory(ActivityRecognitionService.TAG);
                ctx.startService(serviceIntent);
            }
            if (!RunningServices.getInstance().isEnvironmentMonitorServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, EnvironmentMonitorService.class);
                serviceIntent.addCategory(EnvironmentMonitorService.TAG);
                ctx.startService(serviceIntent);
            }
            if (!RunningServices.getInstance().isNearbyTerminalTrackingServiceRunning(ctx)) {
                Intent serviceIntent = new Intent(ctx, NearbyTerminalTrackingService.class);
                serviceIntent.addCategory(NearbyTerminalTrackingService.TAG);
                ctx.startService(serviceIntent);
            }
        }
    }

    /**
     * Stop the sensors at sign out.
     */
    public static void stopSensors(Context ctx) {
        //TODO comment when you don't want to auto start the tracking service at app start
        if (RunningServices.getInstance().isLocationServiceRunning(ctx)) {
            Intent serviceIntent = new Intent(ctx, LocationTrackingService.class);
            serviceIntent.addCategory(LocationTrackingService.TAG);
            ctx.stopService(serviceIntent);
        }
        if (RunningServices.getInstance().isActivityRecognitionServiceRunning(ctx)) {
            Intent serviceIntent = new Intent(ctx, ActivityRecognitionService.class);
            serviceIntent.addCategory(ActivityRecognitionService.TAG);
            ctx.stopService(serviceIntent);
        }
        if (RunningServices.getInstance().isEnvironmentMonitorServiceRunning(ctx)) {
            Intent serviceIntent = new Intent(ctx, EnvironmentMonitorService.class);
            serviceIntent.addCategory(EnvironmentMonitorService.TAG);
            ctx.stopService(serviceIntent);
        }
        if (RunningServices.getInstance().isNearbyTerminalTrackingServiceRunning(ctx)) {
            Intent serviceIntent = new Intent(ctx, NearbyTerminalTrackingService.class);
            serviceIntent.addCategory(NearbyTerminalTrackingService.TAG);
            ctx.stopService(serviceIntent);
        }
    }


    /*
     * From @url{https://github.com/apache/pig/blob/89c2e8e76c68d0d0abe6a36b4e08ddc56979796f/src/org/apache/pig/impl/util/ObjectSerializer.java}
     */
    public static String serialize(Serializable obj) throws IOException {
        if (obj == null) return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
//            throw WrappedIOException.wrap("Serialization error: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(String str) throws IOException {
        if (str == null || str.length() == 0) return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(serialObj);
            return objStream.readObject();
        } catch (Exception e) {
//            throw WrappedIOException.wrap("Deserialization error: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            char c = str.charAt(i);
            bytes[i / 2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i + 1);
            bytes[i / 2] += (c - 'a');
        }
        return bytes;
    } //End of code from Apache pig
}
