package com.mayank.punjabidelight;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String title,message;
    public static final String CHANNEL_ID="Punjabi Delight";
    private static final String CHANNEL_NAME="Punjabi Delight";
    private static final String CHANNEL_DESCRIPTION="Punjabi Delight";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        title=remoteMessage.getData().get("Title");
        message=remoteMessage.getData().get("Message");


        Intent intent=new Intent(MyFirebaseMessagingService.this,HomeActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(MyFirebaseMessagingService.this,100,intent,PendingIntent.FLAG_CANCEL_CURRENT);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder=
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_action_notification)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotificationMgr=NotificationManagerCompat.from(this);
        mNotificationMgr.notify(1,mBuilder.build());
    }

    }

