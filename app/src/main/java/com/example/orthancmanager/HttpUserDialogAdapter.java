package com.example.orthancmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Set;

public class HttpUserDialogAdapter extends RecyclerView.Adapter<HttpUserDialogAdapter.HttpUserDialogViewHolder>{

    Context context;
    String jsonStr;
    Object[] currentItems;
    Object[] jsonkeys;
    JsonObject buf;
    ArrayList<String> bufLogin = new ArrayList<String>();
    ArrayList<String> bufPassword = new ArrayList<String>();


    public HttpUserDialogAdapter(ArrayList<String> bufLogin, ArrayList<String> bufPassword, Context context) {
        this.jsonStr = jsonStr;
        this.context = context;
        this.bufLogin = bufLogin;
        this.bufPassword = bufPassword;
    }

    @NonNull
    @Override
    public HttpUserDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.http_users_adapter, parent, false);
        HttpUserDialogViewHolder httpUserDialogViewHolder = new HttpUserDialogViewHolder(v);
        return httpUserDialogViewHolder;
    }

    HttpUserDialogAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HttpUserDialogAdapter.HttpUserDialogViewHolder holder, final int position) {
        try {
            holder.viewLogin.setText(bufLogin.get(position).toString());
            holder.viewPassword.setText(bufPassword.get(position).toString());
            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpUserDialogPreference.delItem(position);
                    notifyDataSetChanged();
                    //Toast toast = Toast.makeText(context, "delete", Toast.LENGTH_SHORT); toast.show();
                }
            });
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return bufLogin.size();//jsonkeys.length;
    }

    public class HttpUserDialogViewHolder extends RecyclerView.ViewHolder {

        TextView viewLogin;
        TextView viewPassword;
        ImageView deleteItem;
        //ImageView addItem;
        //EditText addLogin;
        //EditText addPassword;

        public HttpUserDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            viewLogin = (TextView)itemView.findViewById(R.id.textLogin);
            viewPassword = (TextView)itemView.findViewById(R.id.textPassword);
            deleteItem = (ImageView)itemView.findViewById(R.id.deleteItem);
            //addItem = (ImageView)itemView.findViewById(R.id.addItem);
            //addLogin = (EditText)itemView.findViewById(R.id.addLogin);
            //addPassword = (EditText)itemView.findViewById(R.id.addPassword);
        }
    }
}
