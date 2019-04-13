package com.example.bahaa.trackme;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent =new Intent(this,MainActivity.class);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification startServiceNotification=new NotificationCompat.Builder(this,App.CHANNEL_ID)
                .setContentTitle("Track me")
                .setContentText("I'm moving")
                .setSmallIcon(R.drawable.walk_black)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,startServiceNotification);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
