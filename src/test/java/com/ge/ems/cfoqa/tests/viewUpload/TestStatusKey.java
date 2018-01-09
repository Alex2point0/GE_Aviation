package com.ge.ems.cfoqa.tests.viewUpload;

import com.ge.ems.cfoqa.pages.CfoqaViewUpload;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by jeff.kramer on 8/22/2017.
 */
public class TestStatusKey extends CfoqaTest {

    private CfoqaViewUpload viewUpload;

    @BeforeMethod
    public void BeforeMethod(){
        if(firstTest){
            firstTest = false;
            driver.get("https://aviation-ems-cfoqa-app-qa.run.aws-usw02-pr.ice.predix.io/#/upload-flights~/upload/explorer");
            viewUpload = new CfoqaViewUpload(driver);
            Assert.assertTrue(viewUpload.waitForStatusKey(), "Status key on the View Upload page didn't load.");
        }
    }

    @Test
    public void ConfirmNoEventsText(){
        Assert.assertEquals(viewUpload.getKeyNoEventsText(), "No Events", "Text for 'No Events' entry in the key does not match the expected value.");
    }

    @Test
    public void ConfirmNoEventsColor(){
        Assert.assertEquals(viewUpload.getKeyNoEventsColor(), "rgba(127, 174, 27, 1)", "Color for 'No Events' entry in the key does not match the expected color.");
    }

    @Test
    public void ConfirmEventsPresentText(){
        Assert.assertEquals(viewUpload.getKeyEventsText(), "Events Present", "Text for 'Events Present' entry in the key does not match the expected value.");
    }

    @Test
    public void ConfirmEventsColor(){
        Assert.assertEquals(viewUpload.getKeyEventsColor(), "rgba(180, 0, 0, 1)", "Color for 'Events Present' entry in the key does not match the expected color.");
    }

    @Test
    public void ConfirmPendingReviewText(){
        Assert.assertEquals(viewUpload.getKeyPendingReviewText(), "Pending Review", "Text for 'Pending Review' entry in the key does not match the expected value.");
    }

    @Test
    public void ConfirmPendingReviewColor(){
        Assert.assertEquals(viewUpload.getKeyPendingReviewColor(), "rgba(254, 198, 0, 1)", "Color for 'Pending Review' entry in the key does not match the expected color.");
    }

    @Test
    public void ConfirmInvalidFlightText(){
        Assert.assertEquals(viewUpload.getKeyInvalidFlightText(), "Invalid Flight", "Text for 'Invalid Flight' entry in the key does not match the expected value.");
    }

    @Test
    public void ConfirmInvalidFlightColor(){
        Assert.assertEquals(viewUpload.getKeyInvalidFlightColor(), "rgba(88, 171, 238, 1)", "Color for 'Invalid Flight' entry in the key does not match the expected color.");
    }
}
