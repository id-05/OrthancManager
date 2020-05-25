package com.example.orthancmanager;

import com.google.gson.JsonArray;

import java.util.ArrayList;

public class Serie {

    private String serieDescription;
    private String modality;
    private int nbInstances;
    private String firstInstanceId;
    private String studyOrthancId;
    private String id;
    private String seriesNumber;
    private String sopClassUid;
    private boolean secondaryCapture;

    public JsonArray getInstances() {
        return instances;
    }

    public void setInstances(JsonArray instances) {
        this.instances = instances;
    }

    private JsonArray instances;

    public Serie(String serieDescription, String modality, int nbInstances,
                 String id, String studyOrthancId, String firstInstanceId, String seriesNumber, String sopClassUid){
        this.serieDescription = serieDescription;
        this.modality = modality;
        this.nbInstances = nbInstances;
        this.id = id;
        this.studyOrthancId = studyOrthancId;
        this.firstInstanceId = firstInstanceId;
        this.sopClassUid = sopClassUid;
        this.seriesNumber=seriesNumber;
        checkIsSecondaryCapture();
    }

    public Serie(String seriesDescription, String seriesNumber, JsonArray instances, int size, String serieId) {
        this.serieDescription = seriesDescription;
        this.instances = instances;
        this.nbInstances = size;
        this.id = serieId;
        this.seriesNumber=seriesNumber;
    }

    private void checkIsSecondaryCapture() {
        ArrayList<String> sopClassUIDs = new ArrayList<String>();
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.1");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.2");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.3");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.4");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.11");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.22");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.33");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.40");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.50");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.59");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.65");
        sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.67");

        if(sopClassUIDs.contains(sopClassUid)){
            secondaryCapture=true;
        }else {
            secondaryCapture=false;
        }

    }
    public String getSerieDescription(){
        return serieDescription;
    }

    public void setSerieDescription(String serieDescription){
        this.serieDescription = serieDescription;
    }

    public String getId() {
        return id;
    }

    public String getModality() {
        return modality;
    }

    public int getNbInstances() {
        return nbInstances;
    }

    public String getFistInstanceId() {
        return firstInstanceId;
    }

    public String getStudyOrthancId() {
        return studyOrthancId;
    }

    public boolean isSecondaryCapture() {
        return secondaryCapture;
    }

    public String getSeriesNumber(){
        return this.seriesNumber;
    }

}

