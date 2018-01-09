package com.ge.ems.cfoqa.tests.uploadFlight;

import com.ge.ems.cfoqa.Util.CfoqaApi;
import com.ge.ems.cfoqa.Util.UploadMetadata;
import com.ge.ems.cfoqa.Util.UploadResultRow;
import com.ge.ems.cfoqa.pages.CfoqaUploadFlight;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.LinkedHashMap;

/**
 * Created by jeff.kramer on 9/11/2017.
 */
public class TestUploadSearch extends CfoqaTest {

    private CfoqaUploadFlight uploadFlight;

    @BeforeMethod
    public void beforeMethod(){
        if(firstTest){
            uploadFlight = nav.getUploadFlightPage();
            uploadFlight.waitForUploadDataLoad();
            firstTest = false;
        }
    }

    @Test
    public void TestRowExpandOnClick(){
        logger.info("Testing row expand on click ...");
        uploadFlight.clickDataRow(0);
        Assert.assertTrue(uploadFlight.confirmDataRowExpanded(0), "Data row metadata not showing after click to show the metadata.");
        uploadFlight.clickDataRow(0);
        Assert.assertFalse(uploadFlight.confirmDataRowExpanded(0), "Data row metadata still showing after click to hide the metadata.");
    }

    //TODO Assertions for Upload Time
    @Test
    public void ConfirmSearchResults(){
        logger.info("Confirming search results on Upload Flight page match data from API ...");
        LinkedHashMap<String, UploadResultRow> appUploadData = uploadFlight.getSearchResults();

        LinkedHashMap<String, UploadResultRow> apiUploadData = CfoqaApi.getUploadFlightUploadData(appUploadData);

        for(String key : apiUploadData.keySet()){
            UploadResultRow appRow = appUploadData.get(key);
            UploadResultRow apiRow = apiUploadData.get(key);

            Assert.assertNotNull(appRow,"Upload with Upload ID " + key + " was returned from the API but not found search results on the Upload Flight page.");

            Assert.assertEquals(appRow.getTailNumber(), apiRow.getTailNumber(), "Tail number from the Upload Flight page for Upload ID " + key + " does not match tail number from the API.");
            Assert.assertEquals(appRow.getComments(), apiRow.getComments(), "Comments from the Upload Flight page for Upload ID " + key + " does not match comments from the API.");
            Assert.assertEquals(appRow.getErrorMsg(), apiRow.getErrorMsg(), "Error message from the Upload Flight page for Upload ID " + key + " does not match error message from the API.");
            Assert.assertEquals(appRow.getDuplicateFlights(), apiRow.getDuplicateFlights(), "Duplicate flights from the Upload Flight page for Upload Id " + key + " does not match duplicate flights from the API.");
            Assert.assertTrue(appRow.getIconClass().contains(apiRow.getIconClass()), "Icon from the Upload Flight page for Upload ID " + key + " does not match expected icon from the API.");

            UploadMetadata appRowMetadata = appRow.getMetadata();
            UploadMetadata apiRowMetadata = apiRow.getMetadata();

            Assert.assertEquals(appRowMetadata.getName(), apiRowMetadata.getName(), "Name from the Upload Flight page metadata for Upload ID " + key + " doesn't not match name from the API.");
            Assert.assertEquals(appRowMetadata.getSize(), apiRowMetadata.getSize(), "Size from the Upload Flight page metadata for Upload ID " + key + " doesn't not match size from the API.");
            Assert.assertEquals(appRowMetadata.getState(), apiRowMetadata.getState(), "State from the Upload Flight page metadata for Upload ID " + key + " doesn't not match state from the API.");
            Assert.assertEquals(appRowMetadata.getProcessingState(), apiRowMetadata.getProcessingState(), "Processing state from the Upload Flight page metadata for Upload ID " + key + " doesn't not match processing state from the API.");
            Assert.assertEquals(appRowMetadata.getProcessingFailure(), apiRowMetadata.getProcessingFailure(), "Processing failure from the Upload Flight page metadata for Upload ID " + key + " doesn't not match processing failure from the API.");
        }
    }
}
