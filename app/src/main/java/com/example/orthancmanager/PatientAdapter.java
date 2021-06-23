package com.example.orthancmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.date.Patient;
import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder>{

    ArrayList<Patient> patients;

    PatientAdapter(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    @NonNull
    @Override
    public PatientAdapter.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_adapter, parent, false);
        return new PatientViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
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
                    SeachFragment.editor.putString("PatientOrthancID", bufPatient.getPatientOrthancId());
                    SeachFragment.editor.putString("patientName", bufPatient.getName());
                    SeachFragment.editor.putString("patientBirthDate", bufPatient.getPatientBirthDate());
                    SeachFragment.editor.putString("patientSex", bufPatient.getPatientSex());
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

    static class PatientViewHolder extends RecyclerView.ViewHolder {

        TextView patientID;
        TextView patientName;
        LinearLayout patientLayout;
        PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientID = itemView.findViewById(R.id.patientId);
            patientName = itemView.findViewById(R.id.patientName);
            patientLayout = itemView.findViewById(R.id.patientLayout);
        }
    }
}