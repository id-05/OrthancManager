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
import java.util.ArrayList;


public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder>{

    private Context context;
    private ArrayList<Serie> series = new ArrayList<Serie>();

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
                    //StudyFragment.newClick = true;
                    //ServerPanel.TabChange(3);
                }
            });
        }catch (Exception e){
            //MainActivity.print("bindviewholder = "+e.toString());
        }
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

