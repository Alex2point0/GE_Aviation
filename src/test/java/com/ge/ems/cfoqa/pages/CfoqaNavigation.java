package com.ge.ems.cfoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by 212597999 on 3/7/2017.
 */
public class CfoqaNavigation extends BasePage {
    private By navigationLinks = By.cssSelector("px-app-nav-item");
    private By emsSystemPickerSpinner = By.cssSelector("main > div.style-scope.cfoqa-app div.style-scope.cfoqa-app px-spinner");
    private By geIcon = By.cssSelector("ge-svg-logo");

    public CfoqaNavigation(WebDriver driver){ super(driver); }

    public CfoqaUploadFlight getUploadFlightPage(){
        WebElement uploadFlightLink = findList(navigationLinks).get(0);
        if(isVisible(uploadFlightLink, 30)) {
            click(uploadFlightLink);
            CfoqaUploadFlight uploadFlight = new CfoqaUploadFlight(driver);

            return uploadFlight;
        }

        return null;
    }

    public CfoqaViewEvent getViewEventPage(){
        WebElement viewEventLink = findList(navigationLinks).get(1);
        if(isVisible(viewEventLink, 10)){
            clickViewEvent();
            click(geIcon);

            return new CfoqaViewEvent(driver);
        }

        return null;
    }

    public void clickUploadFlight() {
        WebElement uploadFlightLink = findList(navigationLinks).get(0);
        click(uploadFlightLink);
    }

    public void clickViewEvent() {
        WebElement viewEventLink = findList(navigationLinks).get(1);
        click(viewEventLink);
    }

    public void waitForEmsSystemLoad(){
        while(isVisible(emsSystemPickerSpinner)){ }
    }

    public boolean isUploadFlightVisible(){
        WebElement uploadFlightLink;

        try {
            uploadFlightLink = findList(navigationLinks).get(0);
        } catch (IndexOutOfBoundsException ibe){
            return false;
        }

        return isVisible(uploadFlightLink);
    }

    public boolean isViewEventVisible(){
        WebElement viewEventLink;

        try {
            viewEventLink = findList(navigationLinks).get(1);
        } catch (IndexOutOfBoundsException ibe){
            return false;
        }

        return isVisible(viewEventLink);
    }

    public boolean isConfigurationViewVisible(){
        WebElement configurationViewLink;

        try {
            configurationViewLink = findList(navigationLinks).get(2);
        } catch (IndexOutOfBoundsException ibe){
            return false;
        }

        return isVisible(configurationViewLink);
    }

    public void enterViewUploadUrl(){
        visit("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/upload-flights~/upload/explorer");
    }

    public void enterViewEventUrl(){
        visit("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/view-events~/events");
    }

    public void enterEventContextViewUrl(){
        visit("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/view-events~/events/context");
    }

    public void enterEventInteractionViewUrl(){
        visit("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/view-events~/events/context/interaction");
    }

    public void enterConfigurationViewUrl(){
        visit("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/config-view~/config");
    }

    public void enterUploadFlightUrl(){
        visit("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/upload-flights~/upload");
    }
}
