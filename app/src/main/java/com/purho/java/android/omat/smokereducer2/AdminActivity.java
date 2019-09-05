package com.purho.java.android.omat.smokereducer2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private MyDbAdapter helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        helper = new MyDbAdapter(this);

    }


public void populate(View v){

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

    //start the assignments from here too at this point. could do from db.

    ArrayList<String> smokepointslist = new ArrayList<>();
    smokepointslist.add("2019-09-04 08:01");
    smokepointslist.add("2019-09-04 08:08");
    smokepointslist.add("2019-09-04 09:01");
    smokepointslist.add("2019-09-04 10:01");
    smokepointslist.add("2019-09-04 11:01");
    smokepointslist.add("2019-09-04 12:01");
    smokepointslist.add("2019-09-04 13:01");
    smokepointslist.add("2019-09-04 14:01");
    smokepointslist.add("2019-09-04 15:01");
    smokepointslist.add("2019-09-04 16:01");

    for(String smtemp:smokepointslist) {
        //SmokeExpander sme = new
                SmokeExpander(smtemp);
    }




}


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

            //TODO already these may overlap -> what to do -- google upsert?

        }


    }




}
