package com.example.orthancmanager;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.example.orthancmanager.date.OrthancServer;
import com.example.orthancmanager.date.Serie;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SeriesFragment extends Fragment implements ConnectionCallback {
    JsonParser parserJson = new JsonParser();
    @SuppressLint("SimpleDateFormat")
    ArrayList<Serie> series = new ArrayList<>();
    SerieAdapter adapter = new    SerieAdapter(this.getContext(), series);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View serieView = inflater.inflate(R.layout.fragment_series, container, false);
        RecyclerView recyclerView = serieView.findViewById(R.id.resyclerSerie);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        return serieView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if ((menuVisible)&(StudyFragment.newClick)) {
            String data = SeachFragment.prefs.getString("StudyOrthancID", "0");
            getOrthancData(SeachFragment.server, data);
            StudyFragment.newClick = false;
        }
    }

    private void getOrthancData(final OrthancServer server, final String param) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> execute = new AbstractAsyncWorker<String>(this, server, param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {
                String result = null;
                String auth = server.login + ":" + server.password;
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    String fulladdress = "http://"+server.ipaddress+":"+server.port;
                    URL url = new URL(fulladdress+ "/studies/" +param+"/series");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        result = sb.toString();
                    } else {
                        MainActivity.print("Error server response =  " + responseCode);
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
        JsonArray seriesA=(JsonArray) parserJson.parse(data);
        Iterator<JsonElement> seriesIterator=seriesA.iterator();
        series.clear();

        while (seriesIterator.hasNext()) {
            JsonObject serieData=(JsonObject) seriesIterator.next();
            JsonObject mainDicomTags=serieData.get("MainDicomTags").getAsJsonObject();
            String serieId=serieData.get("ID").getAsString();
            String seriesDescription="N/A";
            if(mainDicomTags.has("SeriesDescription")) { seriesDescription=mainDicomTags.get("SeriesDescription").getAsString(); }
            String seriesNumber="N/A";
            if(mainDicomTags.has("SeriesNumber")){ seriesNumber=mainDicomTags.get("SeriesNumber").getAsString(); }
            JsonArray instances = serieData.get("Instances").getAsJsonArray();
            Serie newSerie=new Serie(seriesDescription, seriesNumber, instances, instances.size(), serieId);
            series.add(newSerie);
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
