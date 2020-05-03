package com.example.orthancmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
//import android.app.FragmentTransaction;
//import android.app.FragmentTransaction;
//import android.app.Fragment;

//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import static com.example.orthancmanager.MainActivity.dbHelper;
import static com.example.orthancmanager.MainActivity.print;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerPanel extends AppCompatActivity implements ConnectionCallback{

    String selection = null;
    String[] selectionArgs = null;
    int id;
    OrthancServer server = new OrthancServer();
    public String jsonSetting;
    //ServerPanelViewer frag1;
    //ServerPanelSetting frag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_panel);
       // TabLayout tabLayout = findViewById(R.id.tabLayout);
       // TabItem tabView = findViewById(R.id.tabView);
       // TabItem tabSettings = findViewById(R.id.tabSettings);
       // final ViewPager viewPager = findViewById(R.id.viewPager);


        try {
         //   ServerPanelPageAdapter serverPanelPageAdapter = new ServerPanelPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
         //   viewPager.setAdapter(serverPanelPageAdapter);
         //   viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        }catch (Exception e){
            print("error frag ="+e.toString());
        }
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id = arguments.getInt("serverid");
        }

        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            String selection = "id = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            Cursor cursor = userDB.query("servers", null, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                server.id = (cursor.getInt(cursor.getColumnIndex("id")));
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
            MainActivity.print("error = "+ e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.server_panel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.servpanelSetting: {
                Intent i = new Intent(ServerPanel.this, ServerSettings.class);
                i.putExtra("serverid", server.getId());
                i.putExtra("json",jsonSetting);
                //MainActivity.print(String.valueOf(server.getId()));
                startActivity(i);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrthancSettings(server,null);
        //frag1 = new ServerPanelViewer();
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.replace(R.id.frag,frag1);
        //ft.commit();
    }

    private void getOrthancSettings(final OrthancServer server, final String param) {
        new AbstractAsyncWorker<String>(this,server,param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {
                String urlParameters = "f = io.open(\"/\\etc\\/orthanc\\/orthanc.json\",\"r+\");" +
                        "print(f:read(\"*a\"))"+
                        "f:close()";
                String result = null;
                String auth =new String(server.login + ":" + server.password);
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    String fulladdress = "http://"+server.ipaddress+":"+server.port;
                    URL url = new URL(fulladdress+"/tools/execute-script");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    //connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length",  String.valueOf(fulladdress+urlParameters));
                    //connection.setRequestProperty("Accept", "application/json");
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("POST");
                    connection.connect();
                    OutputStream os = connection.getOutputStream();
                    os.write(urlParameters.getBytes());
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //Read
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            if(truestring(line)){
                                int i = line.indexOf("}");
                                if(i!= -1){
                                    if ((i == (line.length()-1)&(i!=0))) {
                                        sb.append(line + ",");
                                    }else
                                    {
                                        sb.append(line);
                                    }
                                }else {
                                    sb.append(line);
                                }
                            }
                        }
                        bufferedReader.close();
                        result = sb.toString();
                    }else {
                        MainActivity.print( "eRROR1 =  "+responseCode);
                    }
                    os.close();
                }catch (Exception e) {
                    MainActivity.print("error get thread :"+e.toString());
                }

                return result;
            }
        }.execute();
    }

    private static Boolean truestring(String str){
        String buf = "/*";
        Boolean troubleSimbol = true;
        Character char2 = buf.charAt(0);
        Character char3 = buf.charAt(1);
        for(int i=0;i<(str.length());i++){
            Character char1 = str.charAt(i);
            if((char1 == char2)|(char1 == char3)){
                troubleSimbol = false;
            }
        }
        return troubleSimbol;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
        /////////////01.05.2020

        jsonSetting = data;
        //MainActivity.print("data = "+data);
        JsonParser parser = new JsonParser();
        JsonObject orthancJson = parser.parse(data).getAsJsonObject();
        JsonObject users = new JsonObject();
        users= orthancJson.get("RegisteredUsers").getAsJsonObject();
        //print(users.toString());
        //Set<String> keys = users.keySet();
        //for(String key:keys){
        //    print(key.toString());
        //}


        //Iterator<String> iter = users. //This should be the iterator you want.
        //while(keys){
        //    String key = iter.next();
       // }
        //MainActivity.print(orthancJson.get("Name").getAsString());
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }
}

