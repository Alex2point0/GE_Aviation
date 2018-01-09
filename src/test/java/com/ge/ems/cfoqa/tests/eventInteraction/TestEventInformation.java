package com.ge.ems.cfoqa.tests.eventInteraction;

import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.ViewEventResultRow;
import com.ge.ems.cfoqa.pages.CfoqaEventContextView;
import com.ge.ems.cfoqa.pages.CfoqaEventInteractionView;
import com.ge.ems.cfoqa.pages.CfoqaNavigation;
import com.ge.ems.cfoqa.pages.CfoqaViewEvent;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;

public class TestEventInformation extends CfoqaTest{

    private Boolean firstTest = true;
    private CfoqaEventContextView contextView;
    private CfoqaEventInteractionView interactionView;
    private ViewEventResultRow selectedEvent;

    @BeforeMethod
    public void beforeMethod(){
        if(firstTest) {
            CfoqaNavigation nav = new CfoqaNavigation(driver);
            CfoqaViewEvent viewEvent = nav.getViewEventPage();
            viewEvent.waitForPageLoad();
            selectedEvent = viewEvent.getEventRow(4);
            contextView = viewEvent.viewEvent(4);
            interactionView = contextView.clickInteractButton();
            firstTest = false;
        }
    }

    @Test
    public void confirmSelectedEvent(){
        String contextSelectedEvent = interactionView.getSelectedEvent();
        String viewSelectedEvent = selectedEvent.getEvent();

        Assert.assertEquals(viewSelectedEvent, contextSelectedEvent, "Selected event from the View Event page does not match the event showing on the Event Context View page.");
    }

    @Test
    public void confirmEventList(){
        String flightId = selectedEvent.getFlightId().toString();
        LinkedList<String> apiEventList = CfoqaApi.getContextViewEventList(flightId, "a7483c449db94a449eb5f67681ee52b0");
        LinkedList<String> appEventList = interactionView.getEventList();

        Assert.assertEquals(apiEventList.size(), appEventList.size(), "The size of the list of events from the Event Context View page does not match the size of the list of events returned from the API.");

        for(String event : apiEventList){
            Assert.assertTrue(appEventList.remove(event), "An event from the API call does not appear in the list of events on the Event Context View.");
        }
    }
}
