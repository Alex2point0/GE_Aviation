package com.ge.ems.cfoqa.pages;

import com.ge.ems.api.util.Utils;
import com.ge.ems.cfoqa.Util.ViewEventResultCompartor;
import com.ge.ems.cfoqa.Util.ViewEventResultRow;
import com.sun.glass.events.ViewEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;

import javax.swing.text.View;
import java.util.*;

public class CfoqaViewEvent extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CfoqaViewEvent.class);

    private By elementTag = By.cssSelector("px-view#event-view");
    private By tailInput = By.cssSelector("#tails #input");
    private By eventInput = By.cssSelector("#events #input");
    private By downloadDateInput = By.cssSelector("#datemodal a");
    private By vaadinComboBoxItem = By.cssSelector("vaadin-combo-box-item");
    private By loadingSpinner = By.cssSelector("#event-view px-spinner");
    private By eventDataRow = By.cssSelector("ems-event-search iron-list .item.style-scope.iron-data-table");
    private By eventDataCell = By.cssSelector("data-table-cell");
    private By viewEventButton = By.cssSelector(".btn.btn--primary.u-m.style-scope.event-view");

    private By calFromHeader = By.cssSelector("#from>div>div:nth-child(1) span");
    private By calFromCells = By.cssSelector("#from px-calendar-cell");
    private By calToHeader = By.cssSelector("#to>div>div:nth-child(1) span");
    private By calToCells = By.cssSelector("#to px-calendar-cell");
    private By calOkayButton = By.cssSelector("#btnModalPositive");

    //private ArrayList<ViewEventResultRow> searchResults = new ArrayList<>();

    public CfoqaViewEvent(WebDriver driver){
        super(driver);
    }

    public Boolean isPageLoaded(){
        return isVisible(elementTag, 10);
    }

    public void waitForPageLoad(){
        int i = 1;
        while(isVisible(loadingSpinner, 1)){
            Utils.threadSleep(1000);
            logger.info("Waited " + i + " second(s) for search results on View Event page to load.");
            if(i == 60){
                Assert.fail("Search results on the View Event page took longer than 60 seconds to load.");
            }
            i++;
        }
    }

    public void enterTailNumber(String tailNumber){
        if(isVisible(tailInput, 2)) {
            type(tailNumber, tailInput);
            click(vaadinComboBoxItem);
        }

        waitForPageLoad();
    }

    public void enterEvent(String event){
        Utils.threadSleep(500);
        if(isVisible(eventInput, 2)){
            type(event, eventInput);
            if(isVisible(vaadinComboBoxItem, 2)) {
                click(vaadinComboBoxItem);
            } else {
                Assert.fail("Vaadin combo box did not show up when entering an event.");
            }
        }

        waitForPageLoad();
    }

    public void enterDownloadDateRange(String startYear, String startMonth, String startDate, String endYear, String endMonth, String endDate){
        String[] startData = {startYear, startMonth.toUpperCase(), startDate};
        String[] endData = {endYear, endMonth.toUpperCase(), endDate};

        if(isVisible(downloadDateInput, 5)) {
            click(downloadDateInput);
            List<WebElement> calDataCells;

            click(find(calFromHeader));
            click(calFromHeader);
            for(String data : startData) {
                calDataCells = findList(calFromCells);
                for (WebElement dataCell : calDataCells) {
                    if (text(dataCell).equals(data)) {
                        click(dataCell);
                        break;
                    }
                }
            }

            click(find(calToHeader));
            click(find(calToHeader));
            for(String data : endData) {
                calDataCells = findList(calToCells);
                for (WebElement dataCell : calDataCells) {
                    if (text(dataCell).equals(data)) {
                        click(dataCell);
                        break;
                    }
                }
            }

            click(calOkayButton);

        } else {
            Assert.fail("Download date link was not visible on the View Event page after 5 seconds.");
        }

        waitForPageLoad();
    }

    public LinkedList<String> getTailNumbers(){
        LinkedList<String> tailNumberList = new LinkedList<>();
        LinkedList<ViewEventResultRow> searchResults = getSearchResults();

        for(ViewEventResultRow row : searchResults){
            tailNumberList.add(row.getTailNumber());
        }

        return tailNumberList;
    }

    public LinkedList<String> getEvents(){
        LinkedList<String> eventList = new LinkedList<>();
        LinkedList<ViewEventResultRow> searchResults = getSearchResults();

        for(ViewEventResultRow row : searchResults){
            eventList.add(row.getEvent());
        }

        return eventList;
    }

    public LinkedList<String> getDownloadDates(){
        LinkedList<String> downloadDateList = new LinkedList<>();
        LinkedList<ViewEventResultRow> searchResults = getSearchResults();

        for(ViewEventResultRow row : searchResults){
            downloadDateList.add(row.getCollectionDate());
        }

        return downloadDateList;
    }

    public LinkedList<ViewEventResultRow> getEventSearchResults(){
        LinkedList<ViewEventResultRow> searchResults = getSearchResults();

        Collections.sort(searchResults, new ViewEventResultCompartor());

        return searchResults;
    }

    public CfoqaEventContextView viewEvent(int index){
        clickDataRow(index);
        scrollTo(viewEventButton);
        if(isClickable(viewEventButton, 2)) {
            click(viewEventButton);
        } else {
            Assert.fail("View Event button not clickable after waiting for 2 seconds.");
        }

        return new CfoqaEventContextView(driver);
    }

    /**
     * Retrieves all of the data from the search results and stores them for use by other methods in the class.
     */
    private LinkedList<ViewEventResultRow> getSearchResults(){
        boolean eod = false;
        String skipStyle = "transform: translate3d(0px, -10000px, 0px);";
        HashSet<String> processedElements = new HashSet<>();
        LinkedList<ViewEventResultRow> searchResults = new LinkedList<>();

        int dataSize = findList(eventDataRow).size();

        int i = 0;
        while(!eod){
            int j = i % dataSize;
            int k = (i + 1) % dataSize;
            clickDataRow(j);
            String style = getAttributeValue(findList(eventDataRow).get(j), "style");
            if(!style.equals(skipStyle) && processedElements.add(style)){
                searchResults.add(getEventRow(j));
            }

            WebElement nextRow = findList(eventDataRow).get(k);
            String nextStyle = getAttributeValue(nextRow, "style");
            String nextHidden = getAttributeValue(nextRow, "hidden");
            if(processedElements.contains(nextStyle) || nextHidden != null){
                eod = true;
            }

            i++;
        }

        return searchResults;
    }

    public ViewEventResultRow getEventRow(int index){
        Assert.assertTrue(isVisible(eventDataRow, 10), "The search results on the View Event page did not load.");

        WebElement dataRow = findList(eventDataRow).get(index);
        List<WebElement> dataCells = dataRow.findElements(eventDataCell);

        Integer flightId = Integer.parseInt(text(dataCells.get(0)));
        String downloadDate = text(dataCells.get(1));
        String event = text(dataCells.get(2));
        String tailNumber = text(dataCells.get(3));

        return new ViewEventResultRow(flightId, downloadDate, event, tailNumber);
    }

    private void clickDataRow(int row){
        if(isVisible(eventDataRow, 1)){
            List<WebElement> dataRows = findList(eventDataRow);
            WebElement targetRow = dataRows.get(row);
            click(targetRow);
        } else {
            Assert.fail("The rows of event data are not visible on the page.");
        }
    }
}
