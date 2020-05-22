package com.example.orthancmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.datastorage.Patient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;


public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder>{

    private Context context;
    private ArrayList<Serie> series = new ArrayList<Serie>();
    private JsonParser parser=new JsonParser();

    public SerieAdapter(ArrayList<Serie> series, Context context) {
        this.series = series;
        this.context = context;
    }

    @Override
    public SerieAdapter.SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.serie_adapter, parent, false);
        SerieAdapter.SerieViewHolder serieViewHolder = new SerieAdapter.SerieViewHolder(v);
        return serieViewHolder;
    }

    SerieAdapter(Context context){
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
                    SeachFragment.editor.putString("SerieOrthancID", bufSerie.getId().toString());
                    SeachFragment.editor.commit();
                    MainActivity.print(bufSerie.getId());

                    ////
                    ImagePlus ip=readSerie(bufSerie.getId());
                }
            });
        }catch (Exception e){
            //MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    public ImagePlus readSerie(String uuid) {
//        StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+uuid);
//        JsonObject seriesDetails=parser.parse(sb.toString()).getAsJsonObject();
//        JsonArray instanceIDList=seriesDetails.get("Instances").getAsJsonArray();
//
//        boolean screenCapture=false;
//        int nbFrameInInstance = 0;
//        ImageStack stack = null;
//
//        for(int i=0 ; i<instanceIDList.size(); i++) {
//
//            String instanceID= instanceIDList.get(i).getAsString();
//            //String metadata = this.extractDicomInfo(instanceID);
//            //end;
//
//            if(i==0) {
//                StringBuilder sop=connexion.makeGetConnectionAndStringBuilder("/instances/"+instanceID+"/metadata/SopClassUid");
//                nbFrameInInstance=getFrameNumber(instanceIDList.get(i).getAsString());
//                //If it is a screen capture change the boolean
//                if(sop.toString().startsWith("1.2.840.10008.5.1.4.1.1.7")) screenCapture=true;
//                if(sop.toString().equals("1.2.840.10008.5.1.4.1.1.6.1")) screenCapture=true;
//            }
//
//            if(nbFrameInInstance==1) {
//                ImageProcessor ip=readInstance(instanceID, screenCapture);
//                if(i==0) {
//                    stack= new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
//                }
//
//                stack.addSlice(metadata, ip);
//
//                IJ.showStatus("Reading");
//                IJ.showProgress((double) (i+1)/instanceIDList.size());
//            } else {
//                ImagePlus imp=readMultiFrameImage(instanceIDList.get(i).getAsString(), nbFrameInInstance, metadata, screenCapture);
//                return imp;
//            }
//
//
//            //end
//
//        }

        ImagePlus imp = new ImagePlus();//=generateFinalImagePlus(stack);
        return imp;
    }

    @Override
    public int getItemCount() {
        return series.size();
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

