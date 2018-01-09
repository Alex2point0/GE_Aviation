package com.ge.ems.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility for Reporting Requests/Responses
 * 
 * @author shung
 *
 */
public class ReportUtil {
	final static protected Logger logger = LoggerFactory.getLogger(ReportUtil.class);
	
	/**
	 * Formats and prints for testng reporter
	 * Also prints out to debugger 
	 * 
	 * @param input
	 * @param description
	 */
    public static void printFormattedJSON(String input, String description) {
        try {
        	ObjectMapper mapper = new ObjectMapper();
        	Object json = mapper.readValue(input, Object.class);
        	String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            description = ((description==null) ? "" : description );
            Reporter.log("=============================================================<br/>");
            Reporter.log( ((description==null) ? "" : description ) +"<br/>");
            Reporter.log("=============================================================<br/>");
            if(logger.isDebugEnabled()){
            	logger.debug(description);
            	logger.debug("\n" + indented);
            }
            Reporter.log(indented);
        } catch (Exception e) {
        	logger.warn("Exception thrown while reporting", e);
            Reporter.log(e.getMessage());
        }
    }
}


