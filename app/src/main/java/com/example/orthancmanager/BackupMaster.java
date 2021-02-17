package com.example.orthancmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class BackupMaster extends AppCompatActivity {

    int id;
    String backupsettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_master);

        setTitle("Settings backup Master");
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id = arguments.getInt("serverid");
            backupsettings = arguments.getString("backupsettings");
        }
        Log.d("orthanclog",backupsettings);
    }
}