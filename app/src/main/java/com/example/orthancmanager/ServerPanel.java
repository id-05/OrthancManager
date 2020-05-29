package com.example.orthancmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.android.material.tabs.TabLayout;
import static com.example.orthancmanager.MainActivity.dbHelper;
import static com.example.orthancmanager.MainActivity.print;

public class ServerPanel extends AppCompatActivity implements ConnectionCallback{

    int id;
    OrthancServer server = new OrthancServer();
    public String jsonSetting;
    public static ViewPager viewPager;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_panel);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
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
        if (id == R.id.servpanelSetting) {
            Intent i = new Intent(ServerPanel.this, ServerSettings.class);
            i.putExtra("serverid", server.getId());
            i.putExtra("json", jsonSetting);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            jsonSetting = data;
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }
}

