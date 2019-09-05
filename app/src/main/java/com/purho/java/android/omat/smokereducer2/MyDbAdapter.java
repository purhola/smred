package com.purho.java.android.omat.smokereducer2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.sql.Timestamp;
import java.util.ArrayList;

//note to self: THE DB GOES TO
// data/data/APP_Name/databases/DATABASE_NAME

//TODO stringsql
//compileStatement()
//Compiles an SQL statement into a reusable pre-compiled statement object.
////Use this method as below
////compileStatement(String sql)
//Pass sql statement as a parameter in string the format.

public class MyDbAdapter implements Serializable {

    private myDbHelper myhelper;
    private Integer intpoints;


    public MyDbAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //INSERT

    public long insertSmokePoint(String smokepoint)
    {
        String[] sqlargs={smokepoint};
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.SMOKEPOINT, sqlargs[0]);

        long insert = dbb.insert(myDbHelper.TABLE_SMOKEPOINT, null , contentValues);

        return insert;
    }

    public long insertAssignedPoints(String smoketime,String points)
    {

        /*INSERT INTO phonebook2(name,phonenumber,validDate)
        VALUES('Alice','704-555-1212','2018-05-08')
        ON CONFLICT(name) DO UPDATE SET
        phonenumber=excluded.phonenumber,
                validDate=excluded.validDate
        WHERE excluded.validDate>phonebook2.validDate;
        */

        //private static final String TABLE_ASSIGNED = "assignedpoints";   //
        //private static final String DAILYSMOKEPOINT = "dailysmokepoint";     //
        //private static final String DAILYPOINTS = "dailypoints";
        intpoints=0;
        intpoints= Integer.parseInt(points);

        SQLiteDatabase db = myhelper.getWritableDatabase();
        String sqlquery="INSERT INTO assignedpoints (dailysmokepoint,dailypoints) "+
                "VALUES( " + smoketime +", " + points + ") " +
                "ON CONFLICT (dailysmokepoint) DO UPDATE SET " +
                "dailypoints = dailypoints + " + intpoints + " " +
                "WHERE dailysmokepoint = " + smoketime + ";";

        System.out.println("SQL QUERY insertdailypoints LASSI " + sqlquery);
        Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();

        /*
        String[] sqlargs={smoketime,points};
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.DAILYSMOKEPOINT, sqlargs[0]);
        contentValues.put(myDbHelper.DAILYPOINTS, sqlargs[1]);
        long insert = dbb.insert(myDbHelper.TABLE_ASSIGNED, null , contentValues);
        return insert;
         */
    }


    //SELECT
