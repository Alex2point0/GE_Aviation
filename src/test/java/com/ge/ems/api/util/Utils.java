package com.ge.ems.api.util;

import java.io.*;
import java.util.*;

import com.ge.ems.api.commons.PropertiesHandler;
import com.ge.ems.api.rest.assured.request.TokenRequest;
import com.ge.ems.api.rest.assured.request.databaseAPIs.DatabaseAsyncQueryGetRequest;
import com.ge.ems.api.rest.assured.request.databaseAPIs.DatabaseAsyncQueryPostRequest;
import com.ge.ems.api.rest.assured.request.databaseAPIs.DatabaseQueryPostRequest;
import com.opencsv.CSVReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * General Utility Class
 * 
 * @author shung
 *
 */
public class Utils {
	
	public static Object TOKEN;
	protected final static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * Helper to convert JSON Object into Java Object
     * 
     * @param jsonString
     * @return JSONObject
     */
    public static JSONObject turnJSONToObject(String jsonString) {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(jsonString);
        } catch (Exception e) {
            Assert.fail("Failed to convert JSON to Object - " + e, e);
        }
        return json;
    }
    
    /**
     * Helper to convert JSON Array into Java Object
     * 
     * @param jsonString
     * @return JSONArray
     */
    public static JSONArray turnJSONArrayToObject(String jsonString) {
        JSONArray json = null;
        try {
            json = (JSONArray) new JSONParser().parse(jsonString);
        } catch (Exception e) {
            Assert.fail("Failed to convert JSON to Object", e);
        }
        return json;
    }     
   
    /**
     * Generate a random name using the name parameter
     *
     * @return "name" + Random number
     */
    public static String randomName() {
    	Random random = new Random();
    	return "name" + random.nextInt();
    }
    
    /**
     * Determine if a String is an Integer
     *
     * @param s
     * @return boolean
     */
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }

        return true;
    }
    
    /**
     * Convert FileInputStream to a String
     *
     * @param f
     * @return String
     */
    public static String fileToString(FileInputStream f) throws IOException {
    	StringBuilder builder = new StringBuilder();
    	int ch;
    	while((ch = f.read()) != -1){
    	    builder.append((char)ch);
    	}
    	return builder.toString();
    }

    /**
     * Method that reads a CSV data file created
     *
     * @param f - File to be read
     * @return List<String[]> where each element in the list is a different row from the CSV file
     */
    public static List<String[]> readCSVDataFile(File f){
        List<String[]> data = null;

        try {
            CSVReader reader = new CSVReader(new FileReader(f));
            //CSVReader reader = new CSVReader(new FileReader("C:\\Users\\212597999\\Downloads\\ems-csv-2017-02-23_11-43-07-pm.csv"));
            data = reader.readAll();
            data.remove(0);
            reader.close();
        } catch (FileNotFoundException fnfe){
            Assert.fail("Datafile Not Found - " + fnfe);
        } catch (IOException ioe) {
            Assert.fail("Reader Close Failed - " + ioe);
        }catch (Exception ex){
            Assert.fail(ex.getMessage());
        }

        File rename = new File(com.ge.ems.api.commons.PropertiesHandler.properties.getProperty(com.ge.ems.api.commons.PropertiesHandler.DOWNLOAD_DIRECTORY) + "processed-" + Utils.FILE.getName());
        //File rename = new File(PropertiesHandler.DIRECTORY + "processed-" + Utils.FILE.getName());
        assertTrue(Utils.FILE.renameTo(rename));

        return data;
    }

    /**
     * Method to process the data returned from the API query for comparison
     * @return List<String[]> where each element in the list is a different row from the API Query
     */
    public static List<String[]> readWDCQuery(List<JSONArray> results){
        List<String[]> data = new ArrayList<>();
        int size = results.size();

        for(JSONArray result : results){
            String[] holder = new String[size];
            int i = 0;
            for(Object element : result){
                holder[i] = element.toString();
                i++;
            }
            data.add(holder);
        }
        return data;
    }

    /**
     * Sorts a list of String arrays by one of the elements in the array
     * @param list - List of String arrays to be sorted
     * @param index - The index of the element in the String array that will be used as the basis for comparison
     */
    public static void sortStringArrayListByElement(List<String[]> list, int index){
        Collections.sort(list, new Comparator<String[]>(){
            @Override
            public int compare(String[] s1, String[] s2){ return s1[index].compareTo(s2[index]); }
        });
    }

    /**
     * Creates a Token to be used when making an API call
     */
    public static void getToken(){
        if(Utils.TOKEN == null){
            String username = PropertiesHandler.properties.getProperty(PropertiesHandler.USERNAME);
            String password = PropertiesHandler.properties.getProperty(PropertiesHandler.PASSWORD);
            TokenRequest.getTokenResponse(username, password, true);
        }
    }

    /**
     * Returns a list of JSONArray objects storing the response data from the API query stored in the File object
     *  passed to the method.
     *
     * @param queryFile - A File object containing the path to the text file containing the query to be run
     * @return - List of JSONArray objects containing the data from the API query
     */
    public static List<JSONArray> queryForData(File queryFile, Boolean asyncQuery){
        return queryForData(queryFile, asyncQuery, null, null, null);
    }

    public static List<JSONArray> queryForData(File queryFile, Boolean asyncQuery, String databaseId){
        return queryForData(queryFile, asyncQuery, null, null, databaseId);
    }

    public static List<JSONArray> queryForData(File queryFile, Boolean asyncQuery, String replaceText, String replaceValue){
        return queryForData(queryFile, asyncQuery, replaceText, replaceValue, null);
    }

    /**
     * Sends the initial API query call and returns the response from the call
     *
     * @param queryFile - A File object containing the path to the text file storing the query to run
     * @return - A JSONObject containing the response from the API query call
     */
    public static List<JSONArray> queryForData(File queryFile, Boolean asyncQuery, String replaceText, String replaceValue, String databaseId){
        getToken();
        FileInputStream fis;
        String queryString;
        JSONObject response = new JSONObject();
        List<JSONArray> dataset = null;

        if(databaseId == null){
            databaseId = PropertiesHandler.properties.getProperty(PropertiesHandler.DATABASE_ID);
        }

        String emsSystemId = PropertiesHandler.properties.getProperty(PropertiesHandler.EMS_SYSTEM_ID);

        try {
            fis = new FileInputStream(queryFile);
            queryString = Utils.fileToString(fis);

            if(replaceText != null){
                queryString = queryString.replace(replaceText, replaceValue);
            }

            if(!asyncQuery){
                response = DatabaseQueryPostRequest.getDatabaseQueryPostResponse(emsSystemId, databaseId, queryString, true);
                dataset = (JSONArray)response.get("rows");
            } else {
                response = DatabaseAsyncQueryPostRequest.getDatabaseAsyncQueryPostResponse(emsSystemId, databaseId, queryString, true);
                dataset = retrieveAsyncData(response);
            }

        } catch (FileNotFoundException fnfe){
            junit.framework.Assert.fail("API query file not found - " + fnfe.getMessage());
        } catch (IOException ioe){
            junit.framework.Assert.fail("Error converting query file to string - " + ioe.getMessage());
        }

        assertNotNull(response);

        return dataset;
    }

    /**
     * Returns a list of JSONArray objects containing the data returns from the response created by a API query call
     *
     * @param response - A JSONObject containing the response from the initial API query call
     * @return - List of JSONArray objects containing the data from the API query call
     */
    @SuppressWarnings("unchecked")
	private static List<JSONArray> retrieveAsyncData(JSONObject response) {
        String queryId = (String) response.get("id");
        String emsSystemId = PropertiesHandler.properties.getProperty(PropertiesHandler.EMS_SYSTEM_ID);
        String databaseId = PropertiesHandler.properties.getProperty(PropertiesHandler.DATABASE_ID);
        List<JSONArray> results = new ArrayList<>();

        do {
            response = DatabaseAsyncQueryGetRequest.getDatabaseAsyncQueryGetResponse(emsSystemId, databaseId, queryId, "0", "100", true);

            assertNotNull(response);

            results.addAll((JSONArray) response.get("rows"));
        } while (Boolean.valueOf(response.get("hasMoreRows").toString()));

        return results;
    }

    /*SELENIUM UTILITIES*/

    public static WebDriver DRIVER;
    public static File FILE;
    public static String API_QUERY;
    public static JSONObject TEST_DEFINITION;

    /**
     * Puts the thread to sleep for the amount of time passed in
     *
     * @param ms - The time, in milliseconds, that the thread should sleep for
     */
    public static void threadSleep(int ms){
        try{
            Thread.sleep(ms);
        } catch (Exception ex){
            logger.debug("Error putting thread to sleep - " + ex.getMessage());
        }
    }

    /**
     * Method to double click a given element
     * @param element - The element to be double clicked
     */
    public static void doubleClick(WebDriver driver, WebElement element) {
        Actions action = new Actions(driver).doubleClick(element);
        action.build().perform();
    }

    /**
     * Method that causes the script to wait until the element with the passed link test exists
     * @param text - the value of the link text that the script is waiting for
     */
    public static void waitByLinkText(WebDriver driver, String text){
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(text)));
    }

    /**
     * Method that causes the script to wait until the element with the passed id attribute has been loaded
     * @param id - the value of the id attribute that the script is waiting for
     */
    public static void waitById(WebDriver driver, String id){
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
    }

    /**
     * Method that causes the script to wait until the element with the passed element tag has been loaded
     * @param tag - the name of the tag (i.e. div, a ) that the script is waiting for
     */
    public static void waitByTag(WebDriver driver, String tag) {
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.elementToBeClickable(By.tagName(tag)));
    }

    /**
     * Method that causes the script to wait until a certain element has been loaded
     * @param name - the name of the class that the script is waiting for
     *
     */
    public static void waitByClass(WebDriver driver, String name){
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.elementToBeClickable(By.className(name)));
    }

    /**
     * Parses a JSON file returning a JSONObject containing the information from the file
     *
     * @param file - JSON file to be parsed
     * @return JSONObject containing the information from the JSON file
     */
    public static void parseJSONFile(String file){
        JSONParser parser = new JSONParser();
        JSONObject jo = new JSONObject();

        //Parse the field definition JSON file into a JSONObject
        try {
            jo = (JSONObject) parser.parse(new FileReader(file));

        } catch (FileNotFoundException fnfe) {
            Assert.fail("JSON file not found - " + fnfe);
        } catch(ParseException pe){
            Assert.fail("Error parsing JSON file - " + pe);
        } catch(Exception ex){
            Assert.fail("Error processing JSON file - " + ex);
        }

        TEST_DEFINITION = jo;
    }
}
