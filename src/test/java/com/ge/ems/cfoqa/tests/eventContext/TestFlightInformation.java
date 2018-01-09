package com.ge.ems.cfoqa.tests.eventContext;

import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.CfoqaUtilities;
import com.ge.ems.cfoqa.Util.ViewEventResultRow;
import com.ge.ems.cfoqa.pages.CfoqaEventContextView;
import com.ge.ems.cfoqa.pages.CfoqaNavigation;
import com.ge.ems.cfoqa.pages.CfoqaViewEvent;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.*;

public class TestFlightInformation extends CfoqaTest {

    private Boolean firstTest = true;
    private CfoqaEventContextView contextView;
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
            contextView.waitForFlightInformationLoad();

            apiFlightData = CfoqaApi.getContextViewFlightInformation();

            firstTest = false;
        }
    }

    @Test
    public void confirmFlightId(){
        String contextFlightId = contextView.getFlightId();
        String viewFlightId = selectedEvent.getFlightId().toString();
        String apiFlightId = apiFlightData[0];

        Assert.assertEquals(contextFlightId, viewFlightId, "Flight ID from the Flight Information on the Event Context View page does not match Flight from the selected event on the View Event page.");

        Assert.assertEquals(contextFlightId, apiFlightId, "Flight ID from the flight information on the Event Context View page does not match Flight ID from API query.");
    }

    @Test
    public void confirmAircraft(){
        String contextAircraft = contextView.getAircraft();
        String viewAircraft = selectedEvent.getTailNumber();
        String apiAircraft = apiFlightData[2];

        Assert.assertEquals(contextAircraft, viewAircraft, "Aircraft from the Flight Information on the Event Context View page does not match Aircraft from the selected event on the View Event page.");

        Assert.assertEquals(contextAircraft, apiAircraft, "Aircraft from the flight information on the Event Context View page does not match Aircraft from API query.");
    }

    @Test
    public void confirmFleet(){
        String contextFleet = contextView.getFleet();
        String apiFleet = apiFlightData[1];

        Assert.assertEquals(contextFleet, apiFleet, "Fleet from the flight information on the Event Context View page does not match Fleet from API query.");
    }

    @Test
    public void confirmTakeoffAirportRunway(){
         String contextAirportRunway = contextView.getAirportRunway();
         String apiAirportRunway = apiFlightData[3] + " / " + apiFlightData[4];

        Assert.assertEquals(contextAirportRunway, apiAirportRunway, "Takeoff Airport and Runway from the flight information on the Event Context View page does not match Takeoff Airport and Runway from API query.");
    }

    @Test
    public void confirmTime(){
        String contextTime = contextView.getTime();
        String apiTime = apiFlightData[6];
        int splitStart = apiTime.indexOf("T") + 1;
        int splitEnd = apiTime.indexOf(".");
        apiTime = apiTime.substring(splitStart, splitEnd) + " GMT";

        Assert.assertEquals(contextTime, apiTime, "Time from the flight information on the Event Context View page does not match Time from API query.");
    }

    @Test
    public void confirmDate(){
        String contextDate = contextView.getDate();
        String apiDate = CfoqaUtilities.formatAPIDateAsEventContextDate(apiFlightData[6]);

        Assert.assertEquals(contextDate, apiDate, "Date from the flight information on the Event Context View page does not match Date from API query.");
    }
}
