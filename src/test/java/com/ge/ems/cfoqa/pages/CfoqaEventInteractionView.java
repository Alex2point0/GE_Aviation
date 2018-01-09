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

public class CfoqaEventInteractionView extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CfoqaEventInteractionView.class);

    public CfoqaEventInteractionView(WebDriver driver){
        super(driver);
    }

    private By fdvLoadingSpinner = By.cssSelector("#event-interaction-view #spinnerWrapper");
    private By pfdLoadingSpinner = By.cssSelector("#pfdview px-spinner");
    private By approachLoadingSpinner = By.cssSelector("ems-approach-viewer px-spinner");
    private By flightInformation = By.cssSelector(".flex.flex--row.flex--spaced.flex--top.style-scope.event-interaction-view");
    private By flightInformationDivs = By.cssSelector(".flex.flex--col.flex--left.flex--top.u-p.style-scope.event-interaction-view");
    private By eventInput = By.cssSelector("#event-interaction-view input");
    private By eventInputOptions = By.cssSelector("vaadin-combo-box-item");

    private By pfdHorizon = By.id("artificial_horizon");
    private By pfdAltitude = By.id("altitude");
    private By pfdSpeed = By.id("speed");
    private By pfdFace = By.id("face");
    private By pfdCDI = By.id("CDI");
    private By pfdDG = By.id("DG");
    private By pfdPanel = By.id("Panel");

    private By trajectoryCanvas = By.cssSelector("#mapCanvas canvas");

    private By approachAltitueLine = By.cssSelector("ems-approach-viewer .highcharts-series.highcharts-series-0");
    private By approachTerrainLine = By.cssSelector("ems-approach-viewer .highcharts-series.highcharts-series-1");

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

    public Boolean flightInformationVisible(int time){
        return isVisible(flightInformation, time);
    }

    public Boolean pfdHorizonVisible(){
        return isVisible(pfdHorizon, 2);
    }

    public Boolean pfdAltitudeVisible(){
        return isVisible(pfdAltitude, 2);
    }

    public Boolean pfdSpeedVisible(){
        return isVisible(pfdSpeed, 2);
    }

    public Boolean pfdFaceVisible(){
        return isVisible(pfdFace, 2);
    }

    public Boolean pfdCdiVisible(){
        return isVisible(pfdCDI, 2);
    }

    public Boolean pfdDgVisible(){
        return isVisible(pfdDG, 2);
    }

    public Boolean pfdPanelVisible(){
        return isVisible(pfdPanel, 2);
    }

    public Boolean trajectoryCanvasVisible(){ return isVisible(trajectoryCanvas, 2); }

    public Boolean approachAltitudeVisible(){ return isVisible(approachAltitueLine, 5); }

    public Boolean approachTerrainVisible(){ return isVisible(approachTerrainLine, 5); }

    private String getFlightInformation(int index){
        WebElement element = findList(flightInformationDivs).get(index);
        String text = text(element);
        int newline = text.indexOf("\n") + 1;
        return text.substring(newline);
    }
}
