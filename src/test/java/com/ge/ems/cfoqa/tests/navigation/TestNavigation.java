package com.ge.ems.cfoqa.tests.navigation;

import com.ge.ems.cfoqa.pages.*;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNavigation extends CfoqaTest {

    @Test
    public void testViewEventsLink(){
        CfoqaViewEvent viewEvent = nav.getViewEventPage();
        Assert.assertTrue(viewEvent.isPageLoaded(), "View Event page did not load after clicking the View Event link.");
    }

    @Test
    public void testUploadFlightLink(){
        CfoqaUploadFlight uploadFlight = nav.getUploadFlightPage();
        Assert.assertTrue(uploadFlight.isPageLoaded(), "Upload Flight page did not load after clicking the Upload Flight link.");
    }
}
