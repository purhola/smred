package com.purho.java.android.omat.smokereducer2;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SmokeExpander {

    private MyDbAdapter helper;

    public SmokeExpander(String smoketime){

        helper = new MyDbAdapter(this);

        //return LocalDateTime.now()
        //        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        //String dateTime = "2018-12-11 17:30";
        LocalDateTime rsplus;
        LocalDateTime rsminus;
        String pointstemp;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime realSmokeTime = LocalDateTime.parse(smoketime, formatter);

        ArrayList<PointsAssigned> listSmokes = new ArrayList<PointsAssigned>();

        PointsAssigned p1 = new PointsAssigned();
        p1.setSmoketime(realSmokeTime);
        p1.setPoints("10");

        listSmokes.add(p1);

        for (int i=1;i<10;i++) {
            PointsAssigned pplus = new PointsAssigned();
            PointsAssigned pminus = new PointsAssigned();
            rsplus=realSmokeTime.plusMinutes(i);
            System.out.println(rsplus);
            rsminus=realSmokeTime.minusMinutes(i);
            int n = 10-i;
            pointstemp=Integer.toString(n);
            pplus.setSmoketime(rsplus);
            pplus.setPoints(pointstemp);
            pminus.setSmoketime(rsminus);
            pminus.setPoints(pointstemp);

            listSmokes.add(pplus);
            listSmokes.add(pminus);
        }


        for(PointsAssigned pointed:listSmokes) {

            System.out.println("Time and points: " + pointed.getTime() + " points " + pointed.getPoints());

            //insert these into table assignedpoints

            helper.insertAssignedPoints(pointed.getTime(),pointed.getPoints());

            //TODO already these may overlap -> what to do -- google upsert?

        }


    }


}
