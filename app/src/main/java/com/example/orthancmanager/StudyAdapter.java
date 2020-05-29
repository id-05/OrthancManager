package com.example.orthancmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.datastorage.Study;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.StudyViewHolder>{

    private ArrayList<Study> studys;

    StudyAdapter(ArrayList<Study> studys) {
        this.studys = studys;
    }

    @NonNull
    @Override
    public StudyAdapter.StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_adapter, parent, false);
        return new StudyViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final StudyViewHolder holder, final int position) {
        try {
            final Study study = studys.get(position);
            Date date = study.getDate();
            String date2 = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
            holder.studyDate.setText(date2);
            holder.studyDescription.setText(study.getStudyDescription());
            holder.accessionNumber.setText(study.getAccession());
            holder.studyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Study bufStudy = studys.get(position);
                    SeachFragment.editor.putString("StudyOrthancID", bufStudy.getOrthancId());
                    SeachFragment.editor.putString("StudyDescription", bufStudy.getStudyDescription());
                    SeachFragment.editor.commit();
                    StudyFragment.newClick = true;
                    ServerPanel.TabChange(3);
                }
            });
        }catch (Exception e){
            MainActivity.print("bindviewholder = "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return studys.size();
    }

    static class StudyViewHolder extends RecyclerView.ViewHolder {

        TextView studyDate;
        TextView studyDescription;
        TextView accessionNumber;
        LinearLayout studyLayout;
        StudyViewHolder(@NonNull View itemView) {
            super(itemView);
            studyDate = itemView.findViewById(R.id.studyDate);
            studyDescription = itemView.findViewById(R.id.studyDescription);
            accessionNumber = itemView.findViewById(R.id.accessionNumber);
            studyLayout = itemView.findViewById(R.id.studyLayout);
        }
    }
}
