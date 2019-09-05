package com.purho.java.android.omat.smokereducer2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PointsAssigned {

    private LocalDateTime smoketime;
    private String time;
    private String points;

    public pointsAssigned() {
        //to do, come code


        this.smoketime= LocalDateTime.now();
        this.points="0";

        this.time=smoketime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //System.out.println("Smoketime: " + smoketime + " points: " + points);
    }

    public pointsAssigned(LocalDateTime smoketime,String points) {

        this.smoketime=smoketime;
        this.points=points;

        //System.out.println("Smoketime: " + smoketime + " points: " + points);



    }

    public LocalDateTime getSmoketime() {
        return smoketime;
    }

    public void setSmoketime(LocalDateTime smoketime) {

        this.smoketime = smoketime;
        this.time=smoketime.format(DateTimeFormatter.ofPattern("HH:mm"));
        //System.out.println("Smoketime: setter " + smoketime);
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
