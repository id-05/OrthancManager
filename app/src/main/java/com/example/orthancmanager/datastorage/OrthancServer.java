package com.example.orthancmanager.datastorage;

public class OrthancServer {

    public int id;
    public String ipaddress;
    public String port;
    public String login;
    public String password;
    public String OS;
    public String pathToJson;
    public String name;
    public String dicomaet;
    public String version;
    public Boolean connect;
    public int CountInstances;
    public int CountPatients;
    public int CountSeries;
    public int CountStudies;
    public int TotalDiskSizeMB;

    public int getCountInstances() {
        return CountInstances;
    }

    public void setCountInstances(int countInstances) {
        CountInstances = countInstances;
    }

    public int getCountPatients() {
        return CountPatients;
    }

    public void setCountPatients(int countPatients) {
        CountPatients = countPatients;
    }

    public int getCountSeries() {
        return CountSeries;
    }

    public void setCountSeries(int countSeries) {
        CountSeries = countSeries;
    }

    public int getCountStudies() {
        return CountStudies;
    }

    public void setCountStudies(int countStudies) {
        CountStudies = countStudies;
    }

    public int getTotalDiskSizeMB() {
        return TotalDiskSizeMB;
    }

    public void setTotalDiskSizeMB(int totalDiskSizeMB) {
        TotalDiskSizeMB = totalDiskSizeMB;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getPathToJson() {
        return pathToJson;
    }

    public void setPathToJson(String pathToJson) {
        this.pathToJson = pathToJson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getConnect() {
        return connect;
    }

    public void setConnect(Boolean connect) {
        this.connect = connect;
    }

    public String getDicomaet() {
        return dicomaet;
    }

    public void setDicomaet(String dicomaet) {
        this.dicomaet = dicomaet;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
