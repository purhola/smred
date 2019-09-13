package com.purho.java.android.omat.smokereducer2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AdminActivity extends AppCompatActivity {



    private MyDbAdapter helper;
    ArrayList<Optimizer> allowedpoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //allowedpoints=new ArrayList<>();
        helper = new MyDbAdapter(this);

    }


public void populate(View v){

    helper.insertSmokePoint("2019-09-09 08:08");
    helper.insertSmokePoint("2019-09-09 09:01");
    helper.insertSmokePoint("2019-09-09 10:01");
    helper.insertSmokePoint("2019-09-09 11:01");
    helper.insertSmokePoint("2019-09-09 12:01");
    helper.insertSmokePoint("2019-09-09 13:01");
    helper.insertSmokePoint("2019-09-09 14:01");
    helper.insertSmokePoint("2019-09-09 15:01");
    helper.insertSmokePoint("2019-09-09 16:01");

    //start the assignments from here too at this point. could do from db.

/*    ArrayList<String> smokepointslist = new ArrayList<>();

    smokepointslist=helper.getSmoked();

    for(String smtemp:smokepointslist) {
        //SmokeExpander sme = new
                SmokeExpander(smtemp);
    }
*/



}

    //TODO need a job for copying the database from ext storage to take into use!!



    public void SmokeExpander(String smoketime){


        //String dateTime = "2018-12-11 17:30";
        LocalDateTime rsplus;
        LocalDateTime rsminus;
        String pointstemp;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime realSmokeTime = LocalDateTime.parse(smoketime, formatter);

        ArrayList<PointsAssigned> listSmokes = new ArrayList<PointsAssigned>();

        PointsAssigned p1 = new PointsAssigned(realSmokeTime,"100");
        //p1.setSmoketime(realSmokeTime);
        //p1.setPoints("10");

        listSmokes.add(p1);

        for (int i=1;i<10;i++) {
            int n = 100-i;
            PointsAssigned pplus = new PointsAssigned(realSmokeTime.plusMinutes(i),Integer.toString(100-i));
            PointsAssigned pminus = new PointsAssigned(realSmokeTime.minusMinutes(i),Integer.toString(100-2*i));

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


        for(PointsAssigned pointed:listSmokes) {

            System.out.println("Time and points: " + pointed.getTime() + " points " + pointed.getPoints());

            //insert these into table assignedpoints

            helper.upsertAssignedPoints(pointed.getTime(),pointed.getPoints());



        }


    }

    public void getAllowedPoints(View v){

        ScheduledRunner sr;
        Integer paivaannos=15;

        //here we might have the amount of smoketimes had the day before in order to reduce them
        allowedpoints=helper.getPointSummedSmokingTimes();

        for (Integer i=0;i< paivaannos;i++) {
            System.out.println("time: " + allowedpoints.get(i).getStrtime() + " points: " + allowedpoints.get(i).getStrpoints());

            //tehdaan ajastus jokaisesta
            ScheduledRunner(allowedpoints.get(i).getStrtime());

        }


    }

    public void ScheduledRunner(String smokeAllowed) {
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
        /*public static class MyTimeTask extends TimerTask
        {

            public void run()
            {

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
        }

    }



*/
}
