package com.example.orthancmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AddNewServer extends AppCompatActivity implements ConnectionCallback<Integer> {
    OrthancServer newServer = new OrthancServer();
    Button checkConnect;
    String fulladdress;
    //EditText nameEdit;
    EditText ipaddressEdit;
    EditText portEdit;
    EditText loginEdit;
    EditText passwordEdit;
    EditText pathtojson;
    Spinner osspinner;
    Button cancel;
    Button save;
    //EditText portEdit;
    //EditText pathEdit;
    Boolean savePressed = false;
    LinearLayout addServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_server);
        setTitle(R.string.titleAddNewServer);

        addServer = (LinearLayout) findViewById(R.id.addServer);
        cancel = (Button)findViewById(R.id.cancel);
        save = (Button)findViewById(R.id.save);
        //nameEdit = (EditText)findViewById(R.id.nameserver);
        ipaddressEdit = (EditText)findViewById(R.id.ipaddres);
        //ipaddressEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
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
               // ipaddressEdit.setText(" . . .");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        portEdit = (EditText)findViewById(R.id.port);
        portEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        loginEdit = (EditText)findViewById(R.id.login);
        passwordEdit = (EditText)findViewById(R.id.password);
        passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pathtojson = (EditText)findViewById(R.id.pathToJson);
        osspinner = (Spinner)findViewById(R.id.osspinner);
        osspinner.setOnItemSelectedListener(osSelect);

        checkConnect = (Button)findViewById(R.id.checkConnect);
        checkConnect.setOnClickListener(checkConnectPress);
        save.setOnClickListener(pressSave);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    final AdapterView.OnItemSelectedListener osSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String[] chooseOS = getResources().getStringArray(R.array.chooseOC);
            String[] choosePath = getResources().getStringArray(R.array.pathjson);
            newServer.setOS(chooseOS[i]);
            if(i!=0){

                pathtojson.setText(choosePath[i-1], TextView.BufferType.EDITABLE);
                newServer.setPathToJson(choosePath[i-1]);
            }else {
                pathtojson.setText("");
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    final View.OnClickListener pressSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if((!ipaddressEdit.getText().toString().equals(""))&
                    (!portEdit.getText().toString().equals(""))){
                //newServer.setName("***");
                newServer.setIpaddress(ipaddressEdit.getText().toString());
                newServer.setPort(portEdit.getText().toString());
                newServer.setLogin(loginEdit.getText().toString());
                newServer.setPassword(passwordEdit.getText().toString());
                newServer.setOS(osspinner.toString());
                newServer.setPathToJson(pathtojson.getText().toString());
                //Connection newConn = new Connection(newServer,AddNewServer.this);
                savePressed = true;
                MainActivity.serverAddBase(newServer);
                //doSomethingAsyncOperaion(newServer);
                finish();

            }else{
                if(ipaddressEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failureIP, Toast.LENGTH_SHORT); toast.show();
                }
                if(portEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failurePORT, Toast.LENGTH_SHORT); toast.show();
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
            //Connection newConn = new Connection(newServer,AddNewServer.this);
            //newConn.UniRequest(AddNewServer.this/*,"GET","/system"*/);
            //GetInfoRequest newConnection = new GetInfoRequest();
            //newConnection.execute();
            //MainActivity.print("pressed check");
            doSomethingAsyncOperaion(newServer,"/system");
        }
    };

    @Override
    public void onBegin() {
        //MainActivity.print("begin");
    }

    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
        MainActivity.print("Success "+data);
        if(data!=null) {
            if (savePressed) {
                //обработка json
                //MainActivity.print(data);
                JsonParser parser = new JsonParser();
                JsonObject orthancJson = parser.parse(data).getAsJsonObject();
                newServer.setName(orthancJson.get("Name").getAsString());
                newServer.setDicomaet(orthancJson.get("DicomAet").getAsString());
                //MainActivity.print(newServer.dicomaet);
              //  MainActivity.serverAddBase(newServer);
              //  finish();
            } else {

                Snackbar.make(addServer
                        ,
                        R.string.sucsess,
                        Snackbar.LENGTH_LONG
                ).show();
                //Toast toast = Toast.makeText(this, R.string.sucsess, Toast.LENGTH_SHORT);
                //toast.show();
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(savePressed){
          //  MainActivity.serverAddBase(newServer);
         //   finish();
        }
        else {
            Snackbar.make(addServer
                    ,
                    R.string.connectionerror,
                    Snackbar.LENGTH_LONG
            ).show();
            //Toast toast = Toast.makeText(this, R.string.connectionerror, Toast.LENGTH_SHORT);
            //toast.show();
        }
    }

    @Override
    public void onEnd() {
        //MainActivity.print("End");
    }

    private void doSomethingAsyncOperaion(OrthancServer server, String param) {
        new AbstractAsyncWorker<String>(this,server, param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() throws Exception {

                String result = null;
                String auth =new String(newServer.login + ":" + newServer.password);
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    fulladdress = "http://"+newServer.ipaddress+":"+newServer.port;
                    URL url = new URL(fulladdress+"/system");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    //connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    //connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        boolean resultConnect = true;
                        MainActivity.print(String.valueOf(resultConnect));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        result = sb.toString();
                        MainActivity.print(result);
                    }else {
                        //resultConnect = false;
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
