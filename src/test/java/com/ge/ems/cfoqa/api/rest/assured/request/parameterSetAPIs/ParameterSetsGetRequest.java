package com.ge.ems.api.rest.assured.request.parameterSetAPIs;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.ge.ems.api.commons.RestAssuredHelper;
import com.ge.ems.api.util.Utils;
import com.jayway.restassured.response.Response;

public class ParameterSetsGetRequest {
	
	private static RestAssuredHelper rest = new RestAssuredHelper();
	private static String contentType = "application/json";
	private static String service = "/api/v2/ems-systems";
	
	public static JSONObject getParameterSetsGetResponse(String emsSystemId, String groupId, boolean isServiceExpectedToWork) {
		
		Map<String, String> request = new HashMap<String, String>();
		if (groupId != null) {
			request.put("groupId", groupId);
		}
		
		Response response = rest.createGetConnectionToAPI(request, contentType, service + "/" + emsSystemId + "/parameter-sets", isServiceExpectedToWork);
		JSONObject jsonObject = Utils.turnJSONToObject(response.asString());
		return jsonObject;
	}
}
