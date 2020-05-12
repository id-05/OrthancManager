package com.example.orthancmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class DicomModelitiesAdapter extends RecyclerView.Adapter<DicomModelitiesAdapter.DicomModelitiesViewHolder>{

    Context context;
    ArrayList<DicomModaliti> dicomModalities= new ArrayList<DicomModaliti>();


    public DicomModelitiesAdapter(ArrayList<DicomModaliti> dicomModalities, Context context) {
        this.dicomModalities = dicomModalities;
        this.context = context;
    }

    @Override
    public DicomModelitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dicom_modalities_adapter, parent, false);
        DicomModelitiesViewHolder dicomModelitiesViewHolder = new DicomModelitiesViewHolder(v);
        return dicomModelitiesViewHolder;
    }

    DicomModelitiesAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DicomModelitiesAdapter.DicomModelitiesViewHolder holder, final int position) {
        try {
            DicomModaliti node = dicomModalities.get(position);
            holder.viewTitle.setText(node.mTitle.toString());
            holder.viewName.setText(node.mName.toString());
            holder.viewIP.setText(node.mIP.toString());
            holder.viewPort.setText(node.mPort.toString());
            holder.viewProperty.setText(node.mProperty.toString());
            holder.imageDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DicomModalitiesDialogPreference.delItem(position);
                    notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return dicomModalities.size();//jsonkeys.length;
    }

    public class DicomModelitiesViewHolder extends RecyclerView.ViewHolder {

        TextView viewTitle;
        TextView viewName;
        TextView viewIP;
        TextView viewPort;
        TextView viewProperty;
        ImageView imageDel;

        public DicomModelitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTitle = (TextView)itemView.findViewById(R.id.nameClient);
            viewName = (TextView)itemView.findViewById(R.id.nameModalities);
            viewIP = (TextView)itemView.findViewById(R.id.IPclient);
            viewPort = (TextView)itemView.findViewById(R.id.PORTclient);
            viewProperty = (TextView)itemView.findViewById(R.id.Propertyclient);
            imageDel = (ImageView)itemView.findViewById(R.id.deleteModalities);
        }
    }
}