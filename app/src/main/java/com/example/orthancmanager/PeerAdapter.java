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


public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder>{

    Context context;
    ArrayList<Peer> peers= new ArrayList<Peer>();


    public PeerAdapter(ArrayList<Peer> peers, Context context) {
        this.peers = peers;
        this.context = context;
    }

    @Override
    public PeerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peer_adapter, parent, false);
        PeerViewHolder peerViewHolder = new PeerViewHolder(v);
        return peerViewHolder;
    }

    PeerAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PeerAdapter.PeerViewHolder holder, final int position) {
        try {
            Peer node = peers.get(position);
            holder.viewURL.setText(node.mURL.toString());
            holder.viewLogin.setText(node.mLogin.toString());
            holder.viewPassword.setText(node.mPassword.toString());
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
        return peers.size();//jsonkeys.length;
    }

    public class PeerViewHolder extends RecyclerView.ViewHolder {

        TextView viewURL;
        TextView viewLogin;
        TextView viewPassword;
        ImageView imageDel;

        public PeerViewHolder(@NonNull View itemView) {
            super(itemView);
            viewURL = (TextView)itemView.findViewById(R.id.peerURL);
            viewLogin = (TextView)itemView.findViewById(R.id.peerLogin);
            viewPassword = (TextView)itemView.findViewById(R.id.peerPassword);
            imageDel = (ImageView)itemView.findViewById(R.id.deletePeer);
        }
    }
}