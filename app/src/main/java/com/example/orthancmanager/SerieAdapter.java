package com.example.orthancmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.datastorage.OrthancServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;


public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder> implements  ConnectionCallback{

    private Context context;
    private ArrayList<Serie> series = new ArrayList<Serie>();
    private JsonParser parser=new JsonParser();
    private HttpURLConnection urlCon;

    public SerieAdapter(ArrayList<Serie> series, Context context) {
        this.series = series;
       // this.context = context;
    }

    @Override
    public SerieAdapter.SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.serie_adapter, parent, false);
        this.context= parent.getContext();
        SerieAdapter.SerieViewHolder serieViewHolder = new SerieAdapter.SerieViewHolder(v);
        return serieViewHolder;
    }

    public SerieAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SerieViewHolder holder, final int position) {
        try {
            final Serie serie = series.get(position);
            holder.serieDescription.setText(serie.getSerieDescription().toString());
            holder.Instances.setText(String.valueOf(serie.getNbInstances()));
            holder.serieNumber.setText(serie.getSeriesNumber().toString());
            holder.serieLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Serie bufSerie = series.get(position);
                    //SeachFragment.editor.putString("SerieOrthancID", bufSerie.getId().toString());
                    //SeachFragment.editor.commit();
                    //MainActivity.print(bufSerie.getId());
                    //Intent intent = new Intent(context,DicomViewer.class);
                    //context.startActivity(intent);
                    //intent.putExtra("SerieOrthancID",servers.get(i).getId());
                    // if(servers.get(i).connect) {
                    //      context.startActivity(intent);

                    //  for(int i=0;i<bufSerie.getInstances().size();i++){
                        JsonArray bufarray = bufSerie.getInstances();
                        //MainActivity.print("instance = "+bufarray.get(0).toString().replace("\"",""));
                        String buf = bufarray.get(0).toString().replace("\"","");
                        //getOrthancData(SeachFragment.server,"/instances/",buf);
                        //   }

                    SeachFragment.editor.putString("InstanceOrthancID", buf);
                    SeachFragment.editor.putString("InstancesOrthancID", bufarray.toString());
                    SeachFragment.editor.commit();
                    //MainActivity.print(bufSerie.getId());
                    try {
                        Intent intent = new Intent(context, DicomViewer.class);
                        context.startActivity(intent);
                    }catch (Exception e){
                        MainActivity.print("error dicom= "+e.toString());
                    }
                }
            });
        }catch (Exception e){
            MainActivity.print("serieadapter = "+e.toString());
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
                   //     BufferedImage bi = ImageIO.read( connexion.openImage(uri));
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
    public int getItemCount() {
        return series.size();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(String data, OrthancServer server, String param) {
        //MainActivity.print("data = "+data);
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

    }

    public class SerieViewHolder extends RecyclerView.ViewHolder {

        TextView serieDescription;
        TextView serieNumber;
        TextView Instances;
        LinearLayout serieLayout;
        public SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            serieDescription = (TextView)itemView.findViewById(R.id.serieDescription);
            serieNumber = (TextView)itemView.findViewById(R.id.serieNumber);
            Instances = (TextView)itemView.findViewById(R.id.serieInstances);
            serieLayout = (LinearLayout)itemView.findViewById(R.id.serieLayout);
        }
    }
}

