package com.purho.java.android.omat.smokereducer2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Optimizer {

    private String strtime;
    private String strpoints;
    private LocalDateTime time;
    private Integer points;
    private String strdailytimes;
    private String strdailypoints;

    public Optimizer(String strtime,String strpoints){

        //this.time=strtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.points=Integer.parseInt(strpoints);


        //save to db
        //calculate points
        //find next time to push a note
        //push the note




    }
    public Optimizer(){

        //this.time=strtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //this.points=Integer.parseInt(strpoints);


        //save to db
        //calculate points
        //find next time to push a note
        //push the note




    }

    public void pushNote (){

      /*  NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                setContentTitle(subject).setSmallIcon(R.drawable.abc).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);


       */
    }

    public String getStrtime() {
        return strtime;
    }

    public void setStrtime(String strtime) {
        this.strtime = strtime;
    }

    public String getStrpoints() {
        return strpoints;
    }

    public void setStrpoints(String strpoints) {
        this.strpoints = strpoints;
    }

    public String getStrdailytimes() {
        return strdailytimes;
    }

    public void setStrdailytimes(String strdailytimes) {
        this.strdailytimes = strdailytimes;
    }

    public String getStrdailypoints() {
        return strdailypoints;
    }

    public void setStrdailypoints(String strdailypoints) {
        this.strdailypoints = strdailypoints;
    }
}
