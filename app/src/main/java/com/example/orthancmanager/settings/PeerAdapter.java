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

public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder>{

    private ArrayList<Peer> peers;

    PeerAdapter(ArrayList<Peer> peers) {
        this.peers = peers;
    }

    @NonNull
    @Override
    public PeerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peer_adapter, parent, false);
        return new PeerViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PeerAdapter.PeerViewHolder holder, final int position) {
        try {
            Peer node = peers.get(position);
            holder.viewName.setText(node.getmName());
            holder.viewURL.setText(node.getmURL());
            holder.viewLogin.setText(node.getmLogin());
            holder.viewPassword.setText(node.getmPassword());
            holder.imageDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PeerDialogPreference.delItem(position);
                    notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    static class PeerViewHolder extends RecyclerView.ViewHolder {

        TextView viewURL;
        TextView viewLogin;
        TextView viewPassword;
        TextView viewName;
        ImageView imageDel;

        PeerViewHolder(@NonNull View itemView) {
            super(itemView);
            viewName = itemView.findViewById(R.id.peerName);
            viewURL = itemView.findViewById(R.id.peerURL);
            viewLogin = itemView.findViewById(R.id.peerLogin);
            viewPassword = itemView.findViewById(R.id.peerPassword);
            imageDel = itemView.findViewById(R.id.deletePeer);
        }
    }
}