package com.ge.ems.cfoqa.tests.viewUpload;

import com.ge.ems.api.util.Utils;
import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.UploadMetadata;
import com.ge.ems.cfoqa.Util.UploadResultRow;
import com.ge.ems.cfoqa.pages.CfoqaUploadFlight;
import com.ge.ems.cfoqa.pages.CfoqaViewUpload;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import com.ge.ems.common.Utilities;
import org.json.simple.JSONArray;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TestSuccessfulSingleFlightUpload extends CfoqaTest{

    private String validTailNumber = "N101AD";
    private String validFileLocation = System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\1.sfd";
    private String uploadComments = "Selenium Upload - Test View Upload";
    private UploadResultRow uploadRow;
    private UploadMetadata metadata;
    private CfoqaViewUpload viewUpload;
    private JSONArray downloadData;

    @BeforeMethod
    public void BeforeMethod(){
        if(firstTest){
            firstTest = false;
            nav.waitForEmsSystemLoad();
            CfoqaUploadFlight uploadFlight = nav.getUploadFlightPage();
            uploadFlight.uploadData(validFileLocation, validTailNumber, uploadComments, false);
            uploadRow = uploadFlight.getUploadRow(0);
            metadata = uploadRow.getMetadata();
            viewUpload = uploadFlight.clickViewUploadButton();
            Assert.assertNotNull(viewUpload, "Failed to click the View Upload button.");

            downloadData = CfoqaApi.getViewUploadDownloadInformation(viewUpload.getHeaderUploadId());
        }
    }

    @Test
    public void ConfirmHeaderUploadId(){
        Assert.assertEquals(metadata.getUploadId(), viewUpload.getHeaderUploadId(), "Upload ID in the View Upload header does not match the Upload ID from the Upload Flight page.");
    }

    @Test
    public void ConfirmHeaderDownloadId(){
        Assert.assertEquals(metadata.getDownloadId(), viewUpload.getHeaderDownloadId(), "Download ID in the View Upload header does not match the Download ID from the Upload Flight page.");
    }

    @Test
    public void ConfirmHeaderUploadDate(){
        String headerUploadDate = viewUpload.getHeaderUploadDate();
        String apiUploadDate = (String)downloadData.get(1);

        Assert.assertNotNull(headerUploadDate, "No upload date found in the View Upload header.");
        Assert.assertEquals(apiUploadDate, headerUploadDate, "Upload date for the flight does not match the upload date from the API.");
    }

    @Test
    public void ConfirmHeaderTailNumber(){
        String headerTailNumber = viewUpload.getHeaderTailNumber();
        String apiTailNumber =  (String)downloadData.get(2);

        Assert.assertEquals(uploadRow.getTailNumber(), headerTailNumber, "Tail Number in the View Upload header does not match the Tail Number from the Upload Flight page.");
        Assert.assertEquals(apiTailNumber, headerTailNumber, "Tail number for the flight does not match the tail number from the API.");
    }

    @Test
    public void ConfirmHeaderSyncErrors(){
        String headerSyncErrors = viewUpload.getHeaderSyncErrors();
        String apiSyncErrors = (String)downloadData.get(3);

        Assert.assertNotEquals(headerSyncErrors, "", "No value found for Sync Errors in the View Upload header.");
        Assert.assertEquals(apiSyncErrors, headerSyncErrors, "Sync error percent for the flight does not match the sync error percent from the API.");
    }

    @Test
    public void ConfirmHeaderValidFlights(){
        String headerValidFlights = viewUpload.getHeaderValidFlights();
        String apiValidFlights = (String)downloadData.get(4);

        Assert.assertNotEquals(headerValidFlights, "", "No value found for Flights with valid takeoff and landing in the View Upload header.");
        Assert.assertEquals(apiValidFlights, headerValidFlights, "Number of valid flights does not match the number of valid flights from the API.");
    }

    @Test
    public void ConfirmHeaderComments(){
        String headerComments = viewUpload.getHeaderComments();
        String apiComments = (String)downloadData.get(5);

        Assert.assertEquals(uploadRow.getComments(), headerComments, "Comments in the View Upload header does not match the Comments from the Upload Flight page.");
        Assert.assertEquals(apiComments.trim(), headerComments, "Comments for the flight do not match the comments from the API.");
    }

    @Test
    public void ConfirmFlightData(){
        JSONArray apiFlightData = CfoqaApi.getViewUploadFlightInformation(viewUpload.getHeaderUploadId());
        HashMap<String, String[]> flightAppData = viewUpload.getFlightData();

        String flightId = (String)apiFlightData.get(0);

        Assert.assertTrue(flightAppData.containsKey(flightId), "Flight ID " + flightId + " from the API not found in the333 list of flights in the application.");

        String[] singleFlightData = flightAppData.get(flightId);

        Assert.assertEquals(apiFlightData.get(1), singleFlightData[0], "Flight date from the API does not match flight date from the application for flight with ID " + flightId + ".");
        Assert.assertEquals(apiFlightData.get(5), singleFlightData[1], "Airport pair from the API does not match the airport pair from the application for flight with ID " + flightId + ".");
        Assert.assertEquals(apiFlightData.get(2), singleFlightData[2], "Flight hours from the API does not match the flight hours from the application for flight with ID " + flightId + ".");

        if(apiFlightData.get(4) == "Duplicate"){
            Assert.assertEquals("DUPLICATE", singleFlightData[3], "Note for flight with ID " + flightId + " does not say DUPLICATE.");
            Assert.assertTrue(singleFlightData[4].contains("invalid"), "Incorrect color being displayed for flight with ID " + flightId);
        }
    }
}
