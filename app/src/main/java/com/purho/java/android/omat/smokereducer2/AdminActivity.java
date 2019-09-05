package com.purho.java.android.omat.smokereducer2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

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

}



}
