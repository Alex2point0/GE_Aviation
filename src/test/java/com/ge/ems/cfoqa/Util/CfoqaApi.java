package com.ge.ems.cfoqa.Util;

import com.ge.ems.api.rest.assured.request.parameterSetAPIs.ParameterSetsGetRequest;
import com.ge.ems.api.rest.assured.request.uploadAPIs.UploadApi;
import com.ge.ems.api.util.Utils;
import com.ge.ems.common.Utilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.File;
import java.util.*;

/**
 * Created by jeff.kramer on 4/24/2017.
 */
public class CfoqaApi {

    private final static Logger logger = LoggerFactory.getLogger(CfoqaApi.class);
    private final static HashMap<String, String> processingErrorMessages;
    private final static HashMap<String, String> uploadErrorMessages;

    static{
        processingErrorMessages = new HashMap<>();
        processingErrorMessages.put("Failed Download Auto-ACID", "Mismatched/unknown tail number");
        processingErrorMessages.put("Failed Download Sync", "Check your file");
        processingErrorMessages.put("Failed Download Dice and Deidentify", "Check your file");
        processingErrorMessages.put("Failed Download Duplicate Detection", "Duplicate file");
        processingErrorMessages.put("", "");

        uploadErrorMessages = new HashMap<>();
        uploadErrorMessages.put("processedFailure", "Submit a support ticket");
        uploadErrorMessages.put("abandonedProcessing", "Submit a support ticket");
        uploadErrorMessages.put("abandonedTransfer", "Check your connection");
        uploadErrorMessages.put("canceled", "Canceled by the user");

    }

    /**
     * View Upload page queries
     */

    public static JSONArray getViewUploadDownloadInformation(String headerUploadId){
        File downloadQueryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\DownloadInformation.txt");
        List<JSONArray> apiDownloadData = Utils.queryForData(downloadQueryFile, false, "--DOWNLOAD_ID--", headerUploadId, Utilities.FOQA_DOWNLOADS_DATABASE);

        Assert.assertNotEquals(apiDownloadData.size(), 0, "API query to retrieve download information for the View Upload page returned 0 rows.");

        return apiDownloadData.get(0);
    }

    /**
     * Value locations for the query run to get the Flight Data from the API
     *
     * [0] - Flight Id
     * [1] - Exact Date
     * [2] - Hours
     * [3] - Percent Invalid
     * [4] - Duplicate Detection Master
     * [5] - City Pair
     * [6] - Event Count
     * [7] - Takeoff Exists
     * [8] - File Exists
     * [9] - Trajectory Exists
     * [10] - Fleet
     * [11] - APM Profile
     */
    public static JSONArray getViewUploadFlightInformation(String headerUploadId){

        File flightQueryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\ViewUploadFlightInformation.txt");

        List<JSONArray> apiFlightDataList = Utils.queryForData(flightQueryFile, false, "--DOWNLOAD_ID--", headerUploadId);

        Assert.assertNotEquals(apiFlightDataList.size(), 0, "No flight data returned from the API.");

        return apiFlightDataList.get(0);
    }

    /**
     * View Event page queries
     */

    public static ArrayList<String> getViewEventTailNumbers(String eventProfileId){
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\EventSearchByTailNumberN101AD.txt");

        ArrayList<String> tailNumberList = new ArrayList<>();

        List<JSONArray> apiData = Utils.queryForData(queryFile, false, Utilities.APM_EVENTS_DATABASE.replace("<profile_id>", eventProfileId));

        for(JSONArray data : apiData){
            tailNumberList.add(data.get(3).toString());
        }

        Collections.sort(tailNumberList);

        return tailNumberList;
    }

    public static ArrayList<String> getViewEventEvents(String eventProfileId){
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\EventSearchByEvent.txt");

        ArrayList<String> eventList = new ArrayList<>();

        List<JSONArray> apiData = Utils.queryForData(queryFile, false, Utilities.APM_EVENTS_DATABASE.replace("<profile_id>", eventProfileId));

        for(JSONArray data : apiData){
            eventList.add(data.get(2).toString());
        }

        return eventList;
    }

