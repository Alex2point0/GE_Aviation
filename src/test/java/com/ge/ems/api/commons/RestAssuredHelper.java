package com.ge.ems.api.commons;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;

import com.ge.ems.api.util.ReportUtil;
import com.ge.ems.api.util.Utils;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Utility for RestAssured
 * 
 * @author shung
 *
 */
public class RestAssuredHelper {
    private static final String URL = PropertiesHandler.properties.getProperty(PropertiesHandler.SERVICE_URL);
    
    private static final String predix_UAA_URL = PropertiesHandler.properties.getProperty(PropertiesHandler.UAA_URL);
    
    private static final String predix_proxy = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_PROXY);

    private static final String predix_proxy_port = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_PROXY_PORT);

	final static protected Logger logger = LoggerFactory.getLogger(RestAssuredHelper.class);

	private static String host = "ems-web-server";
	
	private RequestSpecification reqSpecification;
	
    /**
     * Helper for RestAssured GET call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @param useProxy
     * @return response
     */
    public Response createGetConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork, boolean useProxy) {
    	if (request != null) {
        	Reporter.log(request.toString());
    	}
    	RestAssured.baseURI = URL;
        Reporter.log("\n" + URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        if (!useProxy) {
        	headers.put("Host", host);
        }
        headers.put("Content-Type", contentType);
        headers.put("Authorization", "Bearer " + Utils.TOKEN);
        if (request != null) {
            reqSpecification = RestAssured.given().headers(headers).params(request).when();
        } else {
        	reqSpecification = RestAssured.given().headers(headers).when();
        }
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = null;
        if (useProxy && !predix_proxy.isEmpty()) {
            response = reqSpecification.proxy(predix_proxy, Integer.valueOf(predix_proxy_port)).get(service);
        } else {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).get(service);
        }
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        ReportUtil.printFormattedJSON(response.asString(), service + " Response");
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 200) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }
	
    /**
     * Helper for RestAssured POST call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @param useProxy
     * @return response
     */
    public Response createPostConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork, boolean useProxy) {
    	if (request != null) {
    		ReportUtil.printFormattedJSON(new JSONObject(request).toString(), service + " Request");
    	}
    	RestAssured.baseURI = URL;
        Reporter.log("\n" + URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        if (!useProxy) {
        	headers.put("Host", host);
        }
        headers.put("Content-Type", contentType);
        headers.put("Authorization", "Bearer " + Utils.TOKEN);        
        if (request != null) {
            if(request.size() == 1 && request.containsKey("stringRequest")) {
                String requestString = request.get("stringRequest");
                reqSpecification = RestAssured.given().headers(headers).body(requestString).when();
            } else {
                reqSpecification = RestAssured.given().headers(headers).body(request).when();
            }
        } else {
        	reqSpecification = RestAssured.given().headers(headers).when();
        }
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = null;
        if (useProxy && !predix_proxy.isEmpty()) {
            response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).proxy(predix_proxy, Integer.valueOf(predix_proxy_port)).post(service);
        } else {
            response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).post(service);
        }
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        if (response.getStatusCode() != 204) {
        	ReportUtil.printFormattedJSON(response.asString(), service + " Response");
        }
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 201 || response.getStatusCode() == 204 || response.getStatusCode() == 200) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }

    /**
     * Helper for RestAssured DELETE call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @param useProxy
     * @return response
     */
    public Response createDeleteConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork, boolean useProxy) {
    	if (request != null) {
    		ReportUtil.printFormattedJSON(new JSONObject(request).toString(), service + " Request");
    	}
    	RestAssured.baseURI = URL;
        Reporter.log("\n" + URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        if (!useProxy) {
        	headers.put("Host", host);
        }
        headers.put("Content-Type", contentType);
        headers.put("Authorization", "Bearer " + Utils.TOKEN);
    	if (request != null) {
    		reqSpecification = RestAssured.given().headers(headers).params(request).when();
    	} else {
    		reqSpecification = RestAssured.given().headers(headers).when();
    	}
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = null;
        if (useProxy && !predix_proxy.isEmpty()) {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).proxy(predix_proxy, Integer.valueOf(predix_proxy_port)).delete(service);
        } else {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).delete(service);
        }
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 204) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }
    
    /**
     * Helper for RestAssured PUT call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @param useProxy
     * @return response
     */
	public Response createPutConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork, boolean useProxy) {
    	if (request != null) {
    		ReportUtil.printFormattedJSON(new JSONObject(request).toString(), service + " Request");
    	}
    	RestAssured.baseURI = URL;
        Reporter.log("\n" + URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        if (!useProxy) {
        	headers.put("Host", host);
        }
        headers.put("Content-Type", contentType);
        headers.put("Authorization", "Bearer " + Utils.TOKEN);
        if (request != null) {
        	reqSpecification = RestAssured.given().headers(headers).body(request).when();
        } else {
        	reqSpecification = RestAssured.given().headers(headers).when();
        }
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = null;
        if (useProxy && !predix_proxy.isEmpty()) {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).proxy(predix_proxy, Integer.valueOf(predix_proxy_port)).put(service);
        } else {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).put(service);
        }
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        if (response.getStatusCode() != 204) {
        	ReportUtil.printFormattedJSON(response.asString(), service + " Response");
        }
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 200) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }
    
    /**
     * Helper for RestAssured PATCH call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @param useProxy
     * @return response
     */
	public Response createPatchConnectionToAPI(Map<String, Object> request, String contentType, String service, boolean isServiceExpectedToWork, boolean useProxy) {
    	if (request != null) {
    		ReportUtil.printFormattedJSON(new JSONObject(request).toString(), service + " Request");
    	}
    	RestAssured.baseURI = URL;
        Reporter.log("\n" + URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        if (!useProxy) {
        	headers.put("Host", host);
        }
        headers.put("Content-Type", contentType);
        headers.put("Authorization", "Bearer " + Utils.TOKEN);
        if (request != null) {
        	reqSpecification = RestAssured.given().headers(headers).body(request).when();
        } else {
        	reqSpecification = RestAssured.given().headers(headers).when();
        }
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = null;
        if (useProxy && !predix_proxy.isEmpty()) {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).proxy(predix_proxy, Integer.valueOf(predix_proxy_port)).patch(service);
        } else {
        	response = reqSpecification.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).patch(service);
        }
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        if (response.getStatusCode() != 204) {
        	ReportUtil.printFormattedJSON(response.asString(), service + " Response");
        }
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 200) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }
    
    /**
     * Helper for RestAssured sign on call with request
     * 
     * @param username
     * @param password
     * @param isServiceExpectedToWork
     * @return response
     */
    public Response createSignOnConnectionToAPI(String username, String password, boolean isServiceExpectedToWork) {
    	logger.info(username + "Sign On Request");
    	RestAssured.baseURI = URL;
    	String service = "/api/token";
        Reporter.log("\n" + URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Host", host);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        reqSpecification = RestAssured.given().headers(headers).formParam("grant_type", "password").formParam("username", username).formParam("password", password).when();
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = reqSpecification.post(service);
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        ReportUtil.printFormattedJSON(response.asString(), service);
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 200) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }
    
    /**
     * Helper for RestAssured sign on call for Predix request
     * 
     * @param isServiceExpectedToWork
     * @return response
     */
    public Response createPredixSignOnConnectionToAPI(boolean isServiceExpectedToWork) {
    	RestAssured.baseURI = predix_UAA_URL;
    	String service = "/oauth/token";
        Reporter.log("\n" + predix_UAA_URL + service);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        reqSpecification = RestAssured.given().headers(headers).formParam("grant_type", "client_credentials").formParam("client_id", "aviation_ems_scheduled_task_svc_dev").formParam("client_secret", "3rU7l9HjXr8cUUrv").when();
        reqSpecification.log().everything();
        long startTime = System.currentTimeMillis();
        Response response = null;
        if (predix_proxy.isEmpty()) {
        	response = reqSpecification.post(service);
        } else {
        	response = reqSpecification.proxy(predix_proxy, Integer.valueOf(predix_proxy_port)).post(service);
        }
        String serviceTimeReport = new StringBuilder("\n").append(service).append(" time: ").append((System.currentTimeMillis() - startTime)).append(" milliseconds \n").toString();
        Reporter.log(serviceTimeReport);
        logger.info(serviceTimeReport);
        ReportUtil.printFormattedJSON(response.asString(), service);
        if (isServiceExpectedToWork) {
        	if (response.getStatusCode() == 200) {
        		return response;
        	} else {
                Assert.fail(response.asString());
            }
        }
        return response;
    }
    
    /**
     * Helper for RestAssured GET call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @return response
     */
    public Response createGetConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork) {
    	return createGetConnectionToAPI(request, contentType, service, isServiceExpectedToWork, false);
    }
    
    /**
     * Helper for RestAssured POST call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @param useProxy
     * @return response
     */
    public Response createPostConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork) {
    	return createPostConnectionToAPI(request, contentType, service, isServiceExpectedToWork, false);
    }

    /**
     * Helper for RestAssured DELETE call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @return response
     */
    public Response createDeleteConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork) {
    	return createDeleteConnectionToAPI(request, contentType, service, isServiceExpectedToWork, false);
    }
    
    /**
     * Helper for RestAssured PUT call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @return response
     */
	public Response createPutConnectionToAPI(Map<String, String> request, String contentType, String service, boolean isServiceExpectedToWork) {
    	return createPutConnectionToAPI(request, contentType, service, isServiceExpectedToWork, false);
    }
	
    /**
     * Helper for RestAssured PATCH call with request
     * 
     * @param request
     * @param contentType
     * @param service
     * @param isServiceExpectedToWork
     * @return response
     */
	public Response createPatchConnectionToAPI(Map<String, Object> request, String contentType, String service, boolean isServiceExpectedToWork) {
		return createPatchConnectionToAPI(request, contentType, service, isServiceExpectedToWork, false);
	}
    	
}
