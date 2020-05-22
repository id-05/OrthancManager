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

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.orthancmanager.MainActivity.dbHelper;
import static com.example.orthancmanager.MainActivity.print;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerPanel extends AppCompatActivity implements ConnectionCallback{

    String selection = null;
    String[] selectionArgs = null;
    int id;
    OrthancServer server = new OrthancServer();
    public String jsonSetting;
    public static ViewPager viewPager;
    //ServerPanelViewer frag1;
    //ServerPanelSetting frag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_panel);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem tabView = findViewById(R.id.tabItem1);
        TabItem tabSettings = findViewById(R.id.tabItem2);
        viewPager = findViewById(R.id.viewPage);
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id = arguments.getInt("serverid");
        }
        server = MainActivity.getServerById(id);

        try {
            ServerPanelPageAdapter serverPanelPageAdapter = new ServerPanelPageAdapter(server, getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(serverPanelPageAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }catch (Exception e){
            print("error frag ="+e.toString());
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

    public static void TabChange(int i){
        viewPager.setCurrentItem(i);
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
        //getOrthancSettings(server,null);
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
                            //MainActivity.print(line);
                           // if(truestring(line))
                            {
                                int i = line.indexOf("}");
                                if(i!= -1){
                                    if (((i == (line.length()-1))&(i!=0))) {
                                       // sb.append(line + ","); //??????????????
                                        sb.append(line);
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


    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()){
            case 0:
                super.onBackPressed();
                break;
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(1);
                break;
            case 3:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }

    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
        /////////////01.05.2020
        try {
            jsonSetting = data;
            //MainActivity.print("data = " + data);
            }catch (Exception e){
                MainActivity.print(e.toString());//JsonParser parser = new JsonParser();
            }

    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }
}

