package com.aslan.contra.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

/**
 * Created by vishnuvathsan on 27-Dec-15.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utility.isUserSignedIn(context).equalsIgnoreCase(Constants.SIGNED_IN)) {
            Utility.startSensors(context, false);
        }
    }
}
