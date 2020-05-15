package com.example.orthancmanager;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orthancmanager.datastorage.Patient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PatientsFragment extends Fragment {

    private SharedPreferences prefs;
    private JsonParser parserJson = new JsonParser();
    private SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
    //SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_patients, container, false);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //String data = prefs.getString("SeachResult", "*");
        //MainActivity.print("data = "+data);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            String data = prefs.getString("SeachResult", "*");
            MainActivity.print("data = "+data);
            getPatientsFromJson(data);
        }
        else {
        }
    }

    public void getPatientsFromJson(String data){
        HashMap<String, Patient> patientMap=new HashMap<String, Patient>();
        JsonArray studies=(JsonArray) parserJson.parse(data);
        Iterator<JsonElement> studiesIterator=studies.iterator();

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
            if(parentPatientDetails.has("PatientBirthDate")) {
                patientDobString=parentPatientDetails.get("PatientBirthDate").getAsString();
            }

            try {
                patientDob = format.parse("19000101");
                patientDob=format.parse(patientDobString);
            } catch (Exception e) { }

            if(parentPatientDetails.has("PatientSex")) {
                patientSex=parentPatientDetails.get("PatientSex").getAsString();
            }

            if(parentPatientDetails.has("PatientName")) {
                patientName=parentPatientDetails.get("PatientName").getAsString();
            }
            if(parentPatientDetails.has("PatientID")) {
                patientId=parentPatientDetails.get("PatientID").getAsString();
            }

            String accessionNumber="N/A";
            if(studyDetails.has("AccessionNumber")) {
                accessionNumber=studyDetails.get("AccessionNumber").getAsString();
            }

            String studyInstanceUid=studyDetails.get("StudyInstanceUID").getAsString();

            String studyDate=null;
            Date studyDateObject=null;
            if(studyDetails.has("StudyDate")) {
                studyDate=studyDetails.get("StudyDate").getAsString();
            }

            try {
                studyDateObject=format.parse("19000101");
                studyDateObject=format.parse(studyDate);
            } catch (Exception e) { }

            String studyDescription="N/A";
            if(studyDetails.has("StudyDescription")){
                studyDescription=studyDetails.get("StudyDescription").getAsString();
            }

            //Study2 studyObj=new Study2(studyDescription, studyDateObject, accessionNumber, studyId, patientName, patientId, patientDob, patientSex, parentPatientID, studyInstanceUid, null);

            if(!patientMap.containsKey(parentPatientID)) {
                Patient patient=new Patient(patientName,patientId,patientBirthDate,patientSex,parentPatientID);
                //    patient.addStudy(studyObj);
                patientMap.put(parentPatientID, patient);
            }else {
                //  patientMap.get(parentPatientID).addStudy(studyObj);
            }

        }
        ////
        Iterator<Map.Entry<String, Patient>> iterator = patientMap.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<String, Patient> pair = iterator.next();
            String key = pair.getKey();
            Patient value = pair.getValue();
            MainActivity.print(value.patientId + " : " + value.name);
        }
    }
}
