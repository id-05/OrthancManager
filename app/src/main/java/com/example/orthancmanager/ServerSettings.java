package com.example.orthancmanager;



import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
//import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
//import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import static com.example.orthancmanager.MainActivity.dbHelper;
import static com.example.orthancmanager.MainActivity.print;

public class ServerSettings extends AppCompatActivity {

    int id;
    String json;
    OrthancServer server = new OrthancServer();
    LinearLayout layoutServerSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);
        setTitle("Orthanc.json");
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){ id = arguments.getInt("serverid");
            json = arguments.getString("json"); }

        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            String selection = "id = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            Cursor cursor = userDB.query("servers", null, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                server.name = (cursor.getString(cursor.getColumnIndex("name")));
                server.ipaddress = (cursor.getString(cursor.getColumnIndex("ip")));
                server.port = (cursor.getString(cursor.getColumnIndex("port")));
                server.login = (cursor.getString(cursor.getColumnIndex("login")));
                server.password = (cursor.getString(cursor.getColumnIndex("pass")));
                server.OS = (cursor.getString(cursor.getColumnIndex("OS")));
                server.pathToJson = (cursor.getString(cursor.getColumnIndex("pathjson")));
                server.CountInstances = (cursor.getInt(cursor.getColumnIndex("countinstances")));
                server.CountPatients = (cursor.getInt(cursor.getColumnIndex("countpatients")));
                server.CountSeries = (cursor.getInt(cursor.getColumnIndex("countseries")));
                server.CountStudies = (cursor.getInt(cursor.getColumnIndex("countstudies")));
                server.TotalDiskSizeMB = (cursor.getInt(cursor.getColumnIndex("totaldisksizemb")));
            }
            cursor.close();
        }catch (Exception e){
            MainActivity.print("error db serversettings = "+ e);
        }

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("servername", server.getName());
            editor.putString("StorageDirectory", "/etc/toto");
            editor.putString("HttpUserJson",json);
            //"RegisteredUsers" : {     "doctor" : "doctor"  }
            editor.commit();


            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragpref, new SettingsFragment())
                    .commit();
/*
            Fragment preferenceFragment = new PreferenceFragmentCustom();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragpref, preferenceFragment);
            ft.commit();
*/
        }catch (Exception e){
            MainActivity.print("SharedPreferences  "+e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.server_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.resetserver: {
                Toast toast = Toast.makeText(ServerSettings.this, "reset", Toast.LENGTH_SHORT); toast.show();
                return true;
            }

            case R.id.savesettings: {
                Toast toast = Toast.makeText(ServerSettings.this, "save", Toast.LENGTH_SHORT); toast.show();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);
        }
    }

}
