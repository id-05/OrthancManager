package com.example.orthancmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DicomViewer extends AppCompatActivity implements ConnectionCallback {

    private ArrayList<Serie> series = new ArrayList<Serie>();
    private JsonParser parser=new JsonParser();
    private HttpURLConnection urlCon;
    private  String instanceid;
    private ImageView imageView;
    private Bitmap bitmap;
    private String buf;
    private JsonArray instances;
    JsonParser parserJson = new JsonParser();
    private String curIns;
    public int curIndexInst = 0;
    private TextView numberView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final GestureDetector gdt = new GestureDetector(new GestureListener());

        setContentView(R.layout.activity_dicom_viewer);
        getSupportActionBar().hide(); // Убрать ActionBar
        numberView = (TextView)findViewById(R.id.numberView);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
        instanceid = SeachFragment.prefs.getString("InstanceOrthancID", "0");
        buf = SeachFragment.prefs.getString("InstancesOrthancID", "0");
        instances=(JsonArray) parserJson.parse(buf);
        curIns = instances.get(0).toString().replace("\"","");
    }

    protected void showInstance(int i){
        curIns = instances.get(i).toString().replace("\"","");
        numberView.setText((curIndexInst+1)+"/"+instances.size());
        getOrthancData(SeachFragment.server,"/instances/",curIns);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showInstance(0);
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
                    URL url = new URL(fulladdress+tool+param+"/preview");
                    //MainActivity.print("url = "+url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Authorization", "Basic "+base64);
                    connection.setRequestProperty("Accept", "image/png");
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        result = connection.toString();
                        urlCon = connection;
                        InputStream in = null;
                        try {
                            in = connection.getInputStream();
                        } catch (IOException e) {
                            MainActivity.print(e.toString());
                        }
                        bitmap = BitmapFactory.decodeStream(in);
                        //     BufferedImage bi = ImageIO.read( connexion.openImage(uri));
                    }
                    //connection.disconnect();
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
        //MainActivity.print("data dicom = "+data);
        imageView.setImageBitmap(bitmap);
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
                    //MainActivity.print("справа налево"+curIndexInst);
                    showInstance(curIndexInst);
                }
                return false; // справа налево
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(curIndexInst>0){
                    curIndexInst--;
                    //MainActivity.print("слева направо"+curIndexInst);
                    showInstance(curIndexInst);
                }
                return false; // слева направо
            }
            return false;
        }
    }
}
