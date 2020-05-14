package com.example.orthancmanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;

public class JsonSettings {

    public JsonObject index = new JsonObject();
    // JsonObject des AET
    JsonObject dicomNode=new JsonObject();
    // JsonObject des orthancPeer
    JsonObject orthancPeer=new JsonObject();
    // JsonObject des contentType
    JsonObject contentType=new JsonObject();
    // JsonObject des dictionary
    public JsonObject dictionary=new JsonObject();
    // Array des Lua folder
    public JsonArray luaFolder=new JsonArray();
    //Array des plugin folder
    protected JsonArray pluginsFolder=new JsonArray();

    // Object pour les users
    protected JsonObject users =new JsonObject();

    //Object pour les metadata
    protected JsonObject userMetadata=new JsonObject();

    // A modifier via des setteurs
    String orthancName;
    protected String storageDirectory;
    protected String indexDirectory;
    protected boolean StorageCompression;
    protected int MaximumStorageSize;
    protected int MaximumPatientCount;
    protected boolean HttpServerEnabled;
    protected int HttpPort;
    protected boolean HttpDescribeErrors;
    protected boolean HttpCompressionEnabled;
    protected boolean DicomServerEnabled;
    protected String DicomAet;
    protected boolean DicomCheckCalledAet;
    protected int DicomPort;
    protected String DefaultEncoding;
    protected boolean DeflatedTransferSyntaxAccepted;
    protected boolean JpegTransferSyntaxAccepted;
    protected boolean Jpeg2000TransferSyntaxAccepted;
    protected boolean JpegLosslessTransferSyntaxAccepted;
    protected boolean JpipTransferSyntaxAccepted;
    protected boolean Mpeg2TransferSyntaxAccepted;
    protected boolean RleTransferSyntaxAccepted;
    protected boolean UnknownSopClassAccepted;
    protected int DicomScpTimeout;
    protected boolean RemoteAccessAllowed;
    protected boolean SslEnabled;
    protected String SslCertificate;
    protected boolean AuthenticationEnabled;
    protected int DicomScuTimeout;
    protected String HttpProxy;
    protected int HttpTimeout;
    protected boolean HttpsVerifyPeers;
    protected String HttpsCACertificates;
    protected int StableAge;
    protected boolean StrictAetComparison;
    protected boolean StoreMD5ForAttachments;
    protected int LimitFindResults;
    protected int LimitFindInstances;
    protected int LimitJobs;
    protected boolean LogExportedResources;
    protected boolean KeepAlive;
    protected boolean StoreDicom;
    protected int DicomAssociationCloseDelay;
    protected int QueryRetrieveSize;
    protected boolean CaseSensitivePN;
    protected boolean LoadPrivateDictionary;
    protected boolean dicomAlwaysAllowEcho;
    protected boolean DicomAlwaysStore;
    protected boolean CheckModalityHost;
    protected boolean SynchronousCMove;
    protected int JobsHistorySize;
    protected int ConcurrentJobs;



    protected boolean dicomModalitiesInDb;
    protected boolean orthancPeerInDb;
    protected boolean overwriteInstances;
    protected int mediaArchiveSize;
    protected String storageAccessOnFind;

    protected boolean httpVerbose;
    protected boolean tcpNoDelay;
    protected int httpThreadsCount;
    protected boolean saveJobs;
    protected boolean metricsEnabled;
    protected boolean AllowFindSopClassesInStudy;


