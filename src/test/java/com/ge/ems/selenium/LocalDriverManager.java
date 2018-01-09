package com.ge.ems.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Created by 212597999 on 3/10/2017.
 */
public class LocalDriverManager {
    private static ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    public static WebDriver getDriver(){
        return webDriver.get();
    }

    public static void setWebDriver(WebDriver driver){
        webDriver.set(driver);
    }
}
