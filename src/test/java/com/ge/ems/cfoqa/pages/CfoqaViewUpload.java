package com.ge.ems.cfoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jeff.kramer on 8/21/2017.
 */
public class CfoqaViewUpload extends BasePage {

    private By uploadDataTable = By.cssSelector("upload-explorer-view iron-data-table");
    private By uploadHeaderUploadId = By.cssSelector("#popover .flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(1)");
    private By uploadHeaderDownloadId = By.cssSelector("#popover .flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(2)");
    private By uploadHeaderUploadDate = By.cssSelector(".flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(1)");
    private By uploadHeaderTailNumber = By.cssSelector(".flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(2)");
    private By uploadHeaderSyncErrors = By.cssSelector(".flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(3)");
    private By uploadHeaderValidFlights = By.cssSelector(".flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(4)");
    private By uploadHeaderComments = By.cssSelector(".flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-upload-viewer:nth-child(5)");
    //private By uploadKeyNoEvents = By.cssSelector(".status-key .flex.flex--col.flex--left.flex--top.style-scope.ems-upload-viewer:nth-child(1) p");
    private By uploadKeyEvents = By.cssSelector(".status-key .flex.flex--col.flex--left.flex--top.style-scope.ems-upload-viewer:nth-child(2) p");
    private By uploadKeyPendingReview = By.cssSelector(".status-key .flex.flex--col.flex--left.flex--top.style-scope.ems-upload-viewer:nth-child(3) p");
    private By uploadKeyInvalidFlight = By.cssSelector(".status-key .flex.flex--col.flex--left.flex--top.style-scope.ems-upload-viewer:nth-child(4) p");
    private By statusKeys = By.cssSelector(".flex.flex--row.flex--spaced.flex--top.status-key.style-scope.ems-upload-viewer>div");
    private By flightDataTableRows = By.cssSelector("ems-upload-viewer data-table-row");
    private By advancedInfoLink = By.id("advancedInfo");
    private By idPopover = By.cssSelector("#popover__wrapper");

    public CfoqaViewUpload(WebDriver driver){
        super(driver);
    }

    public String getHeaderUploadId(){
        if(!isVisible(idPopover) && isVisible(advancedInfoLink, 2)){
            click(advancedInfoLink);
        }
        return processHeaderText(text(uploadHeaderUploadId));
    }

    public String getHeaderDownloadId(){
        if(!isVisible(idPopover) && isVisible(advancedInfoLink, 2)){
            click(advancedInfoLink);
        }
        return processHeaderText(text(uploadHeaderDownloadId));
    }

    public String getHeaderUploadDate(){ return processHeaderText(text(uploadHeaderUploadDate)); }

    public String getHeaderTailNumber(){
        return processHeaderText(text(uploadHeaderTailNumber));
    }

    public String getHeaderSyncErrors(){ return processHeaderText(text(uploadHeaderSyncErrors)).replace("%%", "%"); }

    public String getHeaderValidFlights(){
        return processHeaderText(text(uploadHeaderValidFlights));
    }

    public String getHeaderComments(){
        return processHeaderText(text(uploadHeaderComments));
    }

    public String getKeyNoEventsText(){
        WebElement uploadKeyNoEvents = findList(statusKeys).get(3).findElement(By.cssSelector("div"));
        return text(uploadKeyNoEvents);
    }

    public String getKeyNoEventsColor(){
        WebElement icon = findList(statusKeys).get(3).findElement(By.cssSelector("i"));
        return icon.getCssValue("color");
    }

    public String getKeyEventsText(){
        WebElement uploadKeyEvents = findList(statusKeys).get(0).findElement(By.cssSelector("div"));
        return text(uploadKeyEvents);
    }

    public String getKeyEventsColor(){
        WebElement icon = findList(statusKeys).get(0).findElement(By.cssSelector("i"));
        return icon.getCssValue("color");
    }

    public String getKeyPendingReviewText(){
        WebElement uploadKeyPendingReview = findList(statusKeys).get(1).findElement(By.cssSelector("div"));
        return text(uploadKeyPendingReview);
    }

    public String getKeyPendingReviewColor(){
        WebElement icon = findList(statusKeys).get(1).findElement(By.cssSelector("i"));
        return icon.getCssValue("color");
    }

    public String getKeyInvalidFlightText(){
        WebElement uploadKeyInvalidFlight = findList(statusKeys).get(2).findElement(By.cssSelector("div"));
        return text(uploadKeyInvalidFlight);
    }

    public String getKeyInvalidFlightColor(){
        WebElement icon = findList(statusKeys).get(2).findElement(By.cssSelector("i"));;
        return icon.getCssValue("color");
    }

    public Boolean waitForFlightData(){
        return isVisible(uploadDataTable, 60);
    }

    public Boolean waitForStatusKey(){ return isVisible(uploadHeaderUploadDate, 15); }

    public HashMap<String, String[]> getFlightData(){
        List<WebElement> flightRows = findList(flightDataTableRows);
        HashMap<String, String[]> flightDataMap = new HashMap<>();
        String[] flightDataArray = new String[5];
        flightRows.remove(0); //Remove header row

        for(WebElement flight : flightRows){
            List<WebElement> flightDataCells = flight.findElements(By.cssSelector("data-table-cell"));
            flightDataArray[0] = text(flightDataCells.get(2)); //Flight Date
            flightDataArray[1] = text(flightDataCells.get(3)).replace(">", "-"); //Airport city pair - Slight modification required to match styling from API
            flightDataArray[2] = text(flightDataCells.get(4)); //Hours
            flightDataArray[3] = text(flightDataCells.get(5)); //Note
            flightDataArray[4] = getAttributeValue(flightDataCells.get(0).findElement(By.tagName("i")), "class");

            String flightId = text(flightDataCells.get(1));

            flightDataMap.put(flightId, flightDataArray);
        }

        return flightDataMap;
    }

    private String processHeaderText(String text){
        return text.substring(text.indexOf("\n") + 1);
    }
}
