package com.example.orthancmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SeachFragment extends Fragment implements ConnectionCallback{

    Button seachBut;
    Button butFromDate;
    Button butToDate;
    Calendar calendarFromDate  = Calendar.getInstance();
    Calendar calendarToDate = Calendar.getInstance();
    int id;
    OrthancServer server = new OrthancServer();
    private JsonParser parserJson = new JsonParser();
    private SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ImageView statusImage;
    AnimationDrawable mAnimation = new AnimationDrawable();
    CheckBox cr, ct, mr , nm , pt , us, xa , mg, dx;
    TextView customModalities;
    Spinner seachSpinner;
    ArrayList<String> selectorList = new ArrayList<String>();
    String seachSelector = null;
    EditText editIdName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_seach, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();
        editor.putString("SeachResult","");
        editor.commit();


        if(getArguments() != null){
            id = getArguments().getInt("serverid");
        }
        server = MainActivity.getServerById(id);

        calendarFromDate.set(Calendar.YEAR, 2015);
        calendarFromDate.set(Calendar.MONTH, 1);
        calendarFromDate.set(Calendar.DAY_OF_MONTH, 1);

        cr = (CheckBox)fragmentView.findViewById(R.id.cbCR);
        ct = (CheckBox)fragmentView.findViewById(R.id.cbCT);
        mr = (CheckBox)fragmentView.findViewById(R.id.cbMR);
        nm = (CheckBox)fragmentView.findViewById(R.id.cbNM);
        pt = (CheckBox)fragmentView.findViewById(R.id.cbPT);
        us = (CheckBox)fragmentView.findViewById(R.id.cbUS);
        xa = (CheckBox)fragmentView.findViewById(R.id.cbXA);
        mg = (CheckBox)fragmentView.findViewById(R.id.cbMG);
        dx = (CheckBox)fragmentView.findViewById(R.id.cbDX);
        customModalities = (TextView)fragmentView.findViewById(R.id.customMod);
        seachSpinner = (Spinner)fragmentView.findViewById(R.id.spinnerSeach);
        ArrayAdapter<String> spinnerSeachAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.seach));
        seachSpinner.setAdapter(spinnerSeachAdapter);
        seachSpinner.setOnItemSelectedListener(seachSpinnerListener);
        selectorList.add("PatientID");
        selectorList.add("");//НЕ ЗАКОНЧИЛ ПОТОМУ ЧТО ОКАЗАЛОСЬ НЕТ ПОИСКА ПО ИМЕНИ
        editIdName = (EditText)fragmentView.findViewById(R.id.editIdName);
        seachBut = (Button)fragmentView.findViewById(R.id.butSeach);
        seachBut.setOnClickListener(seachClick);

        butFromDate = (Button)fragmentView.findViewById(R.id.dateFrom);
        butToDate = (Button)fragmentView.findViewById(R.id.dateTo);
        butFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), datefrom,
                        calendarFromDate.get(Calendar.YEAR),
                        calendarFromDate.get(Calendar.MONTH),
                        calendarFromDate.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        butToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateto,
                        calendarFromDate.get(Calendar.YEAR),
                        calendarFromDate.get(Calendar.MONTH),
                        calendarFromDate.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        butFromDate.setText(DateUtils.formatDateTime(getActivity(),
                calendarFromDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        butToDate.setText(DateUtils.formatDateTime(getActivity(),
                calendarToDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        statusImage = (ImageView)fragmentView.findViewById(R.id.imgAnimation);
        statusImage.setVisibility(View.INVISIBLE);

        return fragmentView;
    }

    final AdapterView.OnItemSelectedListener seachSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            seachSelector = selectorList.get(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    DatePickerDialog.OnDateSetListener datefrom = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarFromDate.set(Calendar.YEAR, year);
            calendarFromDate.set(Calendar.MONTH, monthOfYear);
            calendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            butFromDate.setText(DateUtils.formatDateTime(getActivity(),
                    calendarFromDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }
    };

    DatePickerDialog.OnDateSetListener dateto = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarToDate.set(Calendar.YEAR, year);
            calendarToDate.set(Calendar.MONTH, monthOfYear);
            calendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            butToDate.setText(DateUtils.formatDateTime(getActivity(),
                    calendarToDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }
    };

    View.OnClickListener seachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            JsonObject query=new JsonObject();
            query.addProperty("Level", "Studies");
            query.addProperty("CaseSensitive", false);
            query.addProperty("Expand", true);
            query.addProperty("Limit", 0);
            JsonObject queryDetails=new JsonObject();
            String date = format.format(calendarFromDate.getTime())+"-"+format.format(calendarToDate.getTime());
            //MainActivity.print("date= "+date);
            queryDetails.addProperty("StudyDate", date);
            //queryDetails.addProperty("StudyDescription", studyDesc);
            //seachSpinner.getAdapter()
            queryDetails.addProperty("PatientID", editIdName.getText().toString());
            StringBuilder modalities=new StringBuilder();
            if (cr.isChecked()) modalities.append("CR\\");
            if (ct.isChecked()) modalities.append("CT\\");
            if (mr.isChecked()) modalities.append("MR\\");
            if (nm.isChecked()) modalities.append("NM\\");
            if (pt.isChecked()) modalities.append("PT\\");
            if (us.isChecked()) modalities.append("US\\");
            if (xa.isChecked()) modalities.append("XA\\");
            if (mg.isChecked()) modalities.append("MG\\");
            if (dx.isChecked()) modalities.append("DX\\");
            modalities.append(customModalities.getText());

            String modality =  modalities.toString();
            queryDetails.addProperty("Modality", modality);//"MR");
            query.add("Query", queryDetails);
            MainActivity.print("query = "+query.toString());
            getOrthancData(server,"/tools/find", query.toString());
            try {
                statusImage.setVisibility(View.VISIBLE);
                mAnimation = (AnimationDrawable) statusImage.getDrawable();
                mAnimation.start();
            }catch (Exception e){
                MainActivity.print(e.toString());
            }
        }
    };

    private void getOrthancData(final OrthancServer server, final String tool, final String param) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> execute = new AbstractAsyncWorker<String>(this, server, param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {
                String urlParameters = param;
                String result = null;
                String auth = new String(server.login + ":" + server.password);
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    String fulladdress = "http://" + server.ipaddress + ":" + server.port;
                    URL url = new URL(fulladdress + tool);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Basic " + base64);
                    connection.setDoOutput(true);
                    connection.setAllowUserInteraction(false);
                    connection.setConnectTimeout(1000);
                    connection.setRequestMethod("POST");
                    connection.connect();
                    OutputStream os = connection.getOutputStream();
                    os.write(urlParameters.getBytes());
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line);
                        }
                        bufferedReader.close();
                        result = sb.toString();
                    } else {
                        MainActivity.print("Error server response =  " + responseCode);
                    }
                    os.close();
                } catch (Exception e) {
                    MainActivity.print("error get thread :" + e.toString());
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
        editor.putString("SeachResult",data);
        editor.commit();
        ServerPanel.TabChange(1);
        statusImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }
}
