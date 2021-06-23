package com.example.orthancmanager.date;

import java.util.Date;

public class Study {

    public String studyDescription;
    private Date date;
    private String accession;
    private String StudyOrthancId;
    private String patientName;
    private String patientID;
    private Date birthDate;
    private String sex;
    private String patientOrthancId;
    private String studyInstanceUID;

    public Study(String studyDescription, Date date, String accession,
                  String StudyOrthancId, String patientName, String patientID, Date birthDate, String sex, String patientOrthancId, String studyInstanceUID) {
        this.studyDescription = studyDescription;
        this.date = date;
        this.accession = accession;
        this.StudyOrthancId = StudyOrthancId;
        this.patientName = patientName;
        this.patientID = patientID;
        this.birthDate=birthDate;
        this.sex=sex;
        this.studyInstanceUID=studyInstanceUID;
        this.patientOrthancId=patientOrthancId;
    }

    public Study(String studyDescription, Date studyDateObject, String accessionNumber, String studyId) {
        this.studyDescription = studyDescription;
        this.date = studyDateObject;
        this.accession = accessionNumber;
        this.StudyOrthancId = studyId;
    }

    public String getStudyDescription() {
        return studyDescription;
    }

    public String getOrthancId() {
        return StudyOrthancId;
    }

    public Date getDate() {
        return date;
    }

    public String getAccession() {
        return accession;
    }

}