    JsonSettings(String data) {
        JsonParser parser = new JsonParser();
        JsonObject orthancJson=new JsonObject();
        try {
            orthancJson = parser.parse(data).getAsJsonObject();
        }catch (Exception e){
            MainActivity.print(e.toString());
        }
        if (orthancJson.has("Name")) orthancName=orthancJson.get("Name").getAsString();
        if (orthancJson.has("StorageDirectory")) storageDirectory=orthancJson.get("StorageDirectory").getAsString();
        if (orthancJson.has("IndexDirectory")) indexDirectory=orthancJson.get("IndexDirectory").getAsString();
        if (orthancJson.has("StorageCompression")) StorageCompression=orthancJson.get("StorageCompression").getAsBoolean();
        if (orthancJson.has("MaximumStorageSize")) MaximumStorageSize=Integer.parseInt(orthancJson.get("MaximumStorageSize").getAsString());
        if (orthancJson.has("MaximumPatientCount")) MaximumPatientCount=Integer.parseInt(orthancJson.get("MaximumPatientCount").getAsString());
        if (orthancJson.has("HttpServerEnabled")) HttpServerEnabled=orthancJson.get("HttpServerEnabled").getAsBoolean();
        if (orthancJson.has("HttpPort")) HttpPort=Integer.parseInt(orthancJson.get("HttpPort").getAsString());
        if (orthancJson.has("HttpDescribeErrors")) HttpDescribeErrors=orthancJson.get("HttpDescribeErrors").getAsBoolean();
        if (orthancJson.has("HttpCompressionEnabled")) HttpCompressionEnabled=orthancJson.get("HttpCompressionEnabled").getAsBoolean();
        if (orthancJson.has("DicomServerEnabled")) DicomServerEnabled=orthancJson.get("DicomServerEnabled").getAsBoolean();
        if (orthancJson.has("DicomAet")) DicomAet=orthancJson.get("DicomAet").getAsString();
        if (orthancJson.has("DicomCheckCalledAet")) DicomCheckCalledAet=orthancJson.get("DicomCheckCalledAet").getAsBoolean();
        if (orthancJson.has("DicomPort")) DicomPort=orthancJson.get("DicomPort").getAsInt();
        if (orthancJson.has("DefaultEncoding")) DefaultEncoding=orthancJson.get("DefaultEncoding").getAsString();
        if (orthancJson.has("DeflatedTransferSyntaxAccepted")) DeflatedTransferSyntaxAccepted=orthancJson.get("DeflatedTransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("JpegTransferSyntaxAccepted")) JpegTransferSyntaxAccepted=orthancJson.get("JpegTransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("Jpeg2000TransferSyntaxAccepted")) Jpeg2000TransferSyntaxAccepted=orthancJson.get("Jpeg2000TransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("JpegLosslessTransferSyntaxAccepted")) JpegLosslessTransferSyntaxAccepted=orthancJson.get("JpegLosslessTransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("JpipTransferSyntaxAccepted")) JpipTransferSyntaxAccepted=orthancJson.get("JpipTransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("Mpeg2TransferSyntaxAccepted")) Mpeg2TransferSyntaxAccepted=orthancJson.get("Mpeg2TransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("RleTransferSyntaxAccepted")) RleTransferSyntaxAccepted=orthancJson.get("RleTransferSyntaxAccepted").getAsBoolean();
        if (orthancJson.has("UnknownSopClassAccepted")) UnknownSopClassAccepted=orthancJson.get("UnknownSopClassAccepted").getAsBoolean();
        if (orthancJson.has("DicomScpTimeout")) DicomScpTimeout=orthancJson.get("DicomScpTimeout").getAsInt();
        if (orthancJson.has("RemoteAccessAllowed")) RemoteAccessAllowed=orthancJson.get("RemoteAccessAllowed").getAsBoolean();
        if (orthancJson.has("SslEnabled")) SslEnabled=orthancJson.get("SslEnabled").getAsBoolean();
        if (orthancJson.has("SslCertificate")) SslCertificate=orthancJson.get("SslCertificate").getAsString();
        if (orthancJson.has("AuthenticationEnabled")) AuthenticationEnabled=orthancJson.get("AuthenticationEnabled").getAsBoolean();
        if (orthancJson.has("DicomScuTimeout")) DicomScuTimeout=orthancJson.get("DicomScuTimeout").getAsInt();
        if (orthancJson.has("HttpProxy")) HttpProxy=orthancJson.get("HttpProxy").getAsString();
        if (orthancJson.has("HttpTimeout")) HttpTimeout=orthancJson.get("HttpTimeout").getAsInt();
        if (orthancJson.has("HttpsVerifyPeers")) HttpsVerifyPeers=orthancJson.get("HttpsVerifyPeers").getAsBoolean();
        if (orthancJson.has("HttpsCACertificates")) HttpsCACertificates=orthancJson.get("HttpsCACertificates").getAsString();
        if (orthancJson.has("StableAge")) StableAge=orthancJson.get("StableAge").getAsInt();
        if (orthancJson.has("StrictAetComparison")) StrictAetComparison=orthancJson.get("StrictAetComparison").getAsBoolean();
        if (orthancJson.has("StoreMD5ForAttachments")) StoreMD5ForAttachments=orthancJson.get("StoreMD5ForAttachments").getAsBoolean();
        if (orthancJson.has("LimitFindResults")) LimitFindResults=orthancJson.get("LimitFindResults").getAsInt();
        if (orthancJson.has("LimitFindInstances")) LimitFindInstances=orthancJson.get("LimitFindInstances").getAsInt();
        if (orthancJson.has("LimitJobs")) LimitJobs=orthancJson.get("LimitJobs").getAsInt();
        if (orthancJson.has("LogExportedResources")) LogExportedResources=orthancJson.get("LogExportedResources").getAsBoolean();
        if (orthancJson.has("KeepAlive")) KeepAlive=orthancJson.get("KeepAlive").getAsBoolean();
        if (orthancJson.has("StoreDicom")) StoreDicom=orthancJson.get("StoreDicom").getAsBoolean();
        if (orthancJson.has("DicomAssociationCloseDelay")) DicomAssociationCloseDelay=orthancJson.get("DicomAssociationCloseDelay").getAsInt();
        if (orthancJson.has("QueryRetrieveSize")) QueryRetrieveSize=orthancJson.get("QueryRetrieveSize").getAsInt();
        if (orthancJson.has("CaseSensitivePN")) CaseSensitivePN=orthancJson.get("CaseSensitivePN").getAsBoolean();
        if (orthancJson.has("AllowFindSopClassesInStudy")) AllowFindSopClassesInStudy=orthancJson.get("AllowFindSopClassesInStudy").getAsBoolean();
        if (orthancJson.has("LoadPrivateDictionary")) LoadPrivateDictionary=orthancJson.get("LoadPrivateDictionary").getAsBoolean();
        if (orthancJson.has("DicomCheckModalityHost")) CheckModalityHost=orthancJson.get("DicomCheckModalityHost").getAsBoolean();
        if (orthancJson.has("DicomAlwaysAllowStore")) DicomAlwaysStore=orthancJson.get("DicomAlwaysAllowStore").getAsBoolean();
        if (orthancJson.has("DicomAlwaysAllowEcho")) dicomAlwaysAllowEcho=orthancJson.get("DicomAlwaysAllowEcho").getAsBoolean();
        if (orthancJson.has("SynchronousCMove")) SynchronousCMove=orthancJson.get("SynchronousCMove").getAsBoolean();
        if (orthancJson.has("JobsHistorySize")) JobsHistorySize=orthancJson.get("JobsHistorySize").getAsInt();
        if (orthancJson.has("ConcurrentJobs")) ConcurrentJobs=orthancJson.get("ConcurrentJobs").getAsInt();
        if (orthancJson.has("DicomModalitiesInDatabase")) dicomModalitiesInDb=orthancJson.get("DicomModalitiesInDatabase").getAsBoolean();
        if (orthancJson.has("OrthancPeersInDatabase")) orthancPeerInDb=orthancJson.get("OrthancPeersInDatabase").getAsBoolean();
        if (orthancJson.has("OverwriteInstances")) overwriteInstances=orthancJson.get("OverwriteInstances").getAsBoolean();
        if (orthancJson.has("MediaArchiveSize")) mediaArchiveSize=orthancJson.get("MediaArchiveSize").getAsInt();
        if (orthancJson.has("StorageAccessOnFind")) storageAccessOnFind=orthancJson.get("StorageAccessOnFind").getAsString();
        if (orthancJson.has("HttpVerbose")) httpVerbose=orthancJson.get("HttpVerbose").getAsBoolean();
        if (orthancJson.has("TcpNoDelay")) tcpNoDelay=orthancJson.get("TcpNoDelay").getAsBoolean();
        if (orthancJson.has("HttpThreadsCount")) httpThreadsCount=orthancJson.get("HttpThreadsCount").getAsInt();
        if (orthancJson.has("SaveJobs")) saveJobs=orthancJson.get("SaveJobs").getAsBoolean();
        if (orthancJson.has("MetricsEnabled")) metricsEnabled=orthancJson.get("MetricsEnabled").getAsBoolean();



        //On recupere les autres objet JSON dans le JSON principal
        //on recupere les AET declares par un nouveau parser
        if (orthancJson.has("DicomModalities")) dicomNode= orthancJson.get("DicomModalities").getAsJsonObject();

        //On recupere les users
        if (orthancJson.has("RegisteredUsers")) users= orthancJson.get("RegisteredUsers").getAsJsonObject();

        // On recupere les Lua scripts
        if (orthancJson.has("LuaScripts")) luaFolder= orthancJson.get("LuaScripts").getAsJsonArray();

        // On recupere les plugins
        if (orthancJson.has("Plugins")) pluginsFolder= orthancJson.get("Plugins").getAsJsonArray();

        //On recupere les metadata
        if (orthancJson.has("UserMetadata")) userMetadata= orthancJson.get("UserMetadata").getAsJsonObject();

        // On recupere les dictionnary
        if (orthancJson.has("Dictionary")) dictionary= orthancJson.get("Dictionary").getAsJsonObject();

        // On recupere les Content
        if (orthancJson.has("UserContentType")) contentType= orthancJson.get("UserContentType").getAsJsonObject();

        // On recupere les Peer
        if (orthancJson.has("OrthancPeers")) {
            orthancPeer=orthancJson.get("OrthancPeers").getAsJsonObject();
        }
    }
}
