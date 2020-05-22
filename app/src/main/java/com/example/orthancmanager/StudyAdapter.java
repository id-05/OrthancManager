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


public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.StudyViewHolder>{

    private Context context;
    private ArrayList<Study> studys = new ArrayList<Study>();

    public StudyAdapter(ArrayList<Study> studys, Context context) {
        this.studys = studys;
        this.context = context;
    }

    @Override
    public StudyAdapter.StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_adapter, parent, false);
        StudyAdapter.StudyViewHolder studyViewHolder = new StudyAdapter.StudyViewHolder(v);
        return studyViewHolder;
    }

    StudyAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final StudyViewHolder holder, final int position) {
        try {
            final Study study = studys.get(position);
            holder.studyDate.setText(study.getDate().toString());
            holder.studyDescription.setText(study.getStudyDescription());
            holder.accessionNumber.setText(study.getAccession());
            holder.studyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Study bufStudy = studys.get(position);
                    SeachFragment.editor.putString("StudyOrthancID", bufStudy.StudyOrthancId.toString());
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

    public class StudyViewHolder extends RecyclerView.ViewHolder {

        TextView studyDate;
        TextView studyDescription;
        TextView accessionNumber;
        LinearLayout studyLayout;
        public StudyViewHolder(@NonNull View itemView) {
            super(itemView);
            studyDate = (TextView)itemView.findViewById(R.id.studyDate);
            studyDescription = (TextView)itemView.findViewById(R.id.studyDescription);
            accessionNumber = (TextView)itemView.findViewById(R.id.accessionNumber);
            studyLayout = (LinearLayout)itemView.findViewById(R.id.studyLayout);
        }
    }
}