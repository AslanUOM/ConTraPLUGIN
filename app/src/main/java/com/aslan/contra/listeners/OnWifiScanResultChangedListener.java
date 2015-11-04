package com.aslan.contra.listeners;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Vishnuvathsasarma on 04-Nov-15.
 */
public interface OnWifiScanResultChangedListener {
    void onWifiScanResultsChanged(List<ScanResult> wifiList);
}
