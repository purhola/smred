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
    private String strsmoketime;
    private String strsmokepoint;
    private String strpoints;
    private String strdailytimes;
    private String strdailypoints;


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
        dbb.close();
        return insert;
    }

    public void upsertAssignedPoints(String smoketime,String points)
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
        String updatequery=
                "UPDATE assignedpoints SET dailypoints=dailypoints + " + intpoints +
                " WHERE dailysmokepoint= '" + smoketime + "';";
        System.out.println("SQL QUERY UPDATEdailypoints LASSI " + updatequery);
        Cursor c = db.rawQuery(updatequery, null);
        c.moveToFirst();
        c.close();

        String insertquery=
                "INSERT OR IGNORE INTO assignedpoints (dailysmokepoint,dailypoints) "+
                "VALUES( '" + smoketime +"', " + points + ");";
        System.out.println("SQL QUERY INSERTdailypoints LASSI " + insertquery);
        Cursor d = db.rawQuery(insertquery, null);
        d.moveToFirst();
        d.close();

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

    public void insertCumulativePoints() {
        //SQL:  insert into cumulativepoints(smoketime,cumulativesmokepoints) select time(dailysmokepoint) as timepoint,sum(dailypoints) as sumpoints from assignedpoints where date(dailysmokepoint) > date('now','-8 days') and date(dailysmokepoint) < date('now') group by time(dailysmokepoint) order by sum(dailypoints) desc;
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String insertquery=
                "insert into cumulativepoints(smoketime,cumulativesmokepoints) " +
                "select time(dailysmokepoint) as timepoint,sum(dailypoints) as sumpoints " +
                "from assignedpoints where date(dailysmokepoint) > date('now','-8 days') and " +
                "date(dailysmokepoint) < date('now') " +
                "group by time(dailysmokepoint);";
        System.out.println("SQL QUERY insert CUMULATIVEPOINTS LASSI " + insertquery);
        Cursor c = db.rawQuery(insertquery, null);
        c.moveToFirst();
        c.close();
    }


    //SELECT

    //select for the saved smoking times

    public ArrayList<String> getSmoked() {
        ArrayList<String> smoked = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + myDbHelper.TABLE_SMOKEPOINT + " WHERE date(smokepoint) > date('now','-7 day') "
                +"order by smokepoint asc;";

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                //read the db
                strsmokepoint= c.getString(c.getColumnIndex("smokepoint"));

                //add the object to the list
                smoked.add(strsmokepoint);
            } while (c.moveToNext());
            Log.d("array",smoked.toString());
        }
        c.close();
        return smoked;

    }

    //select for the saved smoking times

    public Integer getSmokeCount() {
        Integer countti=0;
        String selectQuery = "SELECT count(*) as count FROM " + myDbHelper.TABLE_SMOKEPOINT + " WHERE date(smokepoint) = date('now','-1 day');";

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                //read the db
                countti= c.getInt(c.getColumnIndex("count"));
            } while (c.moveToNext());
            Log.d("countti",Integer.toString(countti));
        }
        c.close();
        return countti;

    }

    public ArrayList<Optimizer> selectAllowedSmokeTimes() {
        //SQL: select time((strftime('%s',smoketime) / 700)*700,'unixepoch') interval,avg(cumulativesmokepoints) from cumulativepoints group by interval order by avg(cumulativesmokepoints) desc;
        //list for storing the results
        ArrayList<Optimizer> optimizeList = new ArrayList<Optimizer>();
        System.out.println("at the dbadapter");
        String selectQuery =
                "select time((strftime('%s',smoketime) / 700)*700,'unixepoch') interval, " +
                "avg(cumulativesmokepoints) as avgpoints from cumulativepoints " +
                "group by interval order by avg(cumulativesmokepoints) desc;";

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                Optimizer tempOpti = new Optimizer();

                //read the db
                strsmoketime=c.getString(c.getColumnIndex("interval"));
                strpoints=c.getString(c.getColumnIndex("avgpoints"));

                //Assign the values to juviline object
                tempOpti.setStrtime(strsmoketime);
                tempOpti.setStrpoints(strpoints);

                //add the object to the list
                optimizeList.add(tempOpti);
            } while (c.moveToNext());
            Log.d("array",optimizeList.toString());
        }

        return optimizeList;

    }

    //lets fetch the list of point assigned smoketimes
    //gouped by time and with points summed
    public ArrayList<Optimizer> getPointSummedSmokingTimes() //the select query here as string parameter?
    {

        //list for storing the results
        ArrayList<Optimizer> optimizeList = new ArrayList<Optimizer>();
        System.out.println("at the dbadapter");
        String selectQuery =
                "select time(dailysmokepoint) as timepoint,sum(dailypoints) as sumpoints from assignedpoints " +
                "where date(dailysmokepoint) > date('now','-7 day') " +
                "and date(dailysmokepoint) < date('now') " +
                "group by time(dailysmokepoint) order by sum(dailypoints) desc;";

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                Optimizer tempOpti = new Optimizer();

                //read the db
                strsmoketime=c.getString(c.getColumnIndex("timepoint"));
                strpoints=c.getString(c.getColumnIndex("sumpoints"));

                //Assign the values to juviline object
                tempOpti.setStrtime(strsmoketime);
                tempOpti.setStrpoints(strpoints);

                //add the object to the list
                optimizeList.add(tempOpti);
            } while (c.moveToNext());
            Log.d("array",optimizeList.toString());
        }

        return optimizeList;
    }

    public ArrayList<Optimizer> getValuedSmokingTimes() //the select query here as string parameter?
    {

        //list for storing the results
        ArrayList<Optimizer> optimizeList = new ArrayList<Optimizer>();

        String selectQuery =
                "select dailysmokepoint,dailypoints from assignedpoints " +
                        "where date(dailysmokepoint) > date('now','-7 day') " +
                        "order by dailysmokepoint asc;";

        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        //go through the whole table
        if(c.moveToFirst()) {
            do {
                Optimizer tempOpti = new Optimizer();

                //read the db
                strdailytimes=c.getString(c.getColumnIndex("dailysmokepoint"));
                strdailypoints=c.getString(c.getColumnIndex("dailypoints"));

                //Assign the values to juviline object
                tempOpti.setStrdailytimes(strdailytimes);
                tempOpti.setStrdailypoints(strdailypoints);

                //add the object to the list
                optimizeList.add(tempOpti);
            } while (c.moveToNext());
            Log.d("array",optimizeList.toString());
        }
        return optimizeList;
    }


