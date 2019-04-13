package com.example.bahaa.trackme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {



    static final String TAG="NOTIFICATION";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("Message","message is received!");
        String from=remoteMessage.getFrom();
        int size=remoteMessage.getData().size();
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Toast.makeText(this,"message is sent",Toast.LENGTH_SHORT).show();
    }

}
