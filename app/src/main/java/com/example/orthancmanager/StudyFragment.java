package com.example.orthancmanager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orthancmanager.datastorage.OrthancServer;
import com.example.orthancmanager.datastorage.Patient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StudyFragment extends Fragment implements ConnectionCallback {

    private JsonParser parserJson = new JsonParser();
    private SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
    ArrayList<Study> studys = new ArrayList<Study>();
    public static Boolean newClick = false;
    StudyAdapter adapter = new    StudyAdapter(studys,getContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View studyView = inflater.inflate(R.layout.fragment_study, container, false);
        RecyclerView recyclerView = (RecyclerView) studyView.findViewById(R.id.resyclerStudy);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        return studyView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if ((menuVisible)&(PatientsFragment.newClick)) {
            String data = SeachFragment.prefs.getString("PatientOrthancID", "0");
            //MainActivity.print("PatientOrthancID = "+data);
            getOrthancData(SeachFragment.server,"/patients/",data);
            PatientsFragment.newClick = false;
        }
    }

    private void getOrthancData(final OrthancServer server, final String tool, final String param) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> execute = new AbstractAsyncWorker<String>(this, server, param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {
                String result = null;
                String auth =new String(server.login + ":" + server.password);
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    String fulladdress = "http://"+server.ipaddress+":"+server.port;
                    URL url = new URL(fulladdress+tool+param+"/studies");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
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
                    }
                    connection.disconnect();
                }catch (Exception e) {
                    MainActivity.print("error study :"+e.toString());
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
        //MainActivity.print("Studyresult = "+data);

        JsonArray studies=(JsonArray) parserJson.parse(data);
        Iterator<JsonElement> studiesIterator=studies.iterator();
        studys.clear();

        while (studiesIterator.hasNext()) {
            JsonObject studyData=(JsonObject) studiesIterator.next();
            JsonObject mainDicomTags=studyData.get("MainDicomTags").getAsJsonObject();
            //String parentPatientID=studyData.get("ParentPatient").getAsString();
            String studyId=studyData.get("ID").getAsString();
            //JsonObject studyDetails=studyData.get("MainDicomTags").getAsJsonObject();

            String accessionNumber="N/A";
            if(mainDicomTags.has("AccessionNumber")) { accessionNumber=mainDicomTags.get("AccessionNumber").getAsString(); }
            String studyDate=null;
            Date studyDateObject=null;
            if(mainDicomTags.has("StudyDate")) { studyDate=mainDicomTags.get("StudyDate").getAsString(); }
            try {
                studyDateObject=format.parse("19000101");
                studyDateObject=format.parse(studyDate);
            } catch (Exception e) { }
            String studyDescription="N/A";
            if(mainDicomTags.has("StudyDescription")){ studyDescription=mainDicomTags.get("StudyDescription").getAsString(); }
            Study newStudy=new Study(studyDescription, studyDateObject, accessionNumber, studyId);
            //MainActivity.print(studyDescription+"   "+ studyDateObject.toString()+"   "+ accessionNumber+"    "+studyId);
            studys.add(newStudy);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }
}
