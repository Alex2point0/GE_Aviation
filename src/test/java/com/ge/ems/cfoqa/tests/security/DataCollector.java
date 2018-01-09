package com.ge.ems.cfoqa.tests.security;

import com.ge.ems.api.commons.PropertiesHandler;
import com.ge.ems.cfoqa.Util.UploadResultRow;
import com.ge.ems.cfoqa.pages.CfoqaPageNotFound;
import com.ge.ems.cfoqa.pages.CfoqaUploadFlight;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by jeff.kramer on 10/31/2017.
 */
public class DataCollector extends CfoqaTest {

    private CfoqaPageNotFound pageNotFound;

    public DataCollector(){
        username = PropertiesHandler.properties.getProperty(PropertiesHandler.DATA_COLLECTOR_USERNAME);
        password = PropertiesHandler.properties.getProperty(PropertiesHandler.DATA_COLLECTOR_PASSWORD);
    }

    @BeforeMethod
    public void beforeMethod(){
        pageNotFound = new CfoqaPageNotFound(driver);
        CfoqaUploadFlight uploadFlight = new CfoqaUploadFlight(driver);
        uploadFlight.waitForUploadDataLoad();
    }

    @Test
    public void confirmNavigationMenu(){
        Assert.assertTrue(nav.isUploadFlightVisible(), "Upload Flight link is not visible for user with the aviation-ems-cfoqa-app-upload role.");
        Assert.assertFalse(nav.isViewEventVisible(), "View Event link is visible for user with the aviation-ems-cfoqa-app-upload role.");
        Assert.assertFalse(nav.isConfigurationViewVisible(), "Configuration View link is visible for user with the aviation-ems-cfoqa-app-upload role.");
    }

    @Test
    public void testViewEventUrl(){
        nav.enterViewEventUrl();
        Assert.assertTrue(pageNotFound.isPageNotFoundVisible(), "View Event page is visible to a user with the aviation-ems-cfoqa-app-upload role.");
    }

    @Test
    public void testViewUploadUrl(){
        nav.enterViewUploadUrl();
        Assert.assertTrue(pageNotFound.isPageNotFoundVisible(), "View Upload page is visible to a user with the aviation-ems-cfoqa-app-upload role.");
    }

    @Test
    public void testEventContextViewUrl(){
        nav.enterEventContextViewUrl();
        Assert.assertTrue(pageNotFound.isPageNotFoundVisible(), "Event Context View page is visible to a user with the aviation-ems-cfoqa-app-upload role.");
    }

    @Test
    public void testEventInteractionViewUrl(){
        nav.enterEventInteractionViewUrl();
        Assert.assertTrue(pageNotFound.isPageNotFoundVisible(), "Event Interaction View page is visible to a user with the aviation-ems-cfoqa-app-upload role.");
    }

    @Test
    public void testConfigurationViewUrl(){
        nav.enterConfigurationViewUrl();
        Assert.assertTrue(pageNotFound.isPageNotFoundVisible(), "Configuration View page is visible to a user with the aviation-ems-cfoqa-app-upload role.");
    }

    @Test
    public void testFlightUpload(){
        String validFileLocation = System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\1.sfd";
        String validTailNumber = "N101AD";
        String uploadComments = "Selenium Test - Uploader Security Role.";

        nav.enterUploadFlightUrl();
        CfoqaUploadFlight uploadFlight = new CfoqaUploadFlight(driver);
        uploadFlight.waitForUploadDataLoad();
        logger.info("Uploading file");
        uploadFlight.uploadData(validFileLocation, validTailNumber, uploadComments, false);
        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Upload for a user with the aviation-ems-cfoqa-app-upload role is not showing in the list of uploads after completion.");
    }
}
