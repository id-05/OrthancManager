package com.example.orthancmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServerCardAdapter extends RecyclerView.Adapter<ServerCardAdapter.ServerViewHolder> implements View.OnCreateContextMenuListener  {

    ArrayList<OrthancServer> servers;
    private Context context;
    AnimationDrawable mAnimation;

    ServerCardAdapter(ArrayList<OrthancServer> servers, Context context){
        this.servers = servers;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_card, parent, false);
        ServerViewHolder serverViewHolder = new ServerViewHolder(v);
        return serverViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ServerViewHolder serverViewHolder, final int i) {
        serverViewHolder.serverName.setText(servers.get(i).name);
        serverViewHolder.serverIP.setText(servers.get(i).ipaddress);
        serverViewHolder.countInstances.setText(String.valueOf(servers.get(i).getCountInstances()));
        serverViewHolder.countPatients.setText(String.valueOf(servers.get(i).getCountPatients()));
        serverViewHolder.countSeries.setText(String.valueOf(servers.get(i).getCountSeries()));
        serverViewHolder.countStudies.setText(String.valueOf(servers.get(i).getCountStudies()));
        serverViewHolder.totalDiskSizeMB.setText(String.valueOf(servers.get(i).getTotalDiskSizeMB()));
        //serverViewHolder.statusImage.setVisibility(View.VISIBLE);
        //serverViewHolder.statusImage.setBackgroundResource(R.drawable.orthancanimation);
        //mAnimation = (AnimationDrawable) serverViewHolder.statusImage.getDrawable();
        //mAnimation.start();

        //notifyDataSetChanged();
        if(servers.get(i).connect) {
            serverViewHolder.layot.setBackgroundResource(R.drawable.connect_true_fon);
            //serverViewHolder.statusImage.setVisibility(View.INVISIBLE);
        }else{
            serverViewHolder.layot.setBackgroundResource(R.drawable.connect_false_fon);
            //serverViewHolder.statusImage.setVisibility(View.INVISIBLE);
        }

        serverViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServerPanel.class);
                intent.putExtra("serverid",servers.get(i).getId());
                try {
                    if(servers.get(i).connect) {
                        context.startActivity(intent);
                    }else{
                        Toast toast = Toast.makeText(context, R.string.connectionerror, Toast.LENGTH_SHORT); toast.show();
                    }
                }catch (Exception e){
                    MainActivity.print("errorrr = "+e);
                }
            }
        });

        serverViewHolder.menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, serverViewHolder.menuicon);
                popupMenu.inflate(R.menu.recycler_adapter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.itemSetting:
                            {
                                Intent intent = new Intent(context,ServerSettings.class);
                                intent.putExtra("serverid",servers.get(i).getId());
                                if(servers.get(i).connect) {
                                    context.startActivity(intent);
                                }else{
                                    Toast toast = Toast.makeText(context, R.string.connectionerror, Toast.LENGTH_SHORT); toast.show();
                                }
                            }
                                break;
                            case R.id.itemDelete:
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setTitle(R.string.confirmdeletion);
                                alertDialog.setMessage(R.string.areyousure);
                                //alertDialog.setIcon(R.drawable.delete);
                                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        MainActivity.serverDelBase(servers.get(i));
                                        servers.remove(i);
                                        notifyDataSetChanged();
                                    }
                                });

                                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialog.show();
                            }
                                break;
                            case R.id.itemConSettings:
                            {
                                Intent intent = new Intent(context,AddNewServer.class);
                                intent.putExtra("method","edit");
                                intent.putExtra("serverid",servers.get(i).getId());
                                context.startActivity(intent);
                            }
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    public static class ServerViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView serverName;
        TextView serverIP;
        RelativeLayout layot;
        ImageView menuicon;
        ImageView statusImage;
        TextView countInstances;
        TextView countPatients;
        TextView countSeries;
        TextView countStudies;
        TextView totalDiskSizeMB;
        AnimationDrawable mAnimation;

        public ServerViewHolder(View itemView)  {
            super(itemView);
            layot = (RelativeLayout)itemView.findViewById(R.id.cardLayot);
            layot.setBackgroundResource(R.drawable.connect_false_fon);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            serverName = (TextView)itemView.findViewById(R.id.server_name);
            serverIP = (TextView)itemView.findViewById(R.id.server_ip);
            menuicon = (ImageView)itemView.findViewById(R.id.editIcon);
            //statusImage = (ImageView) itemView.findViewById(R.id.connectStatus);
            countInstances = (TextView) itemView.findViewById(R.id.countInstances);
            countPatients = (TextView) itemView.findViewById(R.id.countPatients);
            countSeries = (TextView) itemView.findViewById(R.id.countSeries);
            countStudies = (TextView) itemView.findViewById(R.id.countStudies);
            totalDiskSizeMB = (TextView) itemView.findViewById(R.id.totalDiskSizeMB);

//            mAnimation = (AnimationDrawable) statusImage.getDrawable();
//            mAnimation.start();
        }
    }
}
