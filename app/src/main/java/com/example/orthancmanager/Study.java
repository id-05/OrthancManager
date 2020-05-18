package com.example.orthancmanager;

import java.util.ArrayList;
import java.util.Date;
//import org.petctviewer.orthanc.anonymize.QueryOrthancData;
//import org.petctviewer.orthanc.setup.OrthancRestApis;

public class Study {

    public String studyDescription;
    public Date date;
    public String accession;
    public String StudyOrthancId;
    public String patientName;
    public String patientID;
    public Date birthDate;
    public String sex;
    public String patientOrthancId;
    public String studyInstanceUID;
  //  public ArrayList<Serie> childSeries;
    public int statNbInstance;
    public int statNbSeries;
    public int statMbSize;

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
    //    this.childSeries=childSeries;
        this.patientOrthancId=patientOrthancId;
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

    public String getPatientName() {
        return patientName;
    }

    public String getPatientID() {
        return patientID;
    }

    public Date getPatientDob() {
        return birthDate;
    }

    public String getPatientSex() {
        return sex;
    }

    public String getParentPatientId() {
        return patientOrthancId;
    }

//    public ArrayList<Series> getSeries() {
//        return childSeries;
//    }

//    public void setSeries(ArrayList<Serie> series) {
//        childSeries=series;
//    }

//    public int numberOfSeries() {
//        return childSeries.size();
//    }

    public String getStudyInstanceUid() {
        return studyInstanceUID;
    }


//    public void storeStudyStatistics(QueryOrthancData queryOrthanc) {
//        int[] statistics=queryOrthanc.
//                getStudyStatistics(
//                        StudyOrthancId);
//        this.statNbSeries=statistics[0];
//        this.statNbInstance=statistics[1];
//        this.statMbSize=statistics[2];
//
//    }

    public int getStatNbSeries() {
        return statNbSeries;
    }

    public int getStatNbInstance() {
        return statNbInstance;
    }

    public int getMbSize() {
        return statMbSize;
    }

//    public void refreshChildSeries(QueryOrthancData queryOrthanc) {
//        try {
//            Study2 tempstudy = queryOrthanc.getStudyDetails(this.StudyOrthancId, true);
//            this.setSeries(tempstudy.getSeries());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            this.childSeries=new ArrayList<Serie>();
//        }


//    }

//    public void deleteAllSc(OrthancRestApis connexion) {
//        for (Serie serie : childSeries) {
//            boolean sc=serie.isSecondaryCapture();
//            if(sc) {
//                connexion.makeDeleteConnection("/series/"+serie.getId());
//            }
//        }
//
//    }

//    public ArrayList<String> getModalitiesInStudy() {
//        ArrayList<String> modalities=new ArrayList<String>();
//
//        for (Serie serie : childSeries) {
//            String modality=serie.getModality();
//            if(!modalities.contains(modality)) {
//                modalities.add(modality);
//            }
//        }
//        return modalities;
//    }


}

