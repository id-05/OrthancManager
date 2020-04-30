package com.example.orthancmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Set;

public class HttpUserDialogAdapter extends RecyclerView.Adapter<HttpUserDialogAdapter.HttpUserDialogViewHolder>{

    Context context;
    String jsonStr;

    public HttpUserDialogAdapter() {

    }

    public HttpUserDialogAdapter(String jsonStr, Context context) {
        this.jsonStr = jsonStr;
        this.context = context;
    }

    @NonNull
    @Override
    public HttpUserDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
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
    public void onBindViewHolder(@NonNull HttpUserDialogAdapter.HttpUserDialogViewHolder holder, int position) {
        //MainActivity.print("onbind ");
        try {
            JsonParser parser = new JsonParser();
            JsonObject orthancJson = new JsonObject();
            orthancJson = parser.parse(jsonStr).getAsJsonObject();
            JsonObject buf = new JsonObject();
            buf = orthancJson.get("RegisteredUsers").getAsJsonObject();
            Set<String> keys = buf.keySet();
            Object[] jsonkeys = keys.toArray();
            String buf2 = buf.get(jsonkeys[position].toString()).getAsString();
            MainActivity.print("onbind "+jsonkeys[position].toString()+":"+buf.get(jsonkeys[position].toString()).getAsString());
            holder.viewLogin.setText(jsonkeys[position].toString());
            holder.viewPassword.setText(buf2);
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class HttpUserDialogViewHolder extends RecyclerView.ViewHolder {

        TextView viewLogin;
        TextView viewPassword;
        ImageView deleteItem;

        public HttpUserDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            viewLogin = (TextView)itemView.findViewById(R.id.textLogin);
            viewPassword = (TextView)itemView.findViewById(R.id.textPassword);
            deleteItem = (ImageView)itemView.findViewById(R.id.deleteItem);
        }
    }
}
