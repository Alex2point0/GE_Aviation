package com.ge.ems.login.pages;


import com.ge.ems.api.commons.PropertiesHandler;
import com.ge.ems.cfoqa.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EfoqaLogin extends BasePage {

    public EfoqaLogin(WebDriver driver){
        super(driver);
        String url = PropertiesHandler.properties.getProperty(PropertiesHandler.PREDIX_URL);

        visit(url);
    }

    private By usernameInput = By.id("userNameInput");
    private By passwordInput = By.id("passwordInput");
    private By submitButton = By.id("submitButton");

    public void logIn(String username, String password){
        type(username, usernameInput);
        type(password, passwordInput);
        click(submitButton);
    }
}