/*
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
*/
    public int cleanAssignedPoints() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        int count =db.delete(myDbHelper.TABLE_ASSIGNED,null,null);
        return  count;
    }

    public int cleanCumulativepoints() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        int count =db.delete(myDbHelper.TABLE_CUMULATIVE,null,null);
        return  count;
    }

    public int adminCleanSmokePoints () {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String rajaus[]={"2019-09-09 00:00:00"};
        int count =db.delete(myDbHelper.TABLE_SMOKEPOINT,myDbHelper.SMOKEPOINT + "< ?",rajaus);
        return  count;


    }


    //UPDATE

/*

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
        private static final String DATABASE_NAME = "playtodella1";    // Database Name //TODO OLI playtodella1
        private static final int DATABASE_Version = 5;    // Database Version  //TODO OLI  5 PALAUTA

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
                        + DAILYSMOKEPOINT + " DATETIME, "
                        + DAILYPOINTS + " INTEGER "
                        + ");";

        //sentences to handle table assignedpoints
        private static final String CREATE_TABLE_CUMULATIVE =
                "CREATE TABLE " + TABLE_CUMULATIVE + " ("
                        + SMOKETIME + " TIME, "
                        + CUMULATIVEPOINTS + " INTEGER "
                        + ");";



        private static final String CREATE_INDEX_ASSIGNEDPOINTS = "CREATE UNIQUE INDEX idx_dailysmokepoint ON ASSIGNEDPOINTS (dailysmokepoint);";
        private static final String CREATE_INDEX_CUMULATIVEPOINTS = "CREATE UNIQUE INDEX idx_smoketime ON cumulativepoints (smoketime);";

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

            try {db.execSQL(CREATE_TABLE_SMOKEPOINT);} catch (Exception e) {Message.message(context, "" + e);}
            try {db.execSQL(CREATE_TABLE_ASSIGNED);} catch (Exception e2) {Message.message(context, "" + e2);}
            try {db.execSQL(CREATE_TABLE_CUMULATIVE);} catch (Exception e3) {Message.message(context, "" + e3);}
            try {db.execSQL(CREATE_INDEX_ASSIGNEDPOINTS);} catch (Exception e4) {Message.message(context, "" + e4);}

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "OnUpgrade");
              //  db.execSQL(DROP_TABLE_SMOKEPOINT);
                db.execSQL(DROP_TABLE_ASSIGNED);
                db.execSQL(DROP_TABLE_CUMULATIVE);
                onCreate(db);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }


    }
}
