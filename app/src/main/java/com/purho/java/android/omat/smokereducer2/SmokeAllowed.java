package com.purho.java.android.omat.smokereducer2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SmokeAllowed extends AppCompatActivity {

    private MyDbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke_allowed);

        helper = new MyDbAdapter(this);



    }

    public String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void saveAllowedCig(View v) {

        String smoketime = getCurrentLocalDateTimeStamp();
        //System.out.println("mitähän  " + smoketime);
        long id = 0;


        id = helper.insertSmokePoint(smoketime);
        if (id > 0) Toast.makeText(this, "Cig saved", Toast.LENGTH_LONG).show();
    }

}
