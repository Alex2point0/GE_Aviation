package com.ge.ems.cfoqa.tests.eventInteraction;

import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.CfoqaUtilities;
import com.ge.ems.cfoqa.Util.ViewEventResultRow;
import com.ge.ems.cfoqa.pages.CfoqaEventContextView;
import com.ge.ems.cfoqa.pages.CfoqaEventInteractionView;
import com.ge.ems.cfoqa.pages.CfoqaNavigation;
import com.ge.ems.cfoqa.pages.CfoqaViewEvent;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestFlightInformation extends CfoqaTest {

    private Boolean firstTest = true;
    private CfoqaEventContextView contextView;
    private CfoqaEventInteractionView interactionView;
    private ViewEventResultRow selectedEvent;
    private String[] apiFlightData;

    @BeforeMethod
    public void beforeMethod(){
        if(firstTest) {
            CfoqaNavigation nav = new CfoqaNavigation(driver);
            CfoqaViewEvent viewEvent = nav.getViewEventPage();
            viewEvent.waitForPageLoad();
            selectedEvent = viewEvent.getEventRow(0);
            contextView = viewEvent.viewEvent(0);
            interactionView = contextView.clickInteractButton();
            Assert.assertTrue(interactionView.flightInformationVisible(5), "Flight information on Event Interaction View page was not visible after 5 seconds.");

            apiFlightData = CfoqaApi.getContextViewFlightInformation();

            firstTest = false;
        }
    }

    @Test
    public void confirmFlightId(){
        String contextFlightId = interactionView.getFlightId();
        String viewFlightId = selectedEvent.getFlightId().toString();
        String apiFlightId = apiFlightData[0];

        Assert.assertEquals(contextFlightId, viewFlightId, "Flight ID from the Flight Information on the Event Context View page does not match Flight from the selected event on the View Event page.");

        Assert.assertEquals(contextFlightId, apiFlightId, "Flight ID from the flight information on the Event Context View page does not match Flight ID from API query.");
    }

    @Test
    public void confirmAircraft(){
        String contextAircraft = interactionView.getAircraft();
        String viewAircraft = selectedEvent.getTailNumber();
        String apiAircraft = apiFlightData[2];

        Assert.assertEquals(contextAircraft, viewAircraft, "Aircraft from the Flight Information on the Event Context View page does not match Aircraft from the selected event on the View Event page.");

        Assert.assertEquals(contextAircraft, apiAircraft, "Aircraft from the flight information on the Event Context View page does not match Aircraft from API query.");
    }

    @Test
    public void confirmFleet(){
        String contextFleet = interactionView.getFleet();
        String apiFleet = apiFlightData[1];

        Assert.assertEquals(contextFleet, apiFleet, "Fleet from the flight information on the Event Context View page does not match Fleet from API query.");
    }

    @Test
    public void confirmTakeoffAirportRunway(){
         String contextAirportRunway = interactionView.getAirportRunway();
         String apiAirportRunway = apiFlightData[3] + " / " + apiFlightData[4];

        Assert.assertEquals(contextAirportRunway, apiAirportRunway, "Takeoff Airport and Runway from the flight information on the Event Context View page does not match Takeoff Airport and Runway from API query.");
    }

    @Test
    public void confirmTime(){
        String contextTime = interactionView.getTime();
        String apiTime = apiFlightData[6];
        int splitStart = apiTime.indexOf("T") + 1;
        int splitEnd = apiTime.indexOf(".");
        apiTime = apiTime.substring(splitStart, splitEnd) + " GMT";

        Assert.assertEquals(contextTime, apiTime, "Time from the flight information on the Event Context View page does not match Time from API query.");
    }

    @Test
    public void confirmDate(){
        String contextDate = interactionView.getDate();
        String apiDate = CfoqaUtilities.formatAPIDateAsEventContextDate(apiFlightData[6]);

        Assert.assertEquals(contextDate, apiDate, "Date from the flight information on the Event Context View page does not match Date from API query.");
    }
}