/*
    //lets fetch the list of JUVINILES
    public ArrayList<Juvinile> getJuvinileList() //the select query here as string parameter?
    {

        //list for storing the results
        ArrayList<Juvinile> juvinilesList = new ArrayList<Juvinile>();

        String selectQuery = "SELECT * FROM " + myDbHelper.TABLE_JUVINILE;

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                Juvinile tempjuvi = new Juvinile();

                //read the db
                juvilineid=c.getInt(c.getColumnIndex(myDbHelper.JUVINILEID));
                name=c.getString(c.getColumnIndex(myDbHelper.JUVINILENAME));
                location=c.getString(c.getColumnIndex(myDbHelper.CITY));
                address=c.getString(c.getColumnIndex(myDbHelper.ADDRESS));

                //Assign the values to juviline object
                tempjuvi.setJuvinileID(juvilineid);
                tempjuvi.setAddress(address);
                tempjuvi.setName(name);
                tempjuvi.setCity(location);

                //add the object to the list
                juvinilesList.add(tempjuvi);
            } while (c.moveToNext());
            Log.d("array",juvinilesList.toString());
        }
        return juvinilesList;
    }

    //SELECT FEEDBACKS
    public ArrayList<EventFeedBack> getEventFeedBacks(Integer eventid){

        //Variables for storing the results
        String[] s_eventid={Integer.toString(eventid)};
        ArrayList<EventFeedBack> feedBackList = new ArrayList<EventFeedBack>();

        String selectQuery = "SELECT * FROM " + myDbHelper.TABLE_FEEDBACK + " WHERE " + myDbHelper.JEVENTID + " = " +
                eventid + " ORDER BY DBTIME ASC";

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                EventFeedBack tempfeedback = new EventFeedBack();

                //read the db

                feedbackid=c.getInt(c.getColumnIndex(myDbHelper.FEEDBACKID));
                grade=c.getInt(c.getColumnIndex(myDbHelper.GRADE));
                feedback=c.getString(c.getColumnIndex(myDbHelper.FEEDBACK));
                name=c.getString(c.getColumnIndex(myDbHelper.PARTICIPANT));
                //coalesce broke the db cursor, so let's do it here.
                if(name == null) name="Anonymous";

                //Assign the values to a feedback object
                tempfeedback.setEventid(eventid);
                tempfeedback.setFeedbackid(feedbackid);
                tempfeedback.setGrade(Integer.toString(grade));
                tempfeedback.setFeedback(feedback);
                tempfeedback.setFbgiver(name);

                //add the object to the list
                feedBackList.add(tempfeedback);
            } while (c.moveToNext());
            Log.d("array",feedBackList.toString());
        }
        c.close();
        return feedBackList;

    }

    //DELETE

    //need many of these, or preferably make this delete a more generic taking only a fully prepared sql as parameter
    public  int delete(String[] ID) //poistetaan id ID taulusta TABLE_NAME
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();


        int count =db.delete(myDbHelper.TABLE_JUVINILE ,myDbHelper.JUVINILEID + " = ?", ID);
        return  count;
    }

    //UPDATE



    //update Juvinile data
    public void updateJuvinileDetails(Integer juvinileid,String column,String new_value)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String sqlquery="UPDATE JUVINILE SET " + column + " = \'" + new_value + "\' , dbchange = DATETIME(CURRENT_TIMESTAMP, 'localtime') WHERE juvinileid = " + juvinileid  ;
        System.out.println("SQL QUERY LASSI " + sqlquery);
        Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();
    }


 */


    //table variables and DATABASE VERSION and name
    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "playtodella1";    // Database Name
        private static final int DATABASE_Version = 1;    // Database Version

        //table smokepoints
        private static final String TABLE_SMOKEPOINT = "smokepoint";   //
        private static final String SMOKEPOINT = "smokepoint";     //

        //table assignedpoints
        private static final String TABLE_ASSIGNED = "assignedpoints";   //
        private static final String DAILYSMOKEPOINT = "dailysmokepoint";     //
        private static final String DAILYPOINTS = "dailypoints";     //

        //table cumulativepoints
        private static final String TABLE_CUMULATIVE = "cumulativepoints";   //
        private static final String SMOKETIME = "smoketime";     //
        private static final String CUMULATIVEPOINTS = "cumulativesmokepoints";     //



        //sentences to handle table smokepoint
        private static final String CREATE_TABLE_SMOKEPOINT =
                "CREATE TABLE " + TABLE_SMOKEPOINT + " ("
                        + SMOKEPOINT + " DATETIME "
                        + ");";

        //sentences to handle table assignedpoints
        private static final String CREATE_TABLE_ASSIGNED =
                "CREATE TABLE " + TABLE_ASSIGNED + " ("
                        + DAILYSMOKEPOINT + " DATETIME "
                        + DAILYPOINTS + " INTEGER "
                        + ");";

        //sentences to handle table assignedpoints
        private static final String CREATE_TABLE_CUMULATIVE =
                "CREATE TABLE " + TABLE_CUMULATIVE + " ("
                        + SMOKETIME + " TIME "
                        + CUMULATIVEPOINTS + " INTEGER "
                        + ");";



        private static final String CREATE_INDEX_ASSIGNEDPOINTS = "CREATE UNIQUE INDEX idx_dailysmokepoint ON ASSIGNEDPOINTS (dailysmokepoint);";

        private static final String DROP_TABLE_SMOKEPOINT = "DROP TABLE IF EXISTS " + TABLE_SMOKEPOINT;
        private static final String DROP_TABLE_ASSIGNED = "DROP TABLE IF EXISTS " + TABLE_ASSIGNED;
        private static final String DROP_TABLE_CUMULATIVE = "DROP TABLE IF EXISTS " + TABLE_CUMULATIVE;



        //**************************************************************************************
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {



            try {
                db.execSQL(CREATE_TABLE_SMOKEPOINT);
                db.execSQL(CREATE_TABLE_ASSIGNED);
                db.execSQL(CREATE_TABLE_CUMULATIVE);
                db.execSQL(CREATE_INDEX_ASSIGNEDPOINTS);

            } catch (Exception e) {
                Message.message(context, "" + e);

            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "OnUpgrade");
                db.execSQL(DROP_TABLE_SMOKEPOINT);
                db.execSQL(DROP_TABLE_ASSIGNED);
                db.execSQL(DROP_TABLE_CUMULATIVE);
                onCreate(db);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }


    }
}
