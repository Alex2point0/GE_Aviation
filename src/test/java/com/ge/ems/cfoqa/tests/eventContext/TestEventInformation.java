package com.ge.ems.cfoqa.tests.eventContext;

import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.ViewEventResultRow;
import com.ge.ems.cfoqa.pages.CfoqaEventContextView;
import com.ge.ems.cfoqa.pages.CfoqaNavigation;
import com.ge.ems.cfoqa.pages.CfoqaViewEvent;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;

//TODO Add a test for selecting a new event from the list.
public class TestEventInformation extends CfoqaTest{

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
            selectedEvent = viewEvent.getEventRow(4);
            contextView = viewEvent.viewEvent(4);
            contextView.waitForEventInformationLoad();

            firstTest = false;
        }
    }

    @Test
    public void confirmSelectedEvent(){
        String contextSelectedEvent = contextView.getSelectedEvent();
        String viewSelectedEvent = selectedEvent.getEvent();

        Assert.assertEquals(viewSelectedEvent, contextSelectedEvent, "Selected event from the View Event page does not match the event showing on the Event Context View page.");
    }

    @Test
    public void confirmEventList(){
        String flightId = selectedEvent.getFlightId().toString();
        LinkedList<String> apiEventList = CfoqaApi.getContextViewEventList(flightId, "a7483c449db94a449eb5f67681ee52b0");
        LinkedList<String> appEventList = contextView.getEventList();

        Assert.assertEquals(apiEventList.size(), appEventList.size(), "The size of the list of events from the Event Context View page does not match the size of the list of events returned from the API.");

        for(String event : apiEventList){
            Assert.assertTrue(appEventList.remove(event), "An event from the API call does not appear in the list of events on the Event Context View.");
        }
    }

    @Test
    public void confirmEventSeverity(){
        String expectedEventSeverity = "Caution";
        String appEventSeverity = contextView.getEventSeverity();

        Assert.assertEquals(expectedEventSeverity, appEventSeverity, "The event severity for the application does not match the expected event severity of 'Caution'.");
    }

    @Test
    public void confirmEventMeasurements(){
        contextView.waitForEventMeasurementsLoad();
        LinkedList<String[]> expectedEventMeasurements = new LinkedList();
        expectedEventMeasurements.add(new String[]{"Max Ground Speed during this Event (knots)", "40 knots"});
        LinkedList<String[]> appEventMeasurements = contextView.getEventMeasurements();

        int appMeasurementCount = appEventMeasurements.size();
        int expectedMeasurementCount = expectedEventMeasurements.size();

        Assert.assertEquals(expectedMeasurementCount, appMeasurementCount, "The number of event specific measurements does not match the expected amount: 1");

        for(int i = 0; i < expectedMeasurementCount; i++){
            String[] expectedMeasurement = expectedEventMeasurements.get(i);
            String[] appMeasurement = appEventMeasurements.get(i);

            Assert.assertEquals(expectedMeasurement[0], appMeasurement[0], "The name of an event specific measurement does not match the expected value of " + expectedMeasurement[0]);
            Assert.assertEquals(expectedMeasurement[1], appMeasurement[1], "The value of an event specific measurement does not match the expected value of " + expectedMeasurement[1]);
        }
    }
}
