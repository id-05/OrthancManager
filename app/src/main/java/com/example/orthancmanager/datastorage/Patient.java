package com.example.orthancmanager.datastorage;

//import java.util.ArrayList;
import com.example.orthancmanager.Study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.HashMap;

public class Patient {

    public String name;
    public String patientId;
    public String orthancID;
    public String birthDate;
    public String sex;
    public HashMap<String, Study> childStudies;

    public Patient(String name, String patientId, String birthDate, String sex, String orthancID) {
        this.name=name;
        this.patientId=patientId;
        this.orthancID=orthancID;
        this.birthDate=birthDate;
        this.sex=sex;
    }

    public String getName() {
        return this.name;
    }

    public String getPatientId() {
        return this.patientId;
    }

    public String getPatientOrthancId() {
        return this.orthancID;
    }

    public String getPatientBirthDate() {
        return this.birthDate;
    }

    public String getPatientSex() {
        return this.sex;
    }

//    public void refreshChildStudies(QueryOrthancData queryOrthanc) {
//        childStudies= new HashMap<String, Study2>();
//        ArrayList<Study2> allStudies=queryOrthanc.getAllStudiesOfPatient(orthancID, false);
//        for(Study2 study: allStudies) {
//            this.childStudies.put(study.getOrthancId(), study);
//        }
//    }

    public void addStudy(Study study) {
        if(childStudies==null) {
            childStudies= new HashMap<String, Study>();
        }
        childStudies.put(study.getOrthancId(), study);
    }

    public void addStudies(ArrayList<Study> studies) {
        if(childStudies==null) {
            childStudies= new HashMap<String, Study>();
        }
        for(Study study:studies) {
            childStudies.put(study.getOrthancId(), study);
        }

    }
//
//    /**
//     * Return the studies of this object
//     * @return
//     */
    public ArrayList<Study> getStudies(){
        Study[] studyArray=childStudies.values().toArray(new Study[0]);
        return new ArrayList<Study>(Arrays.asList(studyArray));
    }

    public Study getChildStudy(String studyOrthancID) {
        return childStudies.get(studyOrthancID);
    }

}

