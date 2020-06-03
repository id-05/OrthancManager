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
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SeachFragment extends Fragment implements ConnectionCallback{

    private Button butFromDate;
    private Button butToDate;
    private Calendar calendarFromDate  = Calendar.getInstance();
    private Calendar calendarToDate = Calendar.getInstance();
    private int id;
    public static OrthancServer server = new OrthancServer();
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
    static SharedPreferences prefs;
    static SharedPreferences.Editor editor;
    private ImageView statusImage;
    private CheckBox cr, ct, mr , nm , pt , us, xa , mg, dx;
    private TextView customModalities;
    private ArrayList<String> selectorList = new ArrayList<>();
    private EditText editIdName;
    static boolean newSeach = false;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_seach, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();
        editor.putString("SeachResult","");
        editor.apply();
        if(getArguments() != null){
            id = getArguments().getInt("serverid");
        }
        server = MainActivity.getServerById(id);
        calendarFromDate.set(Calendar.YEAR, 2015);
        calendarFromDate.set(Calendar.MONTH, 1);
        calendarFromDate.set(Calendar.DAY_OF_MONTH, 1);
        cr = fragmentView.findViewById(R.id.cbCR);
        ct = fragmentView.findViewById(R.id.cbCT);
        mr = fragmentView.findViewById(R.id.cbMR);
        nm = fragmentView.findViewById(R.id.cbNM);
        pt = fragmentView.findViewById(R.id.cbPT);
        us = fragmentView.findViewById(R.id.cbUS);
        xa = fragmentView.findViewById(R.id.cbXA);
        mg = fragmentView.findViewById(R.id.cbMG);
        dx = fragmentView.findViewById(R.id.cbDX);
        customModalities = fragmentView.findViewById(R.id.customMod);
        Spinner seachSpinner = fragmentView.findViewById(R.id.spinnerSeach);
        ArrayAdapter<String> spinnerSeachAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.seachtype));
        seachSpinner.setAdapter(spinnerSeachAdapter);
        seachSpinner.setOnItemSelectedListener(seachSpinnerListener);
        selectorList.add("PatientID");
        selectorList.add("");
        editIdName = fragmentView.findViewById(R.id.editIdName);
        Button seachBut = fragmentView.findViewById(R.id.butSeach);
        seachBut.setOnClickListener(seachClick);

        butFromDate = fragmentView.findViewById(R.id.dateFrom);
        butToDate = fragmentView.findViewById(R.id.dateTo);
        butFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Objects.requireNonNull(getContext()), datefrom,
                        calendarFromDate.get(Calendar.YEAR),
                        calendarFromDate.get(Calendar.MONTH),
                        calendarFromDate.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        butToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Objects.requireNonNull(getContext()), dateto,
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

        statusImage = fragmentView.findViewById(R.id.imgAnimation);
        statusImage.setVisibility(View.INVISIBLE);
        return fragmentView;
    }

    private final AdapterView.OnItemSelectedListener seachSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private DatePickerDialog.OnDateSetListener datefrom = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarFromDate.set(Calendar.YEAR, year);
            calendarFromDate.set(Calendar.MONTH, monthOfYear);
            calendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            butFromDate.setText(DateUtils.formatDateTime(getActivity(),
                    calendarFromDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }
    };

    private DatePickerDialog.OnDateSetListener dateto = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarToDate.set(Calendar.YEAR, year);
            calendarToDate.set(Calendar.MONTH, monthOfYear);
            calendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            butToDate.setText(DateUtils.formatDateTime(getActivity(),
                    calendarToDate.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }
    };

    private View.OnClickListener seachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonObject query=new JsonObject();
            query.addProperty("Level", "Studies");
            query.addProperty("CaseSensitive", false);
            query.addProperty("Expand", true);
            query.addProperty("Limit", 0);
            JsonObject queryDetails=new JsonObject();
            String date = format.format(calendarFromDate.getTime())+"-"+format.format(calendarToDate.getTime());
            queryDetails.addProperty("StudyDate", date);
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
            queryDetails.addProperty("Modality", modality);
            query.add("Query", queryDetails);
            getOrthancData(server, query.toString());
            try {
                statusImage.setVisibility(View.VISIBLE);
                AnimationDrawable mAnimation = (AnimationDrawable) statusImage.getDrawable();
                mAnimation.start();
            }catch (Exception e){
                MainActivity.print(e.toString());
            }
        }
    };

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
                    String fulladdress = "http://" + server.ipaddress + ":" + server.port;
                    URL url = new URL(fulladdress + "/tools/find");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Basic " + base64);
                    connection.setDoOutput(true);
                    connection.setAllowUserInteraction(false);
                    connection.setConnectTimeout(1000);
                    connection.setRequestMethod("POST");
                    connection.connect();
                    OutputStream os = connection.getOutputStream();
                    os.write(param.getBytes());
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
                        String line;
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
                    statusImage.setVisibility(View.INVISIBLE);
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
        editor.putInt("currentServerId",server.id);
        //editor.putString("seachMode",seachspinner);
        //editor.putString("name",editIdName.getText().toString());
        editor.commit();
        newSeach = true;
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