    public static ArrayList<String> getViewEventsDownloadDate(String eventProfileId){
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\EventSearchByDownloadDate.txt");

        ArrayList<String> dateList = new ArrayList<>();

        List<JSONArray> apiData = Utils.queryForData(queryFile, false, Utilities.APM_EVENTS_DATABASE.replace("<profile_id>", eventProfileId));

        for(JSONArray data : apiData){
            dateList.add(data.get(1).toString());
        }

        return dateList;
    }

    public static ArrayList<ViewEventResultRow> getViewEventSearchResults(String eventProfileId){
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\EventSearch.txt");

        ArrayList<ViewEventResultRow> searchResultList = new ArrayList<>();

        List<JSONArray> apiData = Utils.queryForData(queryFile, false, Utilities.APM_EVENTS_DATABASE.replace("<profile_id>", eventProfileId));

        for(JSONArray data : apiData){
            Integer flightId = Integer.parseInt(data.get(0).toString());
            String collectionDate = data.get(1).toString();
            String event = data.get(2).toString();
            String tailNumber = data.get(3).toString();
            searchResultList.add(new ViewEventResultRow(flightId, collectionDate, event, tailNumber));
        }

        Collections.sort(searchResultList, new ViewEventResultCompartor());

        return searchResultList;
    }

    /**
     * Event Context View page queries
     */

    /**
     * [0] - Flight Id
     * [1] - Fleet
     * [2] - Tail Number
     * [3] - Takeoff Airport
     * [4] - Takeoff Runway
     * [5] - Month/Year
     * [6] - Full Date & Time
     *
     * @return
     */
    public static String[] getContextViewFlightInformation(){
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\ContextViewFlightInformation.txt");

        String[] flightInformation = new String[7];

        List<JSONArray> apiData = Utils.queryForData(queryFile, false);

        JSONArray data = apiData.get(0);
        flightInformation[0] = data.get(0).toString();
        flightInformation[1] = data.get(1).toString();
        flightInformation[2] = data.get(2).toString();
        flightInformation[3] = data.get(3).toString();
        flightInformation[4] = data.get(4).toString();
        flightInformation[5] = data.get(5).toString();
        flightInformation[6] = data.get(6).toString();

        return flightInformation;
    }

    public static LinkedList<String> getContextViewEventList(String flightId, String eventProfileId){
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\ContextViewEventList.txt");

        List<JSONArray> apiData = Utils.queryForData(queryFile, false, "--FLIGHT_ID--", flightId,  Utilities.APM_EVENTS_DATABASE.replace("<profile_id>", eventProfileId));

        LinkedList<String> eventList = new LinkedList<>();

        for(JSONArray data : apiData){
            eventList.add((String)data.get(0));
        }

        return eventList;
    }

    public static LinkedHashMap<String, UploadResultRow> getUploadFlightUploadData(LinkedHashMap<String, UploadResultRow> appUploadData){
        LinkedHashMap<String, UploadResultRow> apiUploadData = new LinkedHashMap<>();
        JSONObject apiResults = UploadApi.getUploadListForBucket("TestBucketOne", true);

        JSONArray uploads = (JSONArray)apiResults.get("uploads");

        processGetUploadsRequest(uploads, apiUploadData, appUploadData);

        Set<String> uploadIdSet = apiUploadData.keySet();
        String uploadIds = getUploadIdString(uploadIdSet, apiUploadData);

        apiResults = UploadApi.getProcessingStatusForSetOfUploads("TestBucketOne", uploadIds, true);

        JSONArray result = (JSONArray)apiResults.get("statuses");
        JSONObject statuses = (JSONObject)result.get(0);

        processProcessingStatusRequest(uploadIdSet, statuses, apiUploadData);

        getDuplicateFlights(uploadIdSet, apiUploadData);

        return apiUploadData;
    }

