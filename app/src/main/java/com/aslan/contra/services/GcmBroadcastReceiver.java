package com.aslan.contra.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.aslan.contra.R;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;

/**
 * Created by Vishnuvathsasarma on 05-Nov-15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "GcmBroadcastReceiver";

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
//        int icon = R.mipmap.ic_launcher;
//        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent =
//                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//
//        //TODO resolve deprecated method call
////        notification.setLatestEventInfo(context, title, message, intent);
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0, notification);


        Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage("com.aslan.friendsfinder");

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String message = intent.getExtras().getString("message");
        Context ctx = context.getApplicationContext();
        if (message == null || message.isEmpty()) {
            SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(ctx);
            service.setOnResponseListener(new OnResponseListener<String>() {
                @Override
                public void onResponseReceived(String response) {
//                    if (response != null) {
//                        // TODO handle received response from server for contacts update
//                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
//                    } else {
//                        // TODO: Replace by AlertDialog
//                        Toast.makeText(context, "Unable to send contacts to server", Toast.LENGTH_LONG).show();
//                    }
                }

                @Override
                public Class getType() {
                    return String.class;
                }
            });
            service.sendContacts();
        } else {
            Log.d("Message >>>>> ", message);
            generateNotification(ctx, message);
            setResultCode(Activity.RESULT_OK);
        }
    }
}
