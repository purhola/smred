package com.purho.java.android.omat.smokereducer2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.text.format.DateFormat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//näitä tehdään sitten häly per savu

public class ScheduledRunner {

}

/*
    public ScheduledRunner(String smokeAllowed) {
        //the Date and time at which you want to execute
    //    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     //   Date date = ((SimpleDateFormat) dateFormatter).parse("2012-07-06 13:05:45");

        //TODO kuluva tai seuraava päivä -ja smokeallowedhan on tuntimuodossa.



        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once
     //   timer.schedule(new MyTimeTask(), date);

        //Use this if you want to execute it repeatedly
        //int period = 10000;//10secs
        //timer.schedule(new MyTimeTask(), date, period );

    }

//The task which you want to execute
//private static class MyTimeTask extends TimerTask
//{

  //  public void run()
   /* {
        //write your code here
        //Tästäpä vaikka notifikaatio liikkeelle
        String id = "main_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "Channel name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(false);
            if (notificationChannel != null) {
 //               notificationManager.createNotificationChannel((notificationChannel));
            }
        }

        /*
        //notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Smoke");
        notificationBuilder.setContentText("Now");
        notificationBuilder.setLights(Color.WHITE, 500, 5000);
        notificationBuilder.setColor(Color.RED);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1000, notificationBuilder.build());

    }
*/



