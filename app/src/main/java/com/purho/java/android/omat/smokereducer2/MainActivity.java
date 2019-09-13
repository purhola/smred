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
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new MyDbAdapter(this);


        btnSmokeNow = (Button) findViewById(R.id.btnSmokeNow);

        dbname = "playtodella1";




        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_LONG).show();

            exportDatabase(dbname);
        } else {
            requestStoragePermission();

        }
    }



    public void insertSmoke(View v) {

        //System.out.println("pitäis kirjoitella kantaan " );

        String smoketime = getCurrentLocalDateTimeStamp();
//        System.out.println("mitähän  " + smoketime);
        long id = 0;

        id = helper.insertSmokePoint(smoketime);
        if (id > 0) Toast.makeText(this, "Cig saved", Toast.LENGTH_LONG).show();


    }



    //dbg
    public void adminAct(View v) {

        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }


    public String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

        //to get the db onto ext memory  -dbg
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
                    //System.out.println("SOURCE: " + src + " " + currentDB +  " DEST: "+ dst + " " + backupDB);
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
                //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX LUPA SAATU");
                exportDatabase(dbname);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_LONG).show();
                //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX EI SAATU LUPAA");
            }
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



        ArrayList<String> smokepointslist = new ArrayList<>();


        //siivotaan pisteytetyt ja kumulatiiviset

        int ap=helper.cleanAssignedPoints();
        int cp=helper.cleanCumulativepoints();

        if (ap>0 && cp >0)
            Toast.makeText(this, "Calculated data cleaned", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Calculated data NOT cleaned", Toast.LENGTH_SHORT).show();

        //haetaan poltetut
        smokepointslist = helper.getSmoked();



        for (String smtemp : smokepointslist) {

            SmokeExpander(smtemp);
        }

        ArrayList<PendingIntent> intentArray;


        //here we have the amount of smoketimes had the day before in order to reduce them
        Integer paivaannos = helper.getSmokeCount();
       // System.out.println("PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS PÄIVÄANNOS " + paivaannos);


        //allowedpoints = helper.getPointSummedSmokingTimes();

        helper.insertCumulativePoints();

        allowedpoints=helper.selectAllowedSmokeTimes();

        //if for some reason last day's count would be smaller than the count of pointed times
        //so we don't run over array limits
        Integer rajoitin = paivaannos;

        if(allowedpoints.size()-1<paivaannos) rajoitin=allowedpoints.size()-1;

        //some logic
        for (Integer i = 0; i < rajoitin; i++) {


            AlarmManager[] alarmManager=new AlarmManager[rajoitin];
            intentArray = new ArrayList<PendingIntent>();

          //  System.out.println("*********************************************************************************************************");
            System.out.println("time: " + allowedpoints.get(i).getStrtime() + " points: " + allowedpoints.get(i).getStrpoints());

            //*********************************************************************************************************


            //tehdaan sellainen ajastus, etta toimii vaikka appi ei ole kaynnissa.
            //AlarmRunner(allowedpoints.get(i).getStrtime());


            String timme = allowedpoints.get(i).getStrtime();
            String[] time = timme.split ( ":" );
            int hour = Integer.parseInt ( time[0].trim() );
            int min = Integer.parseInt ( time[1].trim() );

            System.out.println("HÄLYT HÄLYT HÄLYT " + hour + ":" + min);

            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);


            Intent alarmIntent = new Intent(this,AlarmReceiver.class);
            pendingIntent=PendingIntent.getBroadcast(this,i,alarmIntent, 0);
            alarmManager[i]=(AlarmManager) getSystemService(ALARM_SERVICE);
            //alarmManager[i].set(AlarmManager.RTC_WAKEUP,lngmstrig,pendingIntent);
            alarmManager[i].set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

            intentArray.add(pendingIntent);
        }


    }

    //dbg
    public void populate(View v) {

        //helper.insertSmokePoint("2019-09-10 08:01");
        //helper.insertSmokePoint("2019-09-10 08:08");
        //helper.insertSmokePoint("2019-09-10 09:01");
        //helper.insertSmokePoint("2019-09-10 10:01");
        //helper.insertSmokePoint("2019-09-10 11:01");
        //helper.insertSmokePoint("2019-09-10 12:01");
        helper.insertSmokePoint("2019-09-12 06:10");
        helper.insertSmokePoint("2019-09-12 07:12");
        helper.insertSmokePoint("2019-09-12 08:05");
        helper.insertSmokePoint("2019-09-12 09:09");
        helper.insertSmokePoint("2019-09-12 10:01");
        helper.insertSmokePoint("2019-09-12 11:36");
        helper.insertSmokePoint("2019-09-12 12:01");
        helper.insertSmokePoint("2019-09-12 12:31");
        helper.insertSmokePoint("2019-09-12 13:39");
        helper.insertSmokePoint("2019-09-12 14:01");
        helper.insertSmokePoint("2019-09-12 15:16");
        helper.insertSmokePoint("2019-09-12 15:41");
        helper.insertSmokePoint("2019-09-12 16:01");
        //    helper.insertSmokePoint("2019-09-11 16:05");
        //    helper.insertSmokePoint("2019-09-11 16:11");
        //    helper.insertSmokePoint("2019-09-11 16:41");
        //    helper.insertSmokePoint("2019-09-11 16:51");
        //    helper.insertSmokePoint("2019-09-11 17:01");
        //    helper.insertSmokePoint("2019-09-11 18:01");
        //    helper.insertSmokePoint("2019-09-11 19:01");
        //    helper.insertSmokePoint("2019-09-11 20:11");

        //start the assignments from here too at this point. could do from db.
/*
        ArrayList<String> smokepointslist = new ArrayList<>();

        smokepointslist = helper.getSmoked();

        for (String smtemp : smokepointslist) {
            //SmokeExpander sme = new
            SmokeExpander(smtemp);
        }
*/

    }

    //dbg
    public void lataaPiste(View v) {


        System.out.println("tehdään hälypiste.....");
        //AlarmManager alarmManager=new AlarmManager();

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 05);


        long lngmstrig= 40000;

        Intent alarmIntent = new Intent(this,AlarmReceiver.class);
        pendingIntent=PendingIntent.getBroadcast(this,102,alarmIntent, 0);
        manager =(AlarmManager) getSystemService(ALARM_SERVICE);
        //manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP,40000,pendingIntent);
        System.out.println("hälypiste tehty.....");

    }


    //dbg
    public void cleanSmokePoint(View v) {

        Integer ok =helper.adminCleanSmokePoints();


    }

}


