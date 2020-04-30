package com.example.orthancmanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.net.HttpURLConnection;
import java.net.URL;
//import javax.net.ssl.HttpsURLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Connection  {

    Context context;
    private OrthancServer server;
    public Boolean resultConnect;

    public static String authentication="";
    static String fulladdress;

    public Connection(OrthancServer server, Context context){
        this.context = context;
        this.server = server;
        this.fulladdress = "http://"+server.ipaddress+":"+server.port;
    }

    public void Connected() {
        GetInfoRequest newConnection = new GetInfoRequest();
        newConnection.execute();
    }

    public void GetSystemInfo() {
        GetSystemInfofmation newConnection = new GetSystemInfofmation();
        newConnection.execute("");
    }

    public void Send(){
        sendHTTP testsend = new sendHTTP();
        testsend.execute();
    }

    public void UniRequest(ConnectionCallback callback/*, String... params*/){
        UniAsyncTask uat = new UniAsyncTask(callback);
        uat.execute(/*params*/);
    }



    public class GetInfoRequest extends AsyncTask<Void, Void, Void> {

        public Boolean resultConnect = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Void doInBackground(Void... params) {
            String auth =new String(server.login + ":" + server.password);
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = android.util.Base64.encodeToString(data1, android.util.Base64.NO_WRAP);
            try {
                URL url = new URL(fulladdress);
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
                resultConnect = true;
                MainActivity.print(resultConnect.toString());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                //bufferedReader.close();
                String result = sb.toString();
               // MainActivity.print(result);
            }else {
                //MainActivity.print("false : "+responseCode);
                resultConnect = false;
            }
            connection.disconnect();
        }catch (Exception e) {
                MainActivity.print("error connect :"+e.toString());
        }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //MainActivity.print(resultConnect.toString());
            Toast toast = Toast.makeText(context, "Соединение установлено!", Toast.LENGTH_SHORT); toast.show();
        }
    }

    public class GetSystemInfofmation extends AsyncTask<String, Void, Void> {

        public Boolean resultConnect = false;
        String resultStr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Void doInBackground(String... params) {
            String auth =new String(server.login + ":" + server.password);
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = android.util.Base64.encodeToString(data1, android.util.Base64.NO_WRAP);
            try {
                String par = params[0];
                URL url = new URL(fulladdress+par);
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
                    resultConnect = true;
                    MainActivity.print(resultConnect.toString());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    resultStr = sb.toString();
                }else {
                    resultConnect = false;
                }
                //connection.disconnect();
            }catch (Exception e) {
                MainActivity.print("error connect :"+e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            MainActivity.print(resultStr);
            Toast toast = Toast.makeText(context, " Ответ получен!", Toast.LENGTH_SHORT); toast.show();
        }
    }

    public class sendHTTP extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... params) {
            //Log.e("myLogs","urlParameters= "+urlParameters);
            String urlParameters = "f = io.open(\"/\\etc\\/orthanc\\/orthanc.json\",\"r+\");" +
                    "print(f:read(\"*a\"))"+
                    "f:close()";
            MainActivity.print("urlParameters =  s"+urlParameters);

            String auth =new String(server.login + ":" + server.password);
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = android.util.Base64.encodeToString(data1, android.util.Base64.NO_WRAP);

            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //    connection.setDoInput(true);
                //connection.setRequestProperty("Content-Type", "application/json");
            //    connection.setRequestProperty("Authorization", "Basic "+base64);


            try {
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
                // Write data
                OutputStream os = connection.getOutputStream();
                os.write(urlParameters.getBytes());

                int responseCode=connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //Read
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    String result = sb.toString();
                    MainActivity.print( "answer =  "+result);

                }else {
                    //    return new String("false : "+responseCode);
                    MainActivity.print( "eRROR1 =  "+responseCode);
                    // new String("false : "+responseCode);
                }
                os.close();
            }
            catch (Exception e) {
                MainActivity.print( "eRROR 2 =  "+e.toString());
                //Log.e("myLogs","url= "+currentURL);
                //Log.e("myLogs","ERROR = "+e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public class UniAsyncTask<V> extends AsyncTask<Void, Void, Void> {
        private ConnectionCallback<V> callback;
        private Throwable t;
        private String resultString;

        protected UniAsyncTask(ConnectionCallback callback){
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Void doInBackground(Void... params) {
            String auth =new String(server.login + ":" + server.password);
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = android.util.Base64.encodeToString(data1, android.util.Base64.NO_WRAP);
            try {
                URL url = new URL(fulladdress);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestProperty("Authorization", "Basic "+base64);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.connect();
                int responseCode=connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    resultConnect = true;
                    MainActivity.print(resultConnect.toString());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    String result = sb.toString();
                    resultString = result;
                    MainActivity.print("resultString :"+resultString);
                }else {
                    resultConnect = false;
                }
                connection.disconnect();
            }catch (Exception e) {
                t = e;
                MainActivity.print("error connect :"+e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            //v = resultString;
            super.onPostExecute(result);
            if (callback != null) {
                callback.onEnd(); //Сообщаем об окончании
            }
            MainActivity.print(resultString);
            //generateCallback(resultString);
        }

        private void generateCallback(String data) { //Генерируем ответ
            if (callback == null) return;
            if (data != null) { //Есть данные - всё хорошо
                callback.onSuccess(data, server, null);
            } else if (t != null) {
                callback.onFailure(t); //Есть ошибка - вызываем onFailure
            } else { //А такая ситуация вообще не должна появляться)
                callback.onFailure(new NullPointerException("Result is empty but error empty too"));
            }
        }
    }

    private static Boolean truestring(String str){
        String buf = "/*";
        Boolean troubleSimbol = true;
        Character char2 = buf.charAt(0);
        Character char3 = buf.charAt(1);
        for(int i=0;i<(str.length());i++){
            Character char1 = str.charAt(i);
            if((char1 == char2)|(char1 == char3)){
                troubleSimbol = false;
            }
        }
        return troubleSimbol;
    }

}

/*
HttpsURLConnection conn=null;
            URL url = null;
            try {
                url = new URL(fulladdress);
                MainActivity.print("test0");
                conn = (HttpsURLConnection) url.openConnection();
                MainActivity.print("test1");
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + authentication);
                conn.connect();
                MainActivity.print("test2");
                //String os = conn.getResponseMessage();

                //MainActivity.print(os);
                int responseCode=conn.getResponseCode();
                MainActivity.print("test4");
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    String result = sb.toString();
                    MainActivity.print("answer =  "+result);
                }else {
                    Log.e("myLogs", "eRROR =  "+responseCode);
                }
                //os.close();

            } catch (Exception e) {
                MainActivity.print("error = "+e.toString());
            }
            return null;
 */
