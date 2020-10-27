package com.mayank.punjabidelight;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static void displayNotification(Context context, String firebase_title,String firebase_body){
        NotificationCompat.Builder mBuilder=
                new NotificationCompat.Builder(context,HomeActivity.CHANNEL_ID)
                              .setSmallIcon(R.drawable.ic_action_notification)
                .setContentTitle(firebase_title)
                .setContentText(firebase_body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotificationMgr=NotificationManagerCompat.from(context);
        mNotificationMgr.notify(1,mBuilder.build());



    }
}
