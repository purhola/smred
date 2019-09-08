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




import android.Manifest;
import android.annotation.TargetApi;
//import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private MyDbAdapter helper;

    private int STORAGE_PERMISSION_CODE = 1;
    Button btnSmokeNow;
    Button btnSmokeOnTime;
    Button btnSkipSmoke;
    String dbname;
    ArrayList<Optimizer> allowedpoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new MyDbAdapter(this);


        btnSmokeNow = (Button) findViewById(R.id.btnSmokeNow);
        btnSmokeOnTime = (Button) findViewById(R.id.btnSmokeOnTime);
        btnSkipSmoke = (Button) findViewById(R.id.btnSkip);

        dbname = "playtodella1";


        //oma scheduled runner, että kerran vuorokaudessa käynnistetään koko fukin' rimpsu


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_LONG).show();

            exportDatabase(dbname);
        } else {
            requestStoragePermission();

        }
    }

    //exportDatabase(dbname);


    //THIS below WORKS!! Use where needed!
    //showNotification();


    //SCHEDULERI
    public void ScheduledRunnerNightRun(String smokeAllowed) {
        //the Date and time at which you want to execute
        //    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //   Date date = ((SimpleDateFormat) dateFormatter).parse("2012-07-06 13:05:45");

        //TODO kuluva tai seuraava päivä -ja smokeallowedhan on tuntimuodossa.
        //tehdäänkö tämä joka päiväksi erikseen vai mitä häh?


        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once
        //   timer.schedule(new MyTimeTask(), date);

        //Use this if you want to execute it repeatedly
        //int period = 10000;//10secs
        //timer.schedule(new MyTimeTask(), date, period );

    }

   /* //The task which you want to execute
    private static class MyTimeTask extends TimerTask
    {

        public void run()
        {

            //TÄSTÄ KOKO RIMPSU LIIKKEELLE


        }
    }
*/


    public void insertSmoke(View v) {

        //System.out.println("pitäis kirjoitella kantaan " );


        String smoketime = getCurrentLocalDateTimeStamp();
        //System.out.println("mitähän  " + smoketime);
        long id = 0;


        id = helper.insertSmokePoint(smoketime);
        if (id > 0) Toast.makeText(this, "Cig saved", Toast.LENGTH_LONG).show();


        Optimizer optimize = new Optimizer();

    }

    public void insertAllowedSmoke(View v) {


    }

    public void skipSmoke(View v) {


    }

    public void adminAct(View v) {

        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }


    public String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }


    public void exportDatabase(String databaseName) {

        Integer card = 0;

        try {
            File sd = Environment.getExternalStorageDirectory();
            System.out.println("FILE ONPI :" + sd);

            File data = Environment.getDataDirectory();


            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                Log.d("Test", "sdcard mounted and writable");
                Toast.makeText(this, "SD card rw", Toast.LENGTH_LONG).show();
                card = 1;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                Log.d("Test", "sdcard mounted readonly");
                Toast.makeText(this, "Can only read SD card", Toast.LENGTH_LONG).show();
            } else {
                Log.d("Test", "sdcard state: " + state);
                Toast.makeText(this, "Can't see SD card", Toast.LENGTH_LONG).show();
            }

            if (card == 1) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//" + databaseName + "";
                //System.out.println("KANNAN PAIKKA " + currentDBPath);
                String backupDBPath = "//smokedsmokes.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    //System.out.println("KANTA LÖYTYY IFFIIN" );

                    Toast.makeText(this, "DB exists", Toast.LENGTH_LONG).show();
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    //System.out.println("TONNE PITI MENNÄ TONNE PITI MENNÄ TONNE PITI MENNÄ : "+ "SOURCE: " + src + " " + currentDB +  " DEST: "+ dst + " " + backupDB);
                    src.close();
                    dst.close();
                }

            } else {
                //System.out.println("EI PYSTY KIRJOITTAAN");
                Toast.makeText(this, "Can't write SD card", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION ILMAANTUI " + e);

        }
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_LONG).show();
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX LUPA SAATU");
                exportDatabase(dbname);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_LONG).show();
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX EI SAATU LUPAA");
            }
        }
    }


    public void populate(View v) {  //tää on vaan nyt tyhmä nimi, vois olla getpoints tms
/*
    helper.insertSmokePoint("2019-09-04 08:01");
    helper.insertSmokePoint("2019-09-04 08:08");
    helper.insertSmokePoint("2019-09-04 09:01");
    helper.insertSmokePoint("2019-09-04 10:01");
    helper.insertSmokePoint("2019-09-04 11:01");
    helper.insertSmokePoint("2019-09-04 12:01");
    helper.insertSmokePoint("2019-09-04 13:01");
    helper.insertSmokePoint("2019-09-04 14:01");
    helper.insertSmokePoint("2019-09-04 15:01");
    helper.insertSmokePoint("2019-09-04 16:01");
*/
        //start the assignments from here too at this point. could do from db.

        ArrayList<String> smokepointslist = new ArrayList<>();

        smokepointslist = helper.getSmoked();

        for (String smtemp : smokepointslist) {
            //SmokeExpander sme = new
            SmokeExpander(smtemp);
        }


    }


    public void SmokeExpander(String smoketime) {


        //String dateTime = "2018-12-11 17:30";
        LocalDateTime rsplus;
        LocalDateTime rsminus;
        String pointstemp;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime realSmokeTime = LocalDateTime.parse(smoketime, formatter);

        ArrayList<PointsAssigned> listSmokes = new ArrayList<PointsAssigned>();

        PointsAssigned p1 = new PointsAssigned(realSmokeTime, "100");
        //p1.setSmoketime(realSmokeTime);
        //p1.setPoints("10");

        listSmokes.add(p1);

        for (int i = 1; i < 10; i++) {
            int n = 100 - i;
            PointsAssigned pplus = new PointsAssigned(realSmokeTime.plusMinutes(i), Integer.toString(100 - i));
            PointsAssigned pminus = new PointsAssigned(realSmokeTime.minusMinutes(i), Integer.toString(100 - 2 * i));

            //rsplus=realSmokeTime.plusMinutes(i);
            //rsminus=realSmokeTime.minusMinutes(i);

            //pointstemp=Integer.toString(n);
            //pplus.setSmoketime(rsplus);
            //pplus.setPoints(pointstemp);
            //pminus.setSmoketime(rsminus);
            //pminus.setPoints(pointstemp);

            listSmokes.add(pplus);
            listSmokes.add(pminus);
        }


        for (PointsAssigned pointed : listSmokes) {

            System.out.println("Time and points: " + pointed.getTime() + " points " + pointed.getPoints());

            //insert these into table assignedpoints

            helper.upsertAssignedPoints(pointed.getTime(), pointed.getPoints());


        }


    }

    public void getAllowedPoints(View v) {

        ScheduledRunner sr;
        Integer paivaannos = 15;

        //here we might have the amount of smoketimes had the day before in order to reduce them
        allowedpoints = helper.getPointSummedSmokingTimes();

        for (Integer i = 0; i < paivaannos; i++) {
            System.out.println("time: " + allowedpoints.get(i).getStrtime() + " points: " + allowedpoints.get(i).getStrpoints());

            //tehdaan ajastus jokaisesta
            ScheduledRunner(allowedpoints.get(i).getStrtime());

        }


    }

    public void ScheduledRunner(String smokeAllowed) {
        //the Date and time at which you want to execute

        String paiva = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + smokeAllowed;
        System.out.println("KULUVAA PÄIVÄÄ TUNTIA: " + paiva);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = ((SimpleDateFormat) dateFormatter).parse(paiva);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once


        timer.schedule(new MyTimeTask(), date);

        //Use this if you want to execute it repeatedly
        //int period = 10000;//10secs
        //timer.schedule(new MyTimeTask(), date, period );

    }


    //The task which you want to execute
    private class MyTimeTask extends TimerTask {

        public void run() {



        //TÄSTÄ notifikaatio liikkeelle
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this,id);     //Context.NOTIFICATION_SERVICE, id);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Smoke");
        notificationBuilder.setContentText("Now");
        notificationBuilder.setLights(Color.WHITE, 500, 5000);
        notificationBuilder.setColor(Color.RED);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(1000, notificationBuilder.build());

        }
    }

}


