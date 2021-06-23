package com.example.orthancmanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.date.Serie;
import com.google.gson.JsonArray;
import java.util.ArrayList;

public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder> {

    Context context;
    ArrayList<Serie> series;

    @NonNull
    @Override
    public SerieAdapter.SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.serie_adapter, parent, false);
        this.context= parent.getContext();
        return new SerieViewHolder(v);
    }

    SerieAdapter(Context context, ArrayList<Serie> series){
        this.context = context;
        this.series = series;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SerieViewHolder holder, final int position) {
        try {
            final Serie serie = series.get(position);
            holder.serieDescription.setText(serie.getSerieDescription());
            holder.Instances.setText(String.valueOf(serie.getNbInstances()));
            holder.serieNumber.setText(serie.getSeriesNumber());
            holder.serieLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Serie bufSerie = series.get(position);
                    JsonArray bufarray = bufSerie.getInstances();
                    String buf = bufarray.get(0).toString().replace("\"","");
                    SeachFragment.editor.putString("InstanceOrthancID", buf);
                    SeachFragment.editor.putString("InstancesOrthancID", bufarray.toString());
                    SeachFragment.editor.putString("serieDescription", bufSerie.getSerieDescription());
                    SeachFragment.editor.commit();
                    try {
                        Intent intent = new Intent(context, DicomViewer.class);
                        context.startActivity(intent);
                    }catch (Exception e){
                        MainActivity.print("error serieadapter= "+e.toString());
                    }
                }
            });
        }catch (Exception e){
            MainActivity.print("serieadapter = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    static class SerieViewHolder extends RecyclerView.ViewHolder {
        TextView serieDescription;
        TextView serieNumber;
        TextView Instances;
        LinearLayout serieLayout;
        SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            serieDescription = itemView.findViewById(R.id.serieDescription);
            serieNumber = itemView.findViewById(R.id.serieNumber);
            Instances = itemView.findViewById(R.id.serieInstances);
            serieLayout = itemView.findViewById(R.id.serieLayout);
        }
    }
}

