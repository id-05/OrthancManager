package com.example.orthancmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity implements ConnectionCallback {


    //public SQLiteDatabase orthancmanagerDB;
    //public DBmanager dBmanager = new DBmanager(this.getApplicationContext());
    private ArrayList<OrthancServer> ServerList = new ArrayList<>();// = dBmanager.GetServerList();
    private Button test;
    private Button test2;
    Connection newConn;
    public static DateBase dbHelper;
    public ServerCardAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        dbHelper = new DateBase(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ServerCardAdapter(ServerList,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        ServerList.clear();
        GetServerList(ServerList);

        for (final OrthancServer server:ServerList){
            try{
                //if(server.getName() == null)
                {
                    doSomethingAsyncOperaion(server,"/system");
                }
            }catch (Exception e){
                print("error resume /system "+e.toString());
            }
        }

        for (final OrthancServer server:ServerList){
            try{
                doSomethingAsyncOperaion(server,"/statistics");
            }catch (Exception e){
                print("error resume /statistics "+e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_form_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.TitleAddServer:
                Intent i = new Intent(MainActivity.this, AddNewServer.class);
                i.putExtra("method","new");
                startActivity(i);
                return true;
            case R.id.TitleSittings:
                Intent j = new Intent(MainActivity.this, ProgrammSetting.class);
                startActivity(j);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    public static void print(String str){
        Log.d("orthanclog",str);
    }

    public void GetServerList(ArrayList<OrthancServer> serverList){
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("servers", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    OrthancServer server = new OrthancServer();
                    server.setId(cursor.getInt(cursor.getColumnIndex("id")));
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
                    server.setConnect(false);
                    serverList.add(server);
                }
                while (cursor.moveToNext());
            }else {
                //серверов ещё не добавлено
            }
            cursor.close();
        }catch (SQLException e){
            MainActivity.print(e.toString());
        }

    }

    public static void serverAddBase(OrthancServer server){
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.name);
        newValues.put("ip",server.ipaddress);
        newValues.put("port",server.port);
        newValues.put("login",server.login);
        newValues.put("pass",server.password);
        newValues.put("OS",server.OS);
        newValues.put("pathjson",server.pathToJson);
        newValues.put("comment","test");
        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            long rowID = userDB.insertOrThrow("servers", null, newValues);
            userDB.close();
        }catch (SQLException e){
            print("error add to base "+e.toString());
        }
    }

    public static void serverDelBase(OrthancServer server) {
        int id = server.getId();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.delete("servers","id = " + id, null);
    }

    public static void serverUpdateBase(OrthancServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.getName());
        newValues.put("dicomaet",server.getDicomaet());
        newValues.put("countinstances",server.getCountInstances());
        newValues.put("countpatients",server.getCountPatients());
        newValues.put("countseries",server.getCountSeries());
        newValues.put("countstudies",server.getCountStudies());
        newValues.put("totaldisksizemb",server.getTotalDiskSizeMB());

        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.update("servers", newValues, "id = ?",
                new String[] {String.valueOf(id)});
    }

    public static void serverConnectionUpdateBase(OrthancServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
        newValues.put("ip",server.getIpaddress());
        newValues.put("port",server.getPort());
        newValues.put("login",server.getLogin());
        newValues.put("pass",server.getPassword());
        newValues.put("OS",server.getOS());
        newValues.put("pathjson",server.getPathToJson());
        //print("main activity server.getPathToJson() = "+server.getPathToJson());
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.update("servers", newValues, "id = ?",
                new String[] {String.valueOf(id)});
    }

    private void doSomethingAsyncOperaion(final OrthancServer server, final String param) {
        new AbstractAsyncWorker<String>(this,server,param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {

                String result = null;
                String auth =new String(server.login + ":" + server.password);
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                sleep(100);
                try {
                    String fulladdress = "http://"+server.ipaddress+":"+server.port;
                    URL url = new URL(fulladdress+param);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    //connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    //connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        boolean resultConnect = true;
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        result = sb.toString();
                        //MainActivity.print(result);
                    }else {
                        //resultConnect = false;
                    }
                    connection.disconnect();
                }catch (Exception e) {
                    //MainActivity.print("error get thread :"+e.toString());
                }

                return result;
            }
        }.execute();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
       // MainActivity.print("data = "+data);
        switch (param) {
            case "/system": {
                try {
                    JsonParser parser = new JsonParser();
                    JsonObject orthancJson = parser.parse(data).getAsJsonObject();
                    server.setName(orthancJson.get("Name").getAsString());
                    server.setDicomaet(orthancJson.get("DicomAet").getAsString());
                    serverUpdateBase(server);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    print("Error /system " + e.toString());
                }
            }
                    break;

            case "/statistics":
            {        try{
                    server.setConnect(true);
                    JsonParser parser = new JsonParser();
                    JsonObject orthancJson = parser.parse(data).getAsJsonObject();
                    server.setCountInstances(orthancJson.get("CountInstances").getAsInt());
                    server.setCountPatients(orthancJson.get("CountPatients").getAsInt());
                    server.setCountSeries(orthancJson.get("CountSeries").getAsInt());
                    server.setCountStudies(orthancJson.get("CountStudies").getAsInt());
                    server.setTotalDiskSizeMB(orthancJson.get("TotalDiskSizeMB").getAsInt());
                    serverUpdateBase(server);
                adapter.notifyDataSetChanged();
                }catch (Exception e){
                    print("Error /statistics "+e.toString());
                }
            }
            break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {
        //MainActivity.print("failure");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEnd() {

    }
}
