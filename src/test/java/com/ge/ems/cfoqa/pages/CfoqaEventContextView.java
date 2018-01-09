package com.ge.ems.cfoqa.pages;

import com.ge.ems.api.util.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.LinkedList;
import java.util.List;

public class CfoqaEventContextView extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CfoqaEventContextView.class);

    public CfoqaEventContextView(WebDriver driver){
        super(driver);
    }

    private By loadingSpinner = By.cssSelector("#event-context-view px-spinner svg");
    private By flightInformation = By.cssSelector("#event-context-view px-card:nth-child(1) .flex.flex--col.flex--left.flex--top.u-p.style-scope.ems-event-context-view");
    private By eventInput = By.cssSelector("#event-context-view input");
    private By eventInputOptions = By.cssSelector("vaadin-combo-box-item");
    private By eventInfoTableRows = By.cssSelector("#event-context-view tr");
    private By tableData = By.cssSelector("td");
    private By interactButton = By.cssSelector(".btn.btn--primary.u-m.style-scope.event-context-view");

    public void waitForFlightInformationLoad(){
        WebElement flightInformationSpinner = null;

        if(isVisible(loadingSpinner, 1)) {
            flightInformationSpinner = findList(loadingSpinner).get(0);
        } else {
            Assert.fail("Loading spinner for flight information not visible on Event Context View.");
        }

        int i = 0;
        while(isVisible(flightInformationSpinner)){
            if(i > 60){
                Assert.fail("Search results on the View Event page took longer than 60 seconds to load.");
            }
            Utils.threadSleep(1000);
            logger.info("Waited approximately " + i + " second(s) for flight information on Event Context page to load.");
            i++;
        }

        logger.info("Flight information loaded.");
    }

    public void waitForEventInformationLoad(){
        WebElement eventInformationSpinner = null;

        if(isVisible(loadingSpinner, 1)) {
            eventInformationSpinner = findList(loadingSpinner).get(0);
        } else {
            Assert.fail("Loading spinner for flight information not visible on Event Context View.");
        }

        int i = 0;
        while(isVisible(eventInformationSpinner)){
            if(i > 60){
                Assert.fail("Search results on the View Event page took longer than 60 seconds to load.");
            }
            Utils.threadSleep(1000);
            logger.info("Waited approximately " + i + " second(s) for event information on Event Context page to load.");
            i++;
        }

        logger.info("Event information loaded.");
    }

    public void waitForEventMeasurementsLoad(){
        WebElement eventMeasurementSpinner = findList(loadingSpinner).get(2);
        isVisible(eventMeasurementSpinner, 2);

        int i = 1;
        while(isVisible(eventMeasurementSpinner, 1)){
            if(i > 60){
                Assert.fail("Search results on the View Event page took longer than 60 seconds to load.");
            }
            Utils.threadSleep(1000);
            logger.info("Waited approximately " + i + " second(s) for event specific measurements on Event Context page to load.");
            i++;
        }

        logger.info("Event specific measurements loaded.");
    }

    public String getFlightId(){
        return getFlightInformation(0);
    }

    public String getAircraft(){
        return getFlightInformation(1);
    }

    public String getFleet(){
        return getFlightInformation(2);
    }

    public String getDate(){
        return getFlightInformation(3);
    }

    public String getTime(){
        return getFlightInformation(4);
    }

    public String getAirportRunway(){
        return getFlightInformation(5);
    }

    public String getSelectedEvent(){
        return getAttributeValue(find(eventInput), "value");
    }

    public String getEventSeverity(){
        WebElement eventSeverityElement = findList(eventInfoTableRows).get(0);
        List<WebElement> eventSeverityData = eventSeverityElement.findElements(tableData);
        return text(eventSeverityData.get(1));
    }

    public LinkedList<String[]> getEventMeasurements(){
        LinkedList<String[]> eventMeasurementList = new LinkedList<>();
        List<WebElement> eventMeasurementRows = findList(eventInfoTableRows);
        eventMeasurementRows.remove(0);

        List<WebElement> eventMeasurementData;

        for(WebElement eventMeasurementRow : eventMeasurementRows){
            eventMeasurementData = eventMeasurementRow.findElements(tableData);
            String measurementName = text(eventMeasurementData.get(0));
            String measurementValue = text(eventMeasurementData.get(1));
            eventMeasurementList.add(new String[]{measurementName, measurementValue});
        }

        return eventMeasurementList;
    }

    public LinkedList<String> getEventList(){
        click(eventInput);

        List<WebElement> eventOptions = findList(eventInputOptions);
        LinkedList<String> eventList = new LinkedList();

        for(WebElement event : eventOptions){
            String eventText = text(event);
            if(eventText.length() > 0){
                eventList.add(eventText);
            }
        }

        return eventList;
    }

    public CfoqaEventInteractionView clickInteractButton(){
        if(isClickable(interactButton, 10)) {
            scrollTo(interactButton);
            click(interactButton);
        } else {
            Assert.fail("Interact Button on the Event Context View page cannot be clicked after 10 waiting seconds.");
        }

        return new CfoqaEventInteractionView(driver);
    }

    private String getFlightInformation(int index){
        WebElement element = findList(flightInformation).get(index);
        String text = text(element);
        int newline = text.indexOf("\n") + 1;
        return text.substring(newline);
    }
}
