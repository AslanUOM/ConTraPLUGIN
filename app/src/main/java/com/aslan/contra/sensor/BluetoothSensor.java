package com.aslan.contra.sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.aslan.contra.listeners.OnBluetoothScanResultChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vishnuvathsan on 26-Dec-15.
 */
public class BluetoothSensor {
    private Context mContext;
    private OnBluetoothScanResultChangedListener listener;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BroadcastReceiver broadcastReceiver;
    private int size = 0;
    private List<String> results;

    public BluetoothSensor(Context context) {
        mContext = context;
    }

    public void start() {
        results = new ArrayList<>();
        size = 0;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Log.d("BLUETOOTH", "Bluetooth is disabled..\nEnable it and restart the app to get Bluetooth updates");
            Toast.makeText(mContext.getApplicationContext(), "Bluetooth is disabled..\nEnable it and restart the app to get Bluetooth updates", Toast.LENGTH_LONG).show();
//            wifiManager.setWifiEnabled(true);
        } else {
            //TODO check for paired devices
//            pairedDevices = bluetoothAdapter.getBondedDevices();
//            if (pairedDevices.size() > 0) {
//                // Loop through paired devices
//                for (BluetoothDevice device : pairedDevices) {
//                    // Add the name and address to an array adapter to show in a ListView
//                    results.add(device.getAddress());
//                    size++;
//                }
//            }
            // Create a BroadcastReceiver for ACTION_FOUND
            broadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        //discovery starts, we can show progress dialog or perform other tasks
                    } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {    // When discovery finds a device
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        results.add(device.getAddress());
                        size++;
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        //discovery finishes, dismis progress dialog
                        mContext.unregisterReceiver(broadcastReceiver);
                        broadcastReceiver = null;
                        onResult();
                    }
                }
            };
// Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            mContext.registerReceiver(broadcastReceiver, filter); // Don't forget to unregister during onDestroy

            bluetoothAdapter.startDiscovery();
        }
    }

    public void onResult() {
        Log.d("BLUETOOTH", "Scanning.... " + size);
        if (size == 0) {
            Log.d("BLUETOOTH", "NOTHING FOUND");
        } else {
            listener.onBluetoothScanResultsChanged(results);
        }
    }

    public void stop() {
        bluetoothAdapter.cancelDiscovery();
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    public void setOnBluetoothScanResultChangedLsitener(OnBluetoothScanResultChangedListener listener) {
        this.listener = listener;
    }

//    public static int calculateSignalStength(WifiManager wifiManager, int level){
//        return wifiManager.calculateSignalLevel(level, 5) + 1;
//    }
}