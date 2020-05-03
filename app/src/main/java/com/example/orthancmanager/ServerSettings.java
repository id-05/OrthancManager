package com.example.orthancmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
//import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
//import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.orthancmanager.MainActivity.dbHelper;
import static com.example.orthancmanager.MainActivity.print;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerSettings extends AppCompatActivity implements ConnectionCallback{

    int id;
    String json;
    OrthancServer server = new OrthancServer();
    int modeCallback;
    //1 - read
    //2 - write
    //3 - reset
    SharedPreferences prefs;// = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor;// = prefs.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);
        setTitle("Orthanc.json");
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id = arguments.getInt("serverid");
            //json = arguments.getString("json");
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

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
            modeCallback = 1;
            getOrthancSettings(server,null);
        }catch (Exception e){
            MainActivity.print("error db serversettings = "+ e);
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
                MainActivity.print(prefs.getString("TransferSyntax","none"));
                Toast toast = Toast.makeText(ServerSettings.this, "save", Toast.LENGTH_SHORT); toast.show();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
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
        String buf2 = str;
        int j = buf2.indexOf("//");
        buf2.replaceAll("\\s+","");
        //for(int i=0;i<(str.length());i++){
        if(buf2.length()>0){
            Character char1 = buf2.charAt(0);
            if((char1 == char3)|(j!=-1)){
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
       switch (modeCallback){
           case 1:
           {
               //MainActivity.print("serversetting!  "+data);
               //read
               try {

                   JsonSettings json = new JsonSettings(data);
                   editor.putString("orthancName", json.orthancName);
                   editor.putString("StorageDirectory", json.storageDirectory);
                   editor.putBoolean("StorageCompression", json.StorageCompression);
                   editor.putString("MaximumStorageSize",String.valueOf(json.MaximumStorageSize));
                   editor.putString("MaximumPatientCount",String.valueOf(json.MaximumPatientCount));
                   editor.putString("ConcurrentJobs",String.valueOf(json.ConcurrentJobs));
                   editor.putBoolean("HttpServerEnabled", json.HttpServerEnabled);
                   editor.putString("HttpPort",String.valueOf(json.HttpPort));
                   editor.putBoolean("HttpDescribeErrors", json.HttpDescribeErrors);
                   editor.putBoolean("HttpCompressionEnabled", json.HttpCompressionEnabled);
                   editor.putBoolean("DicomServerEnabled", json.DicomServerEnabled);
                   editor.putString("DicomAet", json.DicomAet);
                   editor.putBoolean("DicomCheckCalledAet", json.DicomCheckCalledAet);
                   editor.putString("DicomPort",String.valueOf(json.DicomPort));
                   editor.putString("DefaultEncoding", json.DefaultEncoding);
                   JsonObject transferSyntax = new JsonObject();
                   transferSyntax.addProperty("DeflatedTransfer",json.DeflatedTransferSyntaxAccepted);
                   transferSyntax.addProperty("JpegTransfer",json.JpegTransferSyntaxAccepted);
                   transferSyntax.addProperty("Jpeg2000Transfer",json.Jpeg2000TransferSyntaxAccepted);
                   transferSyntax.addProperty("JpegLosslessTransfer",json.JpegLosslessTransferSyntaxAccepted);
                   transferSyntax.addProperty("JpipTransfer",json.JpipTransferSyntaxAccepted);
                   transferSyntax.addProperty("Mpeg2Transfer",json.Mpeg2TransferSyntaxAccepted);
                   transferSyntax.addProperty("RleTransfer",json.RleTransferSyntaxAccepted);
                   editor.putString("TransferSyntax", transferSyntax.toString());
                   editor.putBoolean("UnknownSopClassAccepted", json.UnknownSopClassAccepted);
                   editor.putString("DicomScpTimeout",String.valueOf(json.DicomScpTimeout));
                   editor.putBoolean("RemoteAccessAllowed", json.RemoteAccessAllowed);
                   editor.putBoolean("SslEnabled", json.SslEnabled);
                   editor.putString("SslCertificate", json.SslCertificate);
                   editor.putBoolean("AuthenticationEnabled", json.AuthenticationEnabled);
                   editor.putString("HttpUserJson",data);
                   editor.putBoolean("DicomModalitiesInDatabase", json.dicomModalitiesInDb);
                   editor.putBoolean("DicomAlwaysAllowEcho", json.dicomAlwaysAllowEcho);
                   editor.putBoolean("DicomAlwaysAllowStore", json.DicomAlwaysStore);
                   editor.putBoolean("DicomCheckModalityHost", json.CheckModalityHost);
                   editor.putString("DicomScuTimeout",String.valueOf(json.DicomScuTimeout));
                   editor.putBoolean("OrthancPeersInDatabase", json.orthancPeerInDb);
                   editor.putString("HttpProxy", json.HttpProxy);
                   editor.putBoolean("HttpVerbose", json.httpVerbose);
                   editor.putString("HttpTimeout",String.valueOf(json.HttpTimeout));
                   editor.putBoolean("HttpsVerifyPeers", json.HttpsVerifyPeers);
                   editor.putString("HttpsCACertificates", json.HttpsCACertificates);
                   //!!!
                   editor.putString("UserMetadata", json.userMetadata.toString());
                   ///!!!!
                   editor.putString("UserContentType", json.contentType.toString());
                   editor.putString("StableAge",String.valueOf(json.StableAge));
                   editor.putBoolean("StrictAetComparison", json.StrictAetComparison);
                   editor.putBoolean("StoreMD5ForAttachments", json.StoreMD5ForAttachments);
                   editor.putString("LimitFindResults",String.valueOf(json.LimitFindResults));
                   editor.putString("LimitFindInstances",String.valueOf(json.LimitFindInstances));
                   editor.putString("LimitJobs",String.valueOf(json.LimitJobs));
                   editor.putBoolean("LogExportedResources", json.LogExportedResources);
                   editor.putBoolean("KeepAlive", json.KeepAlive);
                   editor.putBoolean("StoreDicom", json.StoreDicom);
                   editor.putString("DicomAssociationCloseDelay",String.valueOf(json.DicomAssociationCloseDelay));
                   editor.putString("QueryRetrieveSize",String.valueOf(json.QueryRetrieveSize));
                   editor.putBoolean("CaseSensitivePN", json.CaseSensitivePN);
                   editor.putBoolean("AllowFindSopClassesInStudy", json.AllowFindSopClassesInStudy);
                   editor.putBoolean("LoadPrivateDictionary", json.LoadPrivateDictionary);
                   //!!
                   editor.putString("Dictionary", json.dictionary.toString());
                   editor.putBoolean("SynchronousCMove", json.SynchronousCMove);
                   editor.putString("JobsHistorySize",String.valueOf(json.JobsHistorySize));
                   editor.putBoolean("OverwriteInstances", json.overwriteInstances);
                   editor.putString("MediaArchiveSize",String.valueOf(json.mediaArchiveSize));
                   editor.commit();

                   getFragmentManager()
                           .beginTransaction()
                           .add(R.id.fragpref, new SettingsFragment())
                           .commit();

               }catch (Exception e){
                   MainActivity.print("SharedPreferences  "+e.toString());
               }
           }
               break;
           case 2:
           {
               //write
           }
           break;
           case 3:
           {
               //reset
           }
           break;
           default:
               break;
       }
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);
        }
    }

    public void writeJson(JsonObject json){//, File fichier) {
        //use Gson for pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(json);
        //write the json in the destination file
        //Orthanc_Tools.writeCSV(jsonString, fichier);
    }

}
