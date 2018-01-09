package com.ge.ems.login.pages;

import com.ge.ems.aog.pages.BasePage;
import com.ge.ems.api.commons.PropertiesHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PredixLogIn extends BasePage {

    private By usernameLocator = By.id("username");
    private By passwordLocator = By.id("password");
    private By submitButton = By.id("submitFrm");

    public PredixLogIn(WebDriver driver){
        super(driver);
        String url = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_URL);

        visit(url);
    }

    public void logIn(){
        String username = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_USERNAME);
        String password = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_PASSWORD);

        type(username, usernameLocator);
        type(password, passwordLocator);
        click(submitButton);
    }
}
