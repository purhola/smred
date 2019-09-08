package com.purho.java.android.omat.smokereducer2;

import android.icu.text.SimpleDateFormat;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//näitä tehdään sitten häly per savu

public class ScheduledRunner {


    public ScheduledRunner(String smokeAllowed) {
        //the Date and time at which you want to execute
    //    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     //   Date date = ((SimpleDateFormat) dateFormatter).parse("2012-07-06 13:05:45");

        //TODO tämä päivä ja smokeallowedhan on tuntimuodossa.



        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once
     //   timer.schedule(new MyTimeTask(), date);

        //Use this if you want to execute it repeatedly
        //int period = 10000;//10secs
        //timer.schedule(new MyTimeTask(), date, period );

    }

//The task which you want to execute
private static class MyTimeTask extends TimerTask
{

    public void run()
    {
        //write your code here
        //Tästäpä vaikka notifikaatio liikkeelle
    }
}

}
