package com.example.orthancmanager.datastorage;

import com.google.gson.JsonArray;

public class Serie {

    private String serieDescription;
    private int nbInstances;
    private String id;
    private String seriesNumber;

    public JsonArray getInstances() {
        return instances;
    }

    private JsonArray instances;

    public Serie(String seriesDescription, String seriesNumber, JsonArray instances, int size, String serieId) {
        this.serieDescription = seriesDescription;
        this.instances = instances;
        this.nbInstances = size;
        this.id = serieId;
        this.seriesNumber=seriesNumber;
    }

    public String getSerieDescription(){
        return serieDescription;
    }

    public String getId() {
        return id;
    }

    public int getNbInstances() {
        return nbInstances;
    }

    public String getSeriesNumber(){
        return this.seriesNumber;
    }

}

