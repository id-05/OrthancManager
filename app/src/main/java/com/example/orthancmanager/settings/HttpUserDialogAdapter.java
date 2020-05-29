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

public class HttpUserDialogAdapter extends RecyclerView.Adapter<HttpUserDialogAdapter.HttpUserDialogViewHolder>{

    private ArrayList<String> bufLogin;
    private ArrayList<String> bufPassword;


    HttpUserDialogAdapter(ArrayList<String> bufLogin, ArrayList<String> bufPassword) {
        this.bufLogin = bufLogin;
        this.bufPassword = bufPassword;
    }

    @NonNull
    @Override
    public HttpUserDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.http_users_adapter, parent, false);
        return new HttpUserDialogViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HttpUserDialogAdapter.HttpUserDialogViewHolder holder, final int position) {
        try {
            holder.viewLogin.setText(bufLogin.get(position));
            holder.viewPassword.setText(bufPassword.get(position));
            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpUserDialogPreference.delItem(position);
                    notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return bufLogin.size();
    }

    static class HttpUserDialogViewHolder extends RecyclerView.ViewHolder {

        TextView viewLogin;
        TextView viewPassword;
        ImageView deleteItem;

        HttpUserDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            viewLogin = itemView.findViewById(R.id.textLogin);
            viewPassword = itemView.findViewById(R.id.textPassword);
            deleteItem = itemView.findViewById(R.id.deleteItem);
        }
    }
}
