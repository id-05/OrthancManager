package com.example.orthancmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DicomViewer extends AppCompatActivity implements ConnectionCallback {

    private ArrayList<Bitmap> images = new ArrayList<>();
    HttpURLConnection connection;
    private ImageView imageView;
    private Bitmap bitmap;
    private JsonArray instances;
    JsonParser parserJson = new JsonParser();
    public int curIndexInst = 0;
    private TextView numberView;
    private TextView patientName;
    private TextView patientSex;
    private TextView patientBirth;
    private TextView studyDescription;
    private TextView serieDescription;
    private SeekBar seekBar;
    private int currentPosition;
    private Boolean loadComplite = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"ClickableViewAccessibility", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final GestureDetector gdt = new GestureDetector(new GestureListener());
        setContentView(R.layout.activity_dicom_viewer);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        numberView = findViewById(R.id.numberView);
        patientName = findViewById(R.id.patientName);
        patientSex = findViewById(R.id.patientSex);
        patientBirth = findViewById(R.id.patientBirth);
        studyDescription = findViewById(R.id.studyDescriptionView);
        serieDescription = findViewById(R.id.serieDescription);
        imageView = findViewById(R.id.imageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
        String buf = SeachFragment.prefs.getString("InstancesOrthancID", "0");
        instances=(JsonArray) parserJson.parse(buf);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(instances.size());
        seekBar.setProgress(0);
        currentPosition = 0;
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return !loadComplite;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if((progress!=0)&(loadComplite)) {
                    imageView.setImageBitmap(images.get(progress - 1));
                    numberView.setText((progress)+"/"+instances.size());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    protected void showInstance(int i){
        imageView.setImageBitmap(images.get(i));
    }

    @Override
    protected void onResume() {
        super.onResume();
        patientName.setText(SeachFragment.prefs.getString("patientName", "0"));
        patientSex.setText(SeachFragment.prefs.getString("patientSex", "0"));
        patientBirth.setText(SeachFragment.prefs.getString("patientBirthDate", "0"));
        studyDescription.setText(SeachFragment.prefs.getString("StudyDescription", "0"));
        patientBirth.setText(SeachFragment.prefs.getString("patientBirthDate", "0"));
        serieDescription.setText(SeachFragment.prefs.getString("serieDescription", "0"));
        currentPosition = 0;
        getOrthancData(SeachFragment.server, instances.get(currentPosition).toString().replace("\"",""));
    }

    private void getOrthancData(final OrthancServer server, final String param) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> execute = new AbstractAsyncWorker<String>(this, server, param) {
            @SuppressLint("StaticFieldLeak")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doAction() {
                String result = null;
                String auth = server.login + ":" + server.password;
                byte[] data1 = auth.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
                try {
                    String fulladdress = "http://"+server.ipaddress+":"+server.port;
                    URL url = new URL(fulladdress+ "/instances/" +param+"/preview");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    connection.setRequestProperty("Accept", "image/png");
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        result = connection.toString();
                        InputStream in = null;
                        try {
                            in = connection.getInputStream();
                        } catch (IOException e) {
                            MainActivity.print(e.toString());
                        }
                        bitmap = BitmapFactory.decodeStream(in);
                    }
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
        if(!this.isFinishing()) {
            images.add(bitmap);
            currentPosition++;
            seekBar.setProgress(currentPosition);
            imageView.setImageBitmap(bitmap);
            numberView.setText((currentPosition) + "/" + instances.size());
            if(currentPosition<instances.size()) {
                getOrthancData(SeachFragment.server, instances.get(currentPosition).toString().replace("\"", ""));
            }
            if (currentPosition == instances.size()) {
                loadComplite = true; }
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(curIndexInst<(instances.size()-1)){
                    curIndexInst++;
                    showInstance(curIndexInst);
                }
                return false; // справа налево
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(curIndexInst>0){
                    curIndexInst--;
                    showInstance(curIndexInst);
                }
                return false; // слева направо
            }
            return false;
        }
    }
}
