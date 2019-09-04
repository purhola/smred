package com.purho.java.android.omat.smokereducer2;

/* save smoke points to table
calculate the minutes getting most points
    first day -> every smoke
    after that if smoked on minute x, then points go x=10 points , x+-1 = 9 points x+-2 = 8 points and so on
   every day offer the most valuable minutes for smoking. amount of points offered is always one less than smoked on the previous day
   (could be so that isn't affected by one single day deviations) sort of like sales in Mart..)

   need to get the recorded data saved somewhere - most preferably so that they can be restored

   can we make push notifications of the offered smoking times?


   perhaps we should use the helper for creating and using the db - just copy it to some safe place every night / when the app starts* or both.
   *could be done just before dropping the existing tables. -> need to see if we could ask if the should be restored on then just not drop the tables.
   OR make a copy of the history and copy that to the new table.
   perhaps NOT very much needed in prod-phase but very much so in devel.phase.


 */




import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private MyDbAdapter helper;

    Button btnSmokeNow;
    Button btnSmokeOnTime;
    Button btnSkipSmoke;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new MyDbAdapter(this);


        btnSmokeNow = (Button) findViewById(R.id.btnSmokeNow);
        btnSmokeOnTime = (Button) findViewById(R.id.btnSmokeOnTime);
        btnSkipSmoke = (Button) findViewById(R.id.btnSkip);


        //Copy the db to sdcard.. TODO katsellaan
        //nimen sais varmaan helpperillä..


        exportDatabase("playtodella1");


        //THIS below WORKS!! Use where needed!
        //showNotification();

    }


    private void showNotification() {

        //channel
        String id = "main_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "Channel name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
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

    public void insertSmoke(View v) {

        System.out.println("pitäis kirjoitella kantaan " );


        String smoketime = getCurrentLocalDateTimeStamp();
        System.out.println("mitähän  " + smoketime);
        long id=0;

        id=helper.insertSmokePoint(smoketime);
        Optimizer optimize = new Optimizer();

    }

    public void insertAllowedSmoke(View v) {


    }

    public void skipSmoke(View v){


    }

    public String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }



    public void exportDatabase(String databaseName) {

        Integer card=0;

        try {
            File sd = Environment.getExternalStorageDirectory();
            System.out.println("FILE ONPI :" + sd);

            File data = Environment.getDataDirectory();


            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                Log.d("Test", "sdcard mounted and writable");
                Toast.makeText(this, "SD card rw", Toast.LENGTH_LONG).show();
                card=1;
            }
            else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                Log.d("Test", "sdcard mounted readonly");
                Toast.makeText(this, "Can only read SD card", Toast.LENGTH_LONG).show();
            }
            else {
                Log.d("Test", "sdcard state: " + state);
                Toast.makeText(this, "Can't see SD card", Toast.LENGTH_LONG).show();
            }

            if (card==1) {
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+databaseName+"";
                System.out.println("KANNAN PAIKKA " + currentDBPath);
                String backupDBPath = "//smokedsmokes";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    System.out.println("KANTA LÖYTYY IFFIIN" );

                    Toast.makeText(this, "DB exists", Toast.LENGTH_LONG).show();
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                else System.out.println("EI LÖYDY KANTAA  *****************************************************************************************************" );
            }
            else {
                System.out.println("EI PYSTY KIRJOITTAAN");
                //Toast.makeText(this, "Can't write SD card", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION ILMAANTUI " + e);

        }
    }


}


