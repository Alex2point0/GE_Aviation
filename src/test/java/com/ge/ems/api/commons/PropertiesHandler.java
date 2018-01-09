package com.ge.ems.api.commons;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Loads the properties files defined by VM argument 'propertiesFile=XXX.properties'
 *  
 * 
 * @author shung
 *
 */
public final class PropertiesHandler{
		
	//General
	public static final String ENVIRONMENT = "runtime.environment";
	public static final String SERVICE_URL = "services.url";
	public static final String USERNAME = "test.username";
	public static final String PASSWORD = "test.password";
	public static final String CLIENTID = "test.clientId";
	public static final String ID = "test.id";
	public static final String DOWNLOAD_DIRECTORY = "download.directory";
	public static final String EMS_SYSTEM_ID = "ems.system.id";
	public static final String EMS_SYSTEM = "ems.system";
	public static final String DATABASE_ID = "database.id";
	public static final String TEST_DEFINITION = "test.definition";
	public static final String UAA_URL = "uaa.url";
	public static final String PREDIX_PROXY = "predix.proxy";	
	public static final String PREDIX_PROXY_PORT = "predix.proxy.port";	
	
	//DB
	public static final String SQL_SERVER = "sql.server";
	public static final String SQL_USERNAME = "sql.username";
	public static final String SQL_PASSWORD = "sql.password";

	//Predix
	public static final String PREDIX_USERNAME = "predix.username";
	public static final String PREDIX_PASSWORD = "predix.password";
	public static final String PREDIX_URL = "predix.url";

	//eFoqa
	public static final String EFOQA_USERNAME = "efoqa.username";
	public static final String EFOQA_PASSWORD = "efoqa.password";
	public static final String DATA_COLLECTOR_USERNAME = "data.collector.username";
	public static final String DATA_COLLECTOR_PASSWORD = "data.collector.password";

	//Selenium
	public static final String CHROME_DRIVER_DIRECTORY = "chrome.driver.directory";
	public static final String GECKO_DRIVER = "gecko.driver";
	public static final String FIREFOX_BINARY = "firefox.binary";
	public static final String SCREENSHOT_DIRECTORY = "screenshot.directory";

	//Device
	public static final String APP = "device.app";
	public static final String DEVICE_NAME = "device.deviceName";
	public static final String PLATFORM_NAME = "device.platformName";
	public static final String PLATFORM_VERSION = "device.platformVersion";
	public static final String UDID = "device.udid";
	public static final String APP_VERSION = "app.version";

	//RAT
	public static final String EMS_TEST_USERNAME = "ems.test.username";
	public static final String EMS_TEST_PASSWORD = "ems.test.password";
	public static final String EMS_ADMIN_PASSWORD = "ems.admin.password";

	public static Properties properties = new Properties();
    
    public static String fileName = "src/test/resources/" + System.getProperty("propertiesFile");

	final static private Logger logger = LoggerFactory.getLogger(PropertiesHandler.class);
	
    static {
        FileInputStream fIS;
        try {
            fIS = new FileInputStream(fileName.trim());
            properties.load(fIS);
            fIS.close();
        } catch (Exception e) {
        	logger.error("Exception thrown while loading properties.  Check to see if you have vm argument 'propertiesFile'.  Ex. '-DpropertiesFile=LOCAL.properties'",e);
        	Assert.fail(e.getMessage());
        }
    }
}
