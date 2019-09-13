package com.purho.java.android.omat.smokereducer2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        mContext=arg0;

        //do something, i.e.smokeallowed
        System.out.println("joku häly pitäis puskea eiku jotain pitäis käynnistää VANHAA");
        //create the intent after the notification

      //  Intent intent = new Intent(MainActivity.this, NotificationX.class);



        //create the intent after the notification
        Intent intent = new Intent(mContext,SmokeAllowed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0, intent, 0);


        //TÄSTÄ notifikaatio liikkeelle
        String id = "main_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "Channel name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;  //could be IMPORTANCE_DEFAULT
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(false);
            if (notificationChannel != null) {
                notificationManager.createNotificationChannel((notificationChannel));
            }
        }

        //notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext,id);     //Context.NOTIFICATION_SERVICE, id);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Smoke");
        notificationBuilder.setContentText("Now");
        notificationBuilder.setLights(Color.WHITE, 500, 5000);
        notificationBuilder.setColor(Color.RED);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        //here we should set the intent and clearing the notification
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(1000, notificationBuilder.build());




    }

}

