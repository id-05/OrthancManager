package com.example.orthancmanager;

import android.annotation.SuppressLint;
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
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.orthancmanager.MainActivity.dbHelper;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerSettings extends AppCompatActivity implements ConnectionCallback{

    int id;
    String json;
    OrthancServer server = new OrthancServer();
    int modeCallback;
    //1 - read
    //2 - write
    //3 - reset
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);
        setTitle("Orthanc.json");
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id = arguments.getInt("serverid");
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
            String urlParameters = "f = io.open(\""+ ModifyStr(server.pathToJson) +"orthanc.json\",\"r+\");" +
                    "print(f:read(\"*a\"))"+
                    "f:close()";
            getOrthancSettings(server,"/tools/execute-script",urlParameters);
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
                modeCallback = 3;
                getOrthancSettings(server,"/tools/reset","");
                return true;
            }

            case R.id.savesettings: {
                modeCallback = 2;
                SaveSettings();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getOrthancSettings(final OrthancServer server, final String tool, final String param) {
        new AbstractAsyncWorker<String>(this,server,param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {
                String urlParameters = param;
                String result = null;
                String auth =new String(server.login + ":" + server.password);
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    String fulladdress = "http://"+server.ipaddress+":"+server.port;
                    URL url = new URL(fulladdress+tool);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    connection.setRequestProperty("Content-Length",  String.valueOf(fulladdress+urlParameters));
                    connection.setConnectTimeout(1000);
                    connection.setRequestMethod("POST");
                    connection.connect();
                    OutputStream os = connection.getOutputStream();
                    os.write(urlParameters.getBytes());
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            if(truestring(line)){
                                int i = line.indexOf("}");
                                if(i!= -1){
                                    if ((i == (line.length()-1)&(i!=0))) {
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
                        MainActivity.print( "Error server response =  "+responseCode);
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
        Character char3 = buf.charAt(1);
        String buf2 = str;
        int j = buf2.indexOf("//");
        int k = buf2.indexOf("http");
        Boolean ifSlash = false;
        Boolean ifHTTP = false;
        Boolean check = false;
        if(j!=-1){
            ifSlash = true;
            check = true;
        }
        if(k!=-1){
            ifHTTP = true;
        }
        if(ifSlash&ifHTTP){
            if(j<k){
                check = true;
            }else{
                check = false;
            }
        }
        buf2.replaceAll("\\s+","");
        if(buf2.length()>0){
            Character char1 = buf2.charAt(0);
            if((char1 == char3)|(check)){
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
               try {
                   JsonSettings json = new JsonSettings(data);
                   editor.putString("orthancName", json.orthancName);
                   editor.putString("StorageDirectory", json.storageDirectory);
                   editor.putString("IndexDirectory", json.indexDirectory);
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
                   editor.putString("HttpUserJson",json.users.toString());
                   editor.putString("DicomModalities",json.dicomNode.toString());
                   editor.putBoolean("DicomModalitiesInDatabase", json.dicomModalitiesInDb);
                   editor.putBoolean("DicomAlwaysAllowEcho", json.dicomAlwaysAllowEcho);
                   editor.putBoolean("DicomAlwaysAllowStore", json.DicomAlwaysStore);
                   editor.putBoolean("DicomCheckModalityHost", json.CheckModalityHost);
                   editor.putString("DicomScuTimeout",String.valueOf(json.DicomScuTimeout));
                   editor.putString("OrthancPeers",json.orthancPeer.toString());
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
               modeCallback = 0;
           }
               break;
           case 2:
           {
               modeCallback = 0;
               Toast toast = Toast.makeText(ServerSettings.this, R.string.savechange, Toast.LENGTH_SHORT); toast.show();
           }
           break;
           case 3:
           {
               modeCallback = 0;
               Toast toast = Toast.makeText(ServerSettings.this, R.string.resetserver, Toast.LENGTH_SHORT); toast.show();
           }
           break;
           default:
               break;
       }
    }

    private void SaveSettings(){
        try {
            JsonObject jsonOb = new JsonObject();
            jsonOb.addProperty("Name", prefs.getString("orthancName", "none"));
            jsonOb.addProperty("StorageDirectory", prefs.getString("StorageDirectory", "none"));
            jsonOb.addProperty("IndexDirectory", prefs.getString("IndexDirectory", "none"));
            jsonOb.addProperty("StorageCompression", prefs.getBoolean("StorageCompression", false));
            jsonOb.addProperty("MaximumStorageSize", Integer.valueOf(prefs.getString("MaximumStorageSize", "0")));
            jsonOb.addProperty("MaximumPatientCount", Integer.valueOf(prefs.getString("MaximumPatientCount", "0")));
            jsonOb.addProperty("ConcurrentJobs", Integer.valueOf(prefs.getString("ConcurrentJobs", "0")));
            jsonOb.addProperty("HttpServerEnabled", prefs.getBoolean("HttpServerEnabled", false));
            jsonOb.addProperty("HttpPort", Integer.valueOf(prefs.getString("HttpPort", "0")));
            jsonOb.addProperty("HttpDescribeErrors", prefs.getBoolean("HttpDescribeErrors", false));
            jsonOb.addProperty("HttpCompressionEnabled", prefs.getBoolean("HttpCompressionEnabled", false));
            jsonOb.addProperty("DicomServerEnabled", prefs.getBoolean("DicomServerEnabled", false));
            jsonOb.addProperty("DicomAet", prefs.getString("DicomAet", "none"));
            jsonOb.addProperty("DicomCheckCalledAet", prefs.getBoolean("DicomCheckCalledAet", false));
            jsonOb.addProperty("DicomPort", Integer.valueOf(prefs.getString("DicomPort", "0")));
            jsonOb.addProperty("DefaultEncoding", prefs.getString("DefaultEncoding", "none"));
            boolean DeflatedTransferSyntaxAccepted=false;
            boolean JpegTransferSyntaxAccepted=false;
            boolean Jpeg2000TransferSyntaxAccepted=false;
            boolean JpegLosslessTransferSyntaxAccepted=false;
            boolean JpipTransferSyntaxAccepted=false;
            boolean Mpeg2TransferSyntaxAccepted=false;
            boolean RleTransferSyntaxAccepted=false;
            JsonParser parser = new JsonParser();
            JsonObject orthancJson = new JsonObject();
            orthancJson = parser.parse(prefs.getString("TransferSyntax", "none")).getAsJsonObject();
            if (orthancJson.has("DeflatedTransfer")) DeflatedTransferSyntaxAccepted=orthancJson.get("DeflatedTransfer").getAsBoolean();
            if (orthancJson.has("JpegTransfer")) JpegTransferSyntaxAccepted=orthancJson.get("JpegTransfer").getAsBoolean();
            if (orthancJson.has("Jpeg2000Transfer")) Jpeg2000TransferSyntaxAccepted=orthancJson.get("Jpeg2000Transfer").getAsBoolean();
            if (orthancJson.has("JpegLosslessTransfer")) JpegLosslessTransferSyntaxAccepted=orthancJson.get("JpegLosslessTransfer").getAsBoolean();
            if (orthancJson.has("JpipTransfer")) JpipTransferSyntaxAccepted=orthancJson.get("JpipTransfer").getAsBoolean();
            if (orthancJson.has("Mpeg2Transfer")) Mpeg2TransferSyntaxAccepted=orthancJson.get("Mpeg2Transfer").getAsBoolean();
            if (orthancJson.has("RleTransfer")) RleTransferSyntaxAccepted=orthancJson.get("RleTransfer").getAsBoolean();
            jsonOb.addProperty("DeflatedTransferSyntaxAccepted",DeflatedTransferSyntaxAccepted);
            jsonOb.addProperty("JpegTransferSyntaxAccepted",JpegTransferSyntaxAccepted);
            jsonOb.addProperty("Jpeg2000TransferSyntaxAccepted",Jpeg2000TransferSyntaxAccepted);
            jsonOb.addProperty("JpegLosslessTransferSyntaxAccepted",JpegLosslessTransferSyntaxAccepted);
            jsonOb.addProperty("JpipTransferSyntaxAccepted",JpipTransferSyntaxAccepted);
            jsonOb.addProperty("Mpeg2TransferSyntaxAccepted",Mpeg2TransferSyntaxAccepted);
            jsonOb.addProperty("RleTransferSyntaxAccepted",RleTransferSyntaxAccepted);
            jsonOb.addProperty("UnknownSopClassAccepted", prefs.getBoolean("UnknownSopClassAccepted", false));
            jsonOb.addProperty("DicomScpTimeout", Integer.valueOf(prefs.getString("DicomScpTimeout", "0")));
            jsonOb.addProperty("RemoteAccessAllowed", prefs.getBoolean("RemoteAccessAllowed", false));
            jsonOb.addProperty("SslEnabled", prefs.getBoolean("SslEnabled", false));
            jsonOb.addProperty("SslCertificate", prefs.getString("SslCertificate", "none"));
            jsonOb.addProperty("AuthenticationEnabled", prefs.getBoolean("AuthenticationEnabled", false));
            orthancJson = parser.parse(prefs.getString("HttpUserJson", "")).getAsJsonObject();
            jsonOb.add("RegisteredUsers",orthancJson);
            orthancJson = parser.parse(prefs.getString("DicomModalities", "")).getAsJsonObject();
            jsonOb.add("DicomModalities",orthancJson);
            jsonOb.addProperty("DicomModalitiesInDatabase", prefs.getBoolean("DicomModalitiesInDatabase", false));
            jsonOb.addProperty("DicomAlwaysAllowEcho", prefs.getBoolean("DicomAlwaysAllowEcho", false));
            jsonOb.addProperty("DicomAlwaysAllowStore", prefs.getBoolean("DicomAlwaysAllowStore", false));
            jsonOb.addProperty("DicomCheckModalityHost", prefs.getBoolean("DicomCheckModalityHost", false));
            jsonOb.addProperty("DicomScuTimeout", Integer.valueOf(prefs.getString("DicomScuTimeout", "0")));
            orthancJson = parser.parse(prefs.getString("OrthancPeers", "")).getAsJsonObject();
            jsonOb.add("OrthancPeers",orthancJson);
            jsonOb.addProperty("DicomAlwaysAllowEcho", prefs.getBoolean("DicomAlwaysAllowEcho", false));
            jsonOb.addProperty("OrthancPeersInDatabase", prefs.getBoolean("OrthancPeersInDatabase", false));
            jsonOb.addProperty("HttpProxy", prefs.getString("HttpProxy", ""));
            jsonOb.addProperty("HttpVerbose", prefs.getBoolean("HttpVerbose", false));
            jsonOb.addProperty("HttpTimeout", Integer.valueOf(prefs.getString("HttpTimeout", "0")));
            jsonOb.addProperty("HttpsVerifyPeers", prefs.getBoolean("HttpsVerifyPeers", false));
            jsonOb.addProperty("HttpsCACertificates", prefs.getString("HttpsCACertificates", ""));
            orthancJson = parser.parse(prefs.getString("UserMetadata", "")).getAsJsonObject();
            jsonOb.add("UserMetadata", orthancJson);
            orthancJson = parser.parse(prefs.getString("UserContentType", "")).getAsJsonObject();
            jsonOb.add("UserContentType", orthancJson);
            jsonOb.addProperty("StableAge", Integer.valueOf(prefs.getString("StableAge", "0")));
            jsonOb.addProperty("StrictAetComparison", prefs.getBoolean("StrictAetComparison", false));
            jsonOb.addProperty("StoreMD5ForAttachments", prefs.getBoolean("StoreMD5ForAttachments", false));
            jsonOb.addProperty("LimitFindResults", Integer.valueOf(prefs.getString("LimitFindResults", "0")));
            jsonOb.addProperty("LimitFindInstances", Integer.valueOf(prefs.getString("LimitFindInstances", "0")));
            jsonOb.addProperty("LimitJobs", Integer.valueOf(prefs.getString("LimitJobs", "0")));
            jsonOb.addProperty("LogExportedResources", prefs.getBoolean("LogExportedResources", false));
            jsonOb.addProperty("KeepAlive", prefs.getBoolean("KeepAlive", false));
            jsonOb.addProperty("StoreDicom", prefs.getBoolean("StoreDicom", false));
            jsonOb.addProperty("DicomAssociationCloseDelay", Integer.valueOf(prefs.getString("DicomAssociationCloseDelay", "0")));
            jsonOb.addProperty("QueryRetrieveSize", Integer.valueOf(prefs.getString("QueryRetrieveSize", "0")));
            jsonOb.addProperty("CaseSensitivePN", prefs.getBoolean("CaseSensitivePN", false));
            jsonOb.addProperty("AllowFindSopClassesInStudy", prefs.getBoolean("AllowFindSopClassesInStudy", false));
            jsonOb.addProperty("LoadPrivateDictionary", prefs.getBoolean("LoadPrivateDictionary", false));
            orthancJson = parser.parse(prefs.getString("Dictionary", "")).getAsJsonObject();
            jsonOb.add("Dictionary", orthancJson);
            jsonOb.addProperty("SynchronousCMove", prefs.getBoolean("SynchronousCMove", false));
            jsonOb.addProperty("JobsHistorySize", Integer.valueOf(prefs.getString("JobsHistorySize", "0")));
            jsonOb.addProperty("OverwriteInstances", prefs.getBoolean("OverwriteInstances", false));
            jsonOb.addProperty("MediaArchiveSize", Integer.valueOf(prefs.getString("MediaArchiveSize", "0")));
            String modifyStr = ModifyStr(jsonOb.toString());
            String urlParameters = "f = io.open(\""+ ModifyStr(server.pathToJson) +"orthanc.json\",\"w+\");" +
                    "f:write(\""+modifyStr+"\"); "+
                    "f:close()";

            getOrthancSettings(server,"/tools/execute-script",urlParameters);
        }catch (Exception e){
            MainActivity.print("Error save json ="+e.toString());
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

    public String ModifyStr(String str){
        String result = null;
        String buf0 = null;
        String buf = null;
        String buf2 = null;
        buf0 = str.replace("\\","\\\\");
        buf = buf0.replace("/","\\/");
        buf2 = buf.replace("\"","\\\"");
        result = buf2.replace(",",",\\n");
        return result;
    }
}
