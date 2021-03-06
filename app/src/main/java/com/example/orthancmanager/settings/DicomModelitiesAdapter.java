package com.example.orthancmanager.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.MainActivity;
import com.example.orthancmanager.R;
import java.util.ArrayList;


public class DicomModelitiesAdapter extends RecyclerView.Adapter<DicomModelitiesAdapter.DicomModelitiesViewHolder>{

    private ArrayList<DicomModaliti> dicomModalities;

    DicomModelitiesAdapter(ArrayList<DicomModaliti> dicomModalities) {
        this.dicomModalities = dicomModalities;
    }

    @NonNull
    @Override
    public DicomModelitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dicom_modalities_adapter, parent, false);
        return new DicomModelitiesViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DicomModelitiesAdapter.DicomModelitiesViewHolder holder, final int position) {
        try {
            DicomModaliti node = dicomModalities.get(position);
            holder.viewTitle.setText(node.getmTitle());
            holder.viewName.setText(node.getmName());
            holder.viewIP.setText(node.getmIP());
            holder.viewPort.setText(node.getmPort());
            holder.viewProperty.setText(node.getmProperty());
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
        return dicomModalities.size();
    }

    static class DicomModelitiesViewHolder extends RecyclerView.ViewHolder {

        TextView viewTitle;
        TextView viewName;
        TextView viewIP;
        TextView viewPort;
        TextView viewProperty;
        ImageView imageDel;

        DicomModelitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTitle = itemView.findViewById(R.id.nameClient);
            viewName = itemView.findViewById(R.id.nameModalities);
            viewIP = itemView.findViewById(R.id.IPclient);
            viewPort = itemView.findViewById(R.id.PORTclient);
            viewProperty = itemView.findViewById(R.id.Propertyclient);
            imageDel = itemView.findViewById(R.id.deleteModalities);
        }
    }
}