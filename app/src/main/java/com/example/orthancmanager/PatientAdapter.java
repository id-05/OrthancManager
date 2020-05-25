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


public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder>{

    private Context context;
    private ArrayList<Patient> patients = new ArrayList<Patient>();

    public PatientAdapter(ArrayList<Patient> patients, Context context) {
        this.patients = patients;
        this.context = context;
    }

    @Override
    public PatientAdapter.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_adapter, parent, false);
        PatientAdapter.PatientViewHolder patientViewHolder = new PatientAdapter.PatientViewHolder(v);
        return patientViewHolder;
    }

    PatientAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PatientViewHolder holder, final int position) {
        try {
            final Patient patient = patients.get(position);
            holder.patientName.setText(patient.getName());
            holder.patientID.setText(patient.getPatientId());
            holder.patientLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Patient bufPatient = patients.get(position);
                    SeachFragment.editor.putString("PatientOrthancID", bufPatient.orthancID.toString());
                    SeachFragment.editor.putString("patientName", bufPatient.name);
                    SeachFragment.editor.putString("patientBirthDate", bufPatient.birthDate);
                    SeachFragment.editor.putString("patientSex", bufPatient.sex);
                    SeachFragment.editor.commit();
                    PatientsFragment.newClick = true;
                    ServerPanel.TabChange(2);
                }
            });
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {

        TextView patientID;
        TextView patientName;
        LinearLayout patientLayout;
        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientID = (TextView)itemView.findViewById(R.id.patientId);
            patientName = (TextView)itemView.findViewById(R.id.patientName);
            patientLayout = (LinearLayout)itemView.findViewById(R.id.patientLayout);
        }
    }
}