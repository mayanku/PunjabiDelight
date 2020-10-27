package com.mayank.punjabidelight;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() !=null){
            String firebase_title=remoteMessage.getNotification().getTitle();
            String firebase_body=remoteMessage.getNotification().getBody();

            NotificationHelper.displayNotification(getApplicationContext(),firebase_title,firebase_body);
        }
    }
}
