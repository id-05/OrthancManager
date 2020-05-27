package com.example.orthancmanager.settings;

import android.content.Context;
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

    private Context context;
    private String jsonStr;
    private ArrayList<String> bufLogin = new ArrayList<String>();
    private ArrayList<String> bufPassword = new ArrayList<String>();


    HttpUserDialogAdapter(ArrayList<String> bufLogin, ArrayList<String> bufPassword, Context context) {
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
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
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
            viewLogin = (TextView)itemView.findViewById(R.id.textLogin);
            viewPassword = (TextView)itemView.findViewById(R.id.textPassword);
            deleteItem = (ImageView)itemView.findViewById(R.id.deleteItem);
        }
    }
}
