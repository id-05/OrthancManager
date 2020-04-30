package com.example.orthancmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DateBase extends SQLiteOpenHelper {

    public DateBase(Context context) {
        super(context, "orthanc", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase orthancDB) {
        //final Random random = new Random();
        String SQL =
       "create table servers ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "date integer,"
                + "ip text,"
                + "port text,"
                + "login text,"
                + "pass text,"
                + "OS text,"
                + "pathjson text,"
               + "version text,"
               + "dicomaet text,"
               + "countinstances integer,"
               + "countpatients integer,"
               + "countseries integer,"
               + "countstudies integer,"
               + "totaldisksizemb integer,"
                + "comment text" + ");";
        orthancDB.execSQL(SQL);
/*
        ContentValues newValues = new ContentValues();
        newValues.put("name","t1");
        newValues.put("ip","8.8.8.8");
        newValues.put("port","test");
        newValues.put("login","test");
        newValues.put("pass","test");
        newValues.put("OS","test");
        newValues.put("pathjson","test");
        newValues.put("comment","test");
        orthancDB.insert("servers", null, newValues);

        ContentValues newValues2 = new ContentValues();
        newValues2 = new ContentValues();
        newValues2.put("name","test2");
        newValues2.put("ip","192.168.0.1");
        newValues2.put("port","test");
        newValues2.put("login","test");
        newValues2.put("pass","test");
        newValues2.put("OS","test");
        newValues2.put("pathjson","test");
        newValues2.put("comment","test");
        orthancDB.insert("servers", null, newValues2);

        ContentValues newValues3 = new ContentValues();
        newValues3 = new ContentValues();
        newValues3.put("name","test3");
        newValues3.put("ip","192.168.0.254");
        newValues3.put("port","test");
        newValues3.put("login","test");
        newValues3.put("pass","test");
        newValues3.put("OS","test");
        newValues3.put("pathjson","test");
        newValues3.put("comment","test");
        orthancDB.insert("servers", null, newValues3);
  */
    }

    @Override
    public void onUpgrade(SQLiteDatabase orthancDB, int i, int i1) {

    }
}
