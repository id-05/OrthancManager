package com.example.orthancmanager.date;

import com.google.gson.JsonArray;

public class Serie {

    String serieDescription;
    int nbInstances;
    String id;
    String seriesNumber;
    JsonArray instances;

    public JsonArray getInstances() {
        return instances;
    }

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

