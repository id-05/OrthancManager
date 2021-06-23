package com.example.orthancmanager.date;

import java.util.HashMap;

public class Patient {

    String name;
    String patientId;
    String orthancID;
    String birthDate;
    String sex;
    HashMap<String, Study> childStudies;

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
}

