package com.aslan.contra.listeners;

import java.util.List;

/**
 * Created by vishnuvathsan on 26-Dec-15.
 */
public interface OnBluetoothScanResultChangedListener {
    void onBluetoothScanResultsChanged(List<String> bluetoothList);
}