    private static void processGetUploadsRequest(JSONArray uploads, LinkedHashMap<String, UploadResultRow> apiUploadData, LinkedHashMap<String, UploadResultRow> appUploadData){
        logger.info("Processing upload data...");
        int rows = appUploadData.size();
        for(int i = 0; i < rows; i++){
            JSONObject upload = (JSONObject)uploads.get(i);
            String uploadId = (String)upload.get("id");

            if(appUploadData.containsKey(uploadId)) {

                String state = (String) upload.get("state");
                String name = (String) upload.get("name");
                String size = CfoqaUtilities.convertToKBString((Long) upload.get("totalSize"));

                JSONObject metadata = (JSONObject) upload.get("metadata");
                String tailNumber = (String) metadata.get("aircraft_ID");
                String downloadDate = (String) metadata.get("download_Date");
                String comments = (String) metadata.get("comments");

                if(comments.equals("")){
                    comments = "No comments.";
                }

                UploadMetadata uploadMetadata = new UploadMetadata(uploadId, null, name, size, state, null, null);

                UploadResultRow resultRow = new UploadResultRow(downloadDate, tailNumber, null, comments, null, null, uploadMetadata);

                apiUploadData.put(uploadId, resultRow);
            } else {
                rows++;
            }
        }
    }

    private static String getUploadIdString(Set<String> uploadIdSet, LinkedHashMap<String, UploadResultRow> apiUploadData){
        Integer uploadCount = apiUploadData.size();
        Integer counter = 1;
        String uploadIds = "[";

        for(String id : uploadIdSet){

            uploadIds += "\"" + id + "\"";

            if(counter < uploadCount){
                uploadIds += ",";
                counter++;
            }
        }

        uploadIds += "]";
        return uploadIds;
    }

    private static void processProcessingStatusRequest(Set<String> uploadIdSet, JSONObject statuses, LinkedHashMap<String, UploadResultRow> apiUploadData){
        logger.info("Processing processing statuses...");
        for(String uploadId : uploadIdSet){
            UploadResultRow uploadData = apiUploadData.get(uploadId);
            UploadMetadata uploadMetadata = uploadData.getMetadata();
            String state = uploadMetadata.getState();
            String iconClass;
            String downloadId;

            JSONObject status = (JSONObject)statuses.get(uploadId);
            String processingState = (String)status.get("downloadState");
            String processingFailure = (String)status.get("errorMessage");

            if(state.equals("processedSuccess")){
                downloadId = Long.toString((Long) status.get("downloadRecord"));
            } else {
                downloadId = "";
            }

            if( processingState.equals("failure") ){
                iconClass = "fa-times-circle";
            } else if( state.equals("canceled") ){
                iconClass = "fa-exclamation-circle";
            } else {
                iconClass = "fa-check-circle";
            }

            JSONArray flightArray = (JSONArray)status.get("flights");
            String flights = Integer.toString(flightArray.size());

            uploadMetadata.setProcessingFailure(processingFailure);
            uploadMetadata.setProcessingState(processingState);
            uploadMetadata.setDownloadId(downloadId);

            if(state.equals("processedSuccess")) {
                uploadData.setErrorMsg(processingErrorMessages.get(processingFailure));
            } else {
                uploadData.setErrorMsg(uploadErrorMessages.get(state));
            }

            uploadData.setDuplicateFlights(flights);
            uploadData.setIconClass(iconClass);
        }
    }

    private static void getDuplicateFlights(Set<String> uploadIdSet, LinkedHashMap<String, UploadResultRow> apiUploadData){
        logger.info("Retrieving duplicate flights from API...");
        File queryFile = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\queries\\UploadFlightDuplicateFlights.txt");

        String filter = "";

        for(String uploadId : uploadIdSet){
            String filterTemplate = ",{\"type\":\"constant\",\"value\":\"--UPLOAD_ID--\"}";
            filter += filterTemplate.replace("--UPLOAD_ID--", uploadId);
        }

        logger.info("Filter - " + filter);

        List<JSONArray> apiData = Utils.queryForData(queryFile, false, "--UPLOAD_ID_FILTER--", filter, Utilities.FOQA_FLIGHTS_DATABASE);

        for(JSONArray data : apiData){
            UploadResultRow resultRow = apiUploadData.get(data.get(0));
            String numDuplicateFlights = Long.toString((Long)data.get(1));
            String numFlights = resultRow.getDuplicateFlights();
            String duplicateFlights = numDuplicateFlights + " of " + numFlights;
            resultRow.setDuplicateFlights(duplicateFlights);
        }
    }

}
