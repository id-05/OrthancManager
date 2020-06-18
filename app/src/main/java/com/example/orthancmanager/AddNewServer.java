package com.example.orthancmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.orthancmanager.MainActivity.dbHelper;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AddNewServer extends AppCompatActivity implements ConnectionCallback<Integer> {
    OrthancServer newServer = new OrthancServer();
    Button checkConnect;
    String fulladdress;
    EditText ipaddressEdit;
    EditText portEdit;
    EditText loginEdit;
    EditText passwordEdit;
    EditText pathtojson;
    Spinner osspinner;
    Button cancel;
    Button save;
    Boolean savePressed = false;
    LinearLayout addServer;
    String method;
    int id;
    OrthancServer server = new OrthancServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_server);
        addServer = findViewById(R.id.addServer);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        ipaddressEdit = findViewById(R.id.ipaddres);
        ipaddressEdit.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start,
                                               int end, Spanned dest, int dstart, int dend) {
                        if(source.equals("")){ // for backspace
                            return source;
                        }
                        if(source.toString().matches("[0-9.]+")){
                            return source;
                        }
                        return "";
                    }
                }
        });
        ipaddressEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               //
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        portEdit = findViewById(R.id.port);
        portEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        loginEdit = findViewById(R.id.login);
        passwordEdit = findViewById(R.id.password);
        passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pathtojson = findViewById(R.id.pathToJson);
        osspinner = findViewById(R.id.osspinner);
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.chooseOC));
        osspinner.setAdapter(spinnerCountShoesArrayAdapter);
        osspinner.setOnItemSelectedListener(osSelect);
        checkConnect = findViewById(R.id.checkConnect);
        checkConnect.setOnClickListener(checkConnectPress);
        save.setOnClickListener(pressSave);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            method = arguments.getString("method");
            ipaddressEdit.setText("");
            portEdit.setText("");
            loginEdit.setText("");
            passwordEdit.setText("");
            pathtojson.setText("");
            switch (method){
                case "edit":
                {
                    id = arguments.getInt("serverid");
                    try {
                        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
                        String selection = "id = ?";
                        String[] selectionArgs = new String[]{String.valueOf(id)};
                        Cursor cursor = userDB.query("servers", null, selection, selectionArgs, null, null, null);
                        if (cursor.moveToFirst()) {
                            server.setId(id);
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
                        ipaddressEdit.setText(server.ipaddress);
                        portEdit.setText(server.getPort());
                        loginEdit.setText(server.getLogin());
                        passwordEdit.setText(server.password);
                        pathtojson.setText(server.pathToJson);
                        osspinner.setSelection(((ArrayAdapter)osspinner.getAdapter()).getPosition(server.getOS()));
                        setTitle(R.string.editConServer);
                    }catch (Exception e){
                        MainActivity.print("addserver oncreate "+e.toString());
                    }
                }
                    break;
                case "new":
                {
                    setTitle(R.string.titleAddNewServer);
                    ipaddressEdit.setText("");
                    portEdit.setText("");
                    loginEdit.setText("");
                    passwordEdit.setText("");
                    pathtojson.setText("");
                }
                    break;
                default:
                    break;
            }
        }
    }

    final AdapterView.OnItemSelectedListener osSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String[] chooseOS = getResources().getStringArray(R.array.chooseOC);
            String[] choosePath = getResources().getStringArray(R.array.pathjson);
            newServer.setOS(chooseOS[i]);
            if(!method.equals("edit")) {
                if (i != 0) {
                    pathtojson.setText(choosePath[i - 1], TextView.BufferType.EDITABLE);
                    newServer.setPathToJson(choosePath[i - 1]);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    final View.OnClickListener pressSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if((!ipaddressEdit.getText().toString().equals("")) & (!portEdit.getText().toString().equals("")) & (!pathtojson.getText().toString().equals("")))
            {
                switch (method) {
                    case "new":{
                        newServer.setIpaddress(ipaddressEdit.getText().toString());
                        newServer.setPort(portEdit.getText().toString());
                        newServer.setLogin(loginEdit.getText().toString());
                        newServer.setPassword(passwordEdit.getText().toString());
                        newServer.setOS(osspinner.getSelectedItem().toString());
                        newServer.setPathToJson(pathtojson.getText().toString());
                        savePressed = true;
                        MainActivity.serverAddBase(newServer);
                        finish();
                    }
                        break;
                    case "edit":{
                        server.setIpaddress(ipaddressEdit.getText().toString());
                        server.setPort(portEdit.getText().toString());
                        server.setLogin(loginEdit.getText().toString());
                        server.setPassword(passwordEdit.getText().toString());
                        server.setOS(osspinner.getSelectedItem().toString());
                        server.setPathToJson(pathtojson.getText().toString());
                        MainActivity.serverConnectionUpdateBase(server);
                        finish();
                    }
                        break;
                    default:
                        break;
                }
            }else{
                if(ipaddressEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failureIP, Toast.LENGTH_SHORT); toast.show();
                }
                if(portEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failurePORT, Toast.LENGTH_SHORT); toast.show();
                }
                if(pathtojson.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failurepathtojson, Toast.LENGTH_SHORT); toast.show();
                }
            }
        }
    };

    final View.OnClickListener checkConnectPress = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            newServer.setIpaddress(ipaddressEdit.getText().toString());
            newServer.setPort(portEdit.getText().toString());
            newServer.setLogin(loginEdit.getText().toString());
            newServer.setPassword(passwordEdit.getText().toString());
            newServer.setOS(osspinner.toString());
            newServer.setPathToJson(pathtojson.getText().toString());
            doSomethingAsyncOperaion(newServer);
        }
    };

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
        if(data!=null) {
            if (savePressed) {
                JsonParser parser = new JsonParser();
                JsonObject orthancJson = parser.parse(data).getAsJsonObject();
                newServer.setName(orthancJson.get("Name").getAsString());
                newServer.setDicomaet(orthancJson.get("DicomAet").getAsString());
            } else {

                Snackbar.make(addServer
                        ,
                        R.string.sucsess,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(!savePressed){
            Snackbar.make(addServer
                    ,
                    R.string.connectionerror,
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onEnd() {

    }

    @SuppressLint("StaticFieldLeak")
    private void doSomethingAsyncOperaion(OrthancServer server) {
        new AbstractAsyncWorker<String>(this,server, "/system") {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {
                String result = null;
                String auth = newServer.login + ":" + newServer.password;
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    fulladdress = "http://"+newServer.ipaddress+":"+newServer.port;
                    URL url = new URL(fulladdress+"/system");
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
                    }else {
                        MainActivity.print("Error responsecode "+responseCode);
                    }
                    connection.disconnect();
                }catch (Exception e) {
                    MainActivity.print("error addserver :"+e.toString());
                }

                return result;
            }
        }.execute();
    }
}
