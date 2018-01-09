package com.ge.ems.api.rest.assured.request.uploadAPIs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.ems.api.commons.RestAssuredHelper;
import com.ge.ems.api.util.Utils;
import com.jayway.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeff.kramer on 9/11/2017.
 */
public class UploadApi {
    private static RestAssuredHelper rest = new RestAssuredHelper();
    private static String contentType = "application/json";
    private static String service = "/api/v2/buckets/";

    public static JSONObject getUploadListForBucket(String bucket, boolean expectedToWork){
        Utils.getToken();

        String apiCall = service + bucket + "/uploads";

        Response response = rest.createGetConnectionToAPI(null, contentType, apiCall, expectedToWork);

        String responseString = "{\"uploads\":" + response.asString() + "}";

        //System.out.println(responseString);

        return Utils.turnJSONToObject(responseString);
    }

    public static JSONObject getProcessingStatusForSetOfUploads(String bucket, String uploadIds, boolean expectedToWork){
        Utils.getToken();

        String apiCall = service + bucket + "/uploads/processing-status";
        Map<String, String> request = new HashMap<>();

        request.put("stringRequest", uploadIds);

        Response response = rest.createPostConnectionToAPI(request, contentType, apiCall, expectedToWork);

        String responseString = "{\"statuses\":[" + response.asString() + "]}";

        System.out.println(responseString);

        return Utils.turnJSONToObject(responseString);
    }
}
