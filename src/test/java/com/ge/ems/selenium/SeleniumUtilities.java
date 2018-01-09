package com.ge.ems.selenium;

import com.ge.ems.aog.pages.AogLogIn;
import com.ge.ems.api.commons.PropertiesHandler;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 212597999 on 3/10/2017.
 */
public class SeleniumUtilities {

    protected static final Logger logger = LoggerFactory.getLogger(SeleniumUtilities.class);

    /**
     * Takes a screen shot and saves it to the desired location
     *
     * @param driver - The WebDriver for the browser the screen shot will be taken for
     * @param browser - The browser that the screen shot will be taken for
     * @param fileName - The desired file name of the file name resulting from the screen shot
     */
    public static void takeScreenShot(WebDriver driver, String browser, String fileName){
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String screenshotDirectory = PropertiesHandler.properties.getProperty(PropertiesHandler.SCREENSHOT_DIRECTORY);
        File sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String pathName = screenshotDirectory + "\\" + browser + "\\" + fileName + "_" + timeStamp + ".png";

        logger.info("Creating file - " + pathName);
        try {
            FileUtils.copyFile(sourceFile, new File(pathName));
        } catch (IOException ioe){
            logger.error("Error creating screen shot - Browser: " + browser + " - File: " + fileName);
        }
    }

    /**
     * Logs a user into a predix application using the users SSO username and password
     *
     * @param driver - WebDriver for the browser that the test is being run against
     */
    public static void predixLogin(WebDriver driver){
        String url = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_URL);
        driver.get(url);

        AogLogIn aogLogin = new AogLogIn(driver);

        String username = "NGQXI6T";
        String password = "e123456t";

        /*String username = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_USERNAME);
        String password = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_PASSWORD);*/

        aogLogin.logIn(username, password);
    }

    public static void refreshPage(WebDriver driver){
        driver.navigate().refresh();
    }
    
    public static WebDriver openInIPadMode() {
    	Map<String, String> mobileEmulation = new HashMap<String, String>();
    	mobileEmulation.put("deviceName", "iPad");

    	Map<String, Object> chromeOptions = new HashMap<String, Object>();
    	chromeOptions.put("mobileEmulation", mobileEmulation);
    	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    	capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return new ChromeDriver(capabilities);
    }
}
