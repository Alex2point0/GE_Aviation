package com.ge.ems.cfoqa.tests;

import com.ge.ems.api.commons.PropertiesHandler;
import com.ge.ems.cfoqa.pages.CfoqaNavigation;
import com.ge.ems.login.pages.EfoqaLogin;
import com.ge.ems.selenium.LocalDriverFactory;
import com.ge.ems.selenium.LocalDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.util.Arrays;

public class CfoqaTest {
    public boolean firstTest = true;

    public WebDriver driver;
    public CfoqaNavigation nav;
    public String username = PropertiesHandler.properties.getProperty(PropertiesHandler.EFOQA_USERNAME);;
    public String password = PropertiesHandler.properties.getProperty(PropertiesHandler.EFOQA_PASSWORD);;

    public static final Logger logger = LoggerFactory.getLogger(CfoqaTest.class);

    @BeforeClass
    @Parameters("browser")
    public void beforeClass(String browser){
        LocalDriverManager.setWebDriver(LocalDriverFactory.createInstance(browser));
        driver = LocalDriverManager.getDriver();

        EfoqaLogin efoqaLogin = new EfoqaLogin(driver);
        efoqaLogin.logIn(username, password);

        nav = new CfoqaNavigation(driver);

        nav.waitForEmsSystemLoad();
    }

    @AfterClass
    public void afterClass(){
        driver.quit();
    }

    @AfterMethod
    public void AfterMethod(ITestResult testResult){
        try {
            if (testResult.getStatus() == ITestResult.FAILURE) {
                logger.debug("Taking screen capture of screen at the time of test failure");
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, new File("C:\\users\\somoene\\" + testResult.getName() + "-" + Arrays.toString(testResult.getParameters()) + ".jpg"));
            }
        } catch (Exception ex){

        }
    }
}
