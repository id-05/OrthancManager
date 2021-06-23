package com.example.orthancmanager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.date.Patient;
import com.example.orthancmanager.date.Study;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class PatientsFragment extends Fragment {

    SharedPreferences prefs;
    JsonParser parserJson = new JsonParser();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
    ArrayList<Patient> patients = new ArrayList<>();
    PatientAdapter adapter = new PatientAdapter(patients);
    static Boolean newClick = false;
    String seachMode;
    String seachpatientName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_patients, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        RecyclerView recyclerView = fragmentView.findViewById(R.id.resyclerPatient);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        return fragmentView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if ((menuVisible)&(SeachFragment.newSeach)) {
            String data = prefs.getString("SeachResult", "*");
            seachMode = prefs.getString("seachMode", "*");
            seachpatientName = prefs.getString("name", "*");
            SeachFragment.newSeach = false;
            getPatientsFromJson(data);
        }
    }

    private void getPatientsFromJson(String data){
        HashMap<String, Patient> patientMap = new HashMap<>();
        JsonArray studies=(JsonArray) parserJson.parse(data);
        Iterator<JsonElement> studiesIterator=studies.iterator();
        patients.clear();

        while (studiesIterator.hasNext()) {
            JsonObject studyData=(JsonObject) studiesIterator.next();
            JsonObject parentPatientDetails=studyData.get("PatientMainDicomTags").getAsJsonObject();
            String parentPatientID=studyData.get("ParentPatient").getAsString();
            String studyId=studyData.get("ID").getAsString();
            JsonObject studyDetails=studyData.get("MainDicomTags").getAsJsonObject();
            String patientBirthDate="N/A";
            String patientSex="N/A";
            String patientName="N/A";
            String patientId="N/A";
            String patientDobString=null;
            Date patientDob=null;
            if(parentPatientDetails.has("PatientBirthDate")) { patientDobString=parentPatientDetails.get("PatientBirthDate").getAsString(); }

            try {
                patientDob = format.parse("19000101");
                assert patientDobString != null;
                patientDob=format.parse(patientDobString);
            } catch (Exception e) {
                MainActivity.print("Errot to transfer date");
            }

            if(parentPatientDetails.has("PatientSex")) { patientSex=parentPatientDetails.get("PatientSex").getAsString(); }
            if(parentPatientDetails.has("PatientName")) { patientName=parentPatientDetails.get("PatientName").getAsString(); }
            if(parentPatientDetails.has("PatientID")) { patientId=parentPatientDetails.get("PatientID").getAsString(); }
            String accessionNumber="N/A";
            if(studyDetails.has("AccessionNumber")) {accessionNumber=studyDetails.get("AccessionNumber").getAsString();}
            String studyInstanceUid=studyDetails.get("StudyInstanceUID").getAsString();
            String studyDate=null;
            Date studyDateObject=null;
            if(studyDetails.has("StudyDate")) { studyDate=studyDetails.get("StudyDate").getAsString(); }

            try {
                studyDateObject=format.parse("19000101");
                assert studyDate != null;
                studyDateObject=format.parse(studyDate);
            } catch (Exception e) {
                MainActivity.print("Errot to transfer date");
            }

            String studyDescription="N/A";
            if(studyDetails.has("StudyDescription")){ studyDescription=studyDetails.get("StudyDescription").getAsString(); }
            Study studyObj=new Study(studyDescription, studyDateObject, accessionNumber, studyId, patientName, patientId, patientDob, patientSex, parentPatientID, studyInstanceUid);
            SeachFragment.editor.commit();
            if(!patientMap.containsKey(parentPatientID)) {
                Patient patient=new Patient(patientName,patientId,patientBirthDate,patientSex,parentPatientID);
                    patient.addStudy(studyObj);
                patientMap.put(parentPatientID, patient);
                if(seachMode.equals("Patient ID")){
                    patients.add(patient);
                }
                if(seachMode.equals("Patient name")){
                    if(patient.getName().toUpperCase().contains(seachpatientName.toUpperCase())){
                        patients.add(patient);
                    }
                }

            }
        }
        adapter.notifyDataSetChanged();
    }
}
