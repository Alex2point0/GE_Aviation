package com.ge.ems.cfoqa.tests.viewEvent;

import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.ViewEventResultRow;
import com.ge.ems.cfoqa.pages.CfoqaViewEvent;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestEventSearch extends CfoqaTest{

    private CfoqaViewEvent viewEvent;

    @BeforeMethod
    public void beforeMethod(){
        if(firstTest){
            viewEvent = nav.getViewEventPage();
            firstTest = false;
        } else {
           nav.refreshPage();
        }

        viewEvent.waitForPageLoad();
    }

    @Test
    public void ConfirmSearchResults(){
        ArrayList<ViewEventResultRow> apiSearchResults = CfoqaApi.getViewEventSearchResults("a7483c449db94a449eb5f67681ee52b0");

        LinkedList<ViewEventResultRow> appSearchResults = viewEvent.getEventSearchResults();

        Assert.assertEquals(apiSearchResults.size(), appSearchResults.size(), "Number of records being shown on the View Events page after loading does not match the number of records returns from the API query.");

        Integer searchResultSize = apiSearchResults.size();

        for(int i = 0; i < searchResultSize; i++){
            //logger.info("\nAPI: " + apiSearchResults.get(i).getFlightId() + " | " + apiSearchResults.get(i).getEvent() + " | " + apiSearchResults.get(i).getCollectionDate() + " | " + apiSearchResults.get(i).getTailNumber() + "\nApp: " + appSearchResults.get(i).getFlightId() + " | " + appSearchResults.get(i).getEvent() + " | " + appSearchResults.get(i).getCollectionDate() + " | " + appSearchResults.get(i).getTailNumber());

            Assert.assertEquals(apiSearchResults.get(i).getTailNumber(), appSearchResults.get(i).getTailNumber(), "Tail number from the View Event page does not match the value retrieved from the API.");
            Assert.assertEquals(apiSearchResults.get(i).getCollectionDate(), appSearchResults.get(i).getCollectionDate(), "Collection date from the View Event page does not match the value retrieved from the API.");
            Assert.assertEquals(apiSearchResults.get(i).getEvent(), appSearchResults.get(i).getEvent(), "Event from the View Event page does not match the value retrieved from the API.");
            Assert.assertEquals(apiSearchResults.get(i).getFlightId(), appSearchResults.get(i).getFlightId(), "Flight ID from the View Event page does not match the value retrieved from the API.");
        }
    }

    @Test
    public void TestTailNumberSearch(){
        String validTailNumber = "N101AD";
        viewEvent.enterTailNumber(validTailNumber);

        LinkedList<String> appTailNumbers = viewEvent.getTailNumbers();

        ArrayList<String> apiTailNumbers = CfoqaApi.getViewEventTailNumbers("a7483c449db94a449eb5f67681ee52b0");

        Assert.assertEquals(appTailNumbers.size(), apiTailNumbers.size(), "The number of tail numbers shown on the View Event page does not match the number of tail numbers retrieved from the API when filtering by the tail number " + validTailNumber);

        for(String tailNumber : appTailNumbers){
            Assert.assertEquals(validTailNumber, tailNumber, "Tail number from the search results on the View Event page does not match the tail number searched for.");
        }
    }

    @Test
    public void TestEventSearch(){
        String validEvent = "Airspeed Exceeds Limit for Low Altitudes";
        //String validEvent = "Airspeed";
        viewEvent.enterEvent(validEvent);

        LinkedList<String> appEvents = viewEvent.getEvents();

        ArrayList<String> apiEvents = CfoqaApi.getViewEventEvents("a7483c449db94a449eb5f67681ee52b0");

        Assert.assertEquals(appEvents.size(), apiEvents.size(), "The number of events shown on the View Event page does not match the number of event retrieved from the API when filtering by the event " + validEvent);

        for(String event : appEvents){
            Assert.assertEquals(validEvent, event, "Event from the search results on the View Event page does not match the event searched for.");
        }
    }

    @Test
    //TODO Test that the dates returned from the application fall between the two target dates
    public void TestDownloadDateSearch(){
        String downloadDate = "16 Nov 2016";
        viewEvent.enterDownloadDateRange("2016", "NOV", "13", "2016", "DEC", "3");

        LinkedList<String> appDownloadDates = viewEvent.getDownloadDates();

        ArrayList<String> apiDownloadDates = CfoqaApi.getViewEventsDownloadDate("a7483c449db94a449eb5f67681ee52b0");

        Assert.assertEquals(appDownloadDates.size(), apiDownloadDates.size(), "The number of events shown on the View Event page does not match the number of events retrieved from the API when filtering for events between 11/13/2016 and 3/12/2016");

        for(String date : appDownloadDates){
            Assert.assertEquals(downloadDate, date, "Download date from the search results on the View Event page does not match the expected value.");
        }
    }
}
