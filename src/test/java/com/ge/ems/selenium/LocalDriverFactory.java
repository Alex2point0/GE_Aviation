package com.ge.ems.selenium;

import com.ge.ems.api.commons.PropertiesHandler;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LocalDriverFactory {
    protected static final Logger logger = LoggerFactory.getLogger(LocalDriverFactory.class);

    /**
     * Creates an WebDriver instances based on the browser value passed into the method.
     *
     * @param browser - A String value representing the driver that needs to be made.  For a Chrome browser pass "chrome".  For a Firefox browser pass "firefox"
     * @return The WebDriver
     */
    public static WebDriver createInstance(String browser){
        WebDriver driver = null;
        browser = browser.toLowerCase();

        //logger.info("Creating a " + browser + " WebDriver.");

        //Sets the dimensions of the window.  There were some issues with tests failing if elements were off the screen so this was done to remedy that.
        //Dimension dimension = new Dimension(1250, 1000);
        Dimension dimension = new Dimension(1044, 788);

        //Creates and defines a WebDriver for a Google Chrome browser
        if(browser.equals("chrome")){
            logger.info("Starting chrome browser");
            String chromeDriver = PropertiesHandler.properties.getProperty(PropertiesHandler.CHROME_DRIVER_DIRECTORY);

            System.setProperty("webdriver.chrome.driver", chromeDriver);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("disable-infobars");
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOption("prefs", prefs);

            driver = new ChromeDriver(options);
            driver.manage().window().setSize(dimension);
            //driver.manage().window().maximize();
            logger.info("Browser size: H: " + driver.manage().window().getSize().getHeight() + " W: " + driver.manage().window().getSize().getWidth());
            return driver;
        }

        /*
        * Creates and defines a WebDriver for a Mozilla Firefox browser.
        * The latest successful build of the test suite requires using Firefox 48 and Gecko Driver 14.
        * Different versions of the browser/driver combinations don't work with Selenium.
        */
        if(browser.equals("firefox")) {
            logger.info("Starting firefox browser");
            //Build works with Firefox 48 and Gecko Driver 14
            String geckoDriver = PropertiesHandler.properties.getProperty(PropertiesHandler.GECKO_DRIVER);
            String firefoxBinary = PropertiesHandler.properties.getProperty(PropertiesHandler.FIREFOX_BINARY);

            System.setProperty("webdriver.gecko.driver", geckoDriver);
            File pathToBinary = new File(firefoxBinary);
            FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
            FirefoxProfile ffProfile = new FirefoxProfile();
            DesiredCapabilities dc = DesiredCapabilities.firefox();
            dc.setCapability("marionette", true);
            driver = new FirefoxDriver(ffBinary, ffProfile, dc);
            driver.manage().window().setSize(dimension);
            return driver;
        }

        return driver;
    }
}
