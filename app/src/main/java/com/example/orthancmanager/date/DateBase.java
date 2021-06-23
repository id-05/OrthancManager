package com.example.orthancmanager.date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DateBase extends SQLiteOpenHelper {

    public DateBase(Context context) {
        super(context, "orthanc", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase orthancDB) {
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase orthancDB, int i, int i1) {

    }
}
