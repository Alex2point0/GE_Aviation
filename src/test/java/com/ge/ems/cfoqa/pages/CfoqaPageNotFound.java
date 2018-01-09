package com.ge.ems.cfoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by jeff.kramer on 10/31/2017.
 */
public class CfoqaPageNotFound extends BasePage {

    private By pageNotFound = By.id("page-not-found-view");

    public CfoqaPageNotFound(WebDriver driver){
        super(driver);
    }

    public boolean isPageNotFoundVisible(){
        String status = getAttributeValue(find(pageNotFound), "status");

        if(status.equals("hidden")){
            return false;
        } else {
            return true;
        }
    }
}
