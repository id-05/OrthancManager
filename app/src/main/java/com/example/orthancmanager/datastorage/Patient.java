package com.example.orthancmanager.datastorage;

import java.util.HashMap;

public class Patient {

    public String name;
    private String patientId;
    private String orthancID;
    private String birthDate;
    private String sex;
    private HashMap<String, Study> childStudies;

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

    public void addStudy(Study study) {
        if(childStudies==null) {
            childStudies= new HashMap<>();
        }
        childStudies.put(study.getOrthancId(), study);
    }

//    public void addStudies(ArrayList<Study> studies) {
//        if(childStudies==null) {
//            childStudies= new HashMap<>();
//        }
//        for(Study study:studies) {
//            childStudies.put(study.getOrthancId(), study);
//        }
//
//    }

//    public ArrayList<Study> getStudies(){
//        Study[] studyArray=childStudies.values().toArray(new Study[0]);
//        return new ArrayList<>(Arrays.asList(studyArray));
//    }

//    public Study getChildStudy(String studyOrthancID) {
//        return childStudies.get(studyOrthancID);
//    }

}

