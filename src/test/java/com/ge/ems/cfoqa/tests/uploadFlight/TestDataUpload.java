package com.ge.ems.cfoqa.tests.uploadFlight;

import com.ge.ems.api.util.Utils;
import com.ge.ems.cfoqa.Util.CfoqaUtilities;
import com.ge.ems.cfoqa.Util.UploadMetadata;
import com.ge.ems.cfoqa.Util.UploadResultRow;
import com.ge.ems.cfoqa.pages.CfoqaUploadFlight;
import com.ge.ems.common.Utilities;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Created by jeff.kramer on 8/3/2017.
 */
public class TestDataUpload extends CfoqaTest {

    private CfoqaUploadFlight uploadFlight;

    private String validTailNumber = "N101AD";
    private String validFileLocation = System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\1.sfd";
    private String invalidFileLocation = System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\Install-StandaloneFDV.ps1";
    private String timesCircleClass = "fa-times-circle";
    private String checkYourFileErrorMsg = "Check your file";

    @BeforeMethod
    public void beforeMethod(){
        if(firstTest){
            uploadFlight = nav.getUploadFlightPage();
            firstTest = false;
        }
        
        uploadFlight.waitForUploadDataLoad();
    }

    @AfterMethod
    public void AfterMethod(ITestResult testResult){
        String testName = testResult.getName();

        logger.info(testName);

        if(testName.equals("TestDiceFailure")){
            Utilities.setDiceAndDeidentifyMinFlightHours(".1");
        }

        if(testName.equals("TestDuplicateFile")){
            Utilities.deactivateDuplicateDetection();
        }

        try {
            if (testResult.getStatus() == ITestResult.FAILURE) {
                logger.debug("Taking screen capture of screen at the time of test failure");
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, new File("C:\\users\\somoene\\" + testResult.getName() + ".jpg"));

                logger.debug("Taking screen capture of console from dev tools at the time of failure");
                String fileName = "C:\\Users\\somoene\\" + testName + "-ConsoleCapture.jpg";
                //String fileName = "C:\\users\\jeff.kramer\\" + testName + "-ConsoleCapture.png";
                Process pr = Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\src\\test\\resources\\cfoqa\\scripts\\GetChromeDevToolsConsoleOutput.exe " + fileName);

                int retVal = pr.waitFor();

                logger.debug("AutoIT command return value: " + retVal);

                Utils.threadSleep(2000);

                nav.refreshPage();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //@Test
    public void TestSingleFlightUpload(){
        String uploadComments = "Selenium Upload - Test Single Flight";
        String checkCircleClass = "fa-check-circle";

        logger.info("Testing valid flight upload ...");
        Reporter.log("Tail Number: " + validTailNumber + "\nFile: " + validFileLocation + "\nComments: " + uploadComments);

        uploadFlight.uploadData(validFileLocation, validTailNumber, uploadComments, false);
        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        UploadMetadata metadata = uploadRow.getMetadata();

        Assert.assertEquals(uploadRow.getTailNumber(), validTailNumber, "Tail number from the upload search results does not match the tail number entered.");
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Comments from the upload search results do not match the comments entered.");
        Assert.assertEquals(uploadRow.getUploadTime(), "Last hour", "Upload time from the upload search results does not match the expected upload time.");
        Assert.assertTrue(uploadRow.getIconClass().contains(checkCircleClass), "Check circle icon for a successful upload not being shown in the upload search results.");

        String appDuplicateFlights = uploadRow.getDuplicateFlights();
        if(!(appDuplicateFlights.equals("1 of 1") || appDuplicateFlights.equals("0 of 1"))){
            Assert.fail("Duplicate flights from the upload search results (" + appDuplicateFlights + ") does not match the expected value for duplicate flights.");
        }

        Assert.assertNotNull(metadata, "Error retrieving metadata for the selected upload.");
        Assert.assertNotNull(metadata.getUploadId(), "No Upload ID found for the upload.");
        Assert.assertNotNull(metadata.getDownloadId(), "No EMS Download ID found for the upload.");
        Assert.assertEquals(metadata.getName(), validTailNumber, "Tail number in metadata does not match tail number provided for the upload.");
        Assert.assertEquals(metadata.getSize(), "489.25KB", "File size in the metadata does not match the expected file of 489.25KB");
        Assert.assertEquals(metadata.getState(), "processedSuccess", "Incorrect value being displayed in the metadata for the state of the upload.");
        Assert.assertEquals(metadata.getProcessingState(), "processed", "Incorrect value being displayed in the metadata for the EMS processing state of the upload.");
        Assert.assertEquals(metadata.getProcessingFailure(), "", "Incorrect value being displayed in the metadata for the EMS processing failure of the upload.");
    }

    //@Test
    public void TestWrongTailNumber(){
        String uploadComments = "Selenium Upload - Test Invalid Tail Number";
        String invalidTailNumber = "N282WN";
        String expectedErrorMessage = "Mismatched/unknown tail number";

        logger.info("Testing upload with incorrect tail number ...");

        Reporter.log("Tail Number: " + invalidTailNumber + "\nFile: " + validFileLocation + "\nComments: " + uploadComments);

        uploadFlight.uploadData(validFileLocation, invalidTailNumber, uploadComments, false);
        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        UploadMetadata metadata = uploadRow.getMetadata();

        Assert.assertEquals(uploadRow.getTailNumber(), invalidTailNumber, "Tail number from the upload search results does not match the tail number entered.");
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Comments from the upload search results do not match the comments entered.");
        Assert.assertEquals(uploadRow.getUploadTime(), "Last hour", "Upload time from the upload search results does not match the expected upload time.");
        Assert.assertTrue(uploadRow.getIconClass().contains(timesCircleClass), "Times circle icon for a unsuccessful upload not being shown in the upload search results.");
        Assert.assertEquals(uploadRow.getDuplicateFlights(), "Not Available", "Duplicate flights from the upload search results does not match the expected duplicate flight.");
        Assert.assertEquals(uploadRow.getErrorMsg(), expectedErrorMessage, "Error messages from uploading data with an incorrect tail number does not match the expected error message.");

        Assert.assertNotNull(metadata, "Error retrieving metadata for the selected upload.");
        Assert.assertNotNull(metadata.getUploadId(), "No Upload ID found for the upload.");
        Assert.assertNotNull(metadata.getDownloadId(), "No EMS Download ID found for the upload.");
        Assert.assertEquals(metadata.getName(), invalidTailNumber, "Tail number in metadata does not match tail number provided for the upload.");
        Assert.assertEquals(metadata.getSize(), "489.25KB", "File size in metadata does not match the expected file size of 489.25KB");
        Assert.assertEquals(metadata.getState(), "processedSuccess", "Incorrect value being displayed in the metadata for the state of the upload.");
        Assert.assertEquals(metadata.getProcessingState(), "failure", "Incorrect value being displayed in the metadata for the EMS processing state of the upload.");
        Assert.assertEquals(metadata.getProcessingFailure(), "Failed Download Auto-ACID", "Incorrect value being displayed in the metadata for the EMS processing failure of the upload.");
    }

    //@Test
    public void TestWrongFileType(){
        logger.info("Testing upload with incorrect file type ...");

        String uploadComments = "Selenium Upload - Test Incorrect File Type";
        Reporter.log("Tail Number: " + validTailNumber + "\nFile: " + invalidFileLocation + "\nComments: " + uploadComments);

        uploadFlight.uploadData(invalidFileLocation, validTailNumber, uploadComments, false);
        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        UploadMetadata metadata = uploadRow.getMetadata();

        Assert.assertEquals(uploadRow.getTailNumber(), validTailNumber, "Tail number from the upload search results does not match the tail number entered.");
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Comments from the upload search results do not match the comments entered.");
        Assert.assertEquals(uploadRow.getUploadTime(), "Last hour", "Upload time from the upload search results does not match the expected upload time.");
        Assert.assertTrue(uploadRow.getIconClass().contains(timesCircleClass), "Times circle icon for a unsuccessful upload not being shown in the upload search results.");
        Assert.assertEquals(uploadRow.getDuplicateFlights(), "Not Available", "Duplicate flights from the upload search results does not match the expected duplicate flight.");
        Assert.assertEquals(uploadRow.getErrorMsg(), checkYourFileErrorMsg, "Error messages from uploading data with an incorrect tail number does not match the expected error message.");

        Assert.assertNotNull(metadata, "Error retrieving metadata for the selected upload.");
        Assert.assertNotNull(metadata.getUploadId(), "No Upload ID found for the upload.");
        Assert.assertNotNull(metadata.getDownloadId(), "No EMS Download ID found for the upload.");
        Assert.assertEquals(metadata.getName(), validTailNumber, "Tail number in metadata does not match tail number provided for the upload.");
        Assert.assertEquals(metadata.getSize(), "1.74KB", "File size in the metadata does not match the expected file size of 1.74KB.");
        Assert.assertEquals(metadata.getState(), "processedSuccess", "Incorrect value being displayed in the metadata for the state of the upload.");
        Assert.assertEquals(metadata.getProcessingState(), "failure", "Incorrect value being displayed in the metadata for the EMS processing state of the upload.");
        Assert.assertEquals(metadata.getProcessingFailure(), "Failed Download Sync", "Incorrect value being displayed in the metadata for the EMS processing failure of the upload.");
    }

    //@Test
    public void TestDuplicateFile(){
        String uploadComments = "Selenium Upload - Test Duplicate Detection";
        String expectedErrorMessage = "Duplicate file";

        logger.info("Testing upload of duplicate file ...");
        Reporter.log("Tail Number: " + validTailNumber + "\nFile: " + validFileLocation + "\nComments: " + uploadComments);

        Utilities.activateDuplicateDetection();

        uploadFlight.uploadData(validFileLocation, validTailNumber, uploadComments, false);
        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        UploadMetadata metadata = uploadRow.getMetadata();

        Assert.assertEquals(uploadRow.getTailNumber(), validTailNumber, "Tail number from the upload search results does not match the tail number entered.");
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Comments from the upload search results do not match the comments entered.");
        Assert.assertEquals(uploadRow.getUploadTime(), "Last hour", "Upload time from the upload search results does not match the expected upload time.");
        Assert.assertTrue(uploadRow.getIconClass().contains(timesCircleClass), "Times circle icon for a unsuccessful upload not being shown in the upload search results.");
        Assert.assertEquals(uploadRow.getDuplicateFlights(), "Not Available", "Duplicate flights from the upload search results does not match the expected duplicate flight.");
        Assert.assertEquals(uploadRow.getErrorMsg(), expectedErrorMessage, "Error messages from uploading duplicate data does not match the expected error message.");

        Assert.assertNotNull(metadata, "Error retrieving metadata for the selected upload.");
        Assert.assertNotNull(metadata.getUploadId(), "No Upload ID found for the upload.");
        Assert.assertNotNull(metadata.getDownloadId(), "No EMS Download ID found for the upload.");
        Assert.assertEquals(metadata.getName(), validTailNumber, "Tail number in metadata does not match tail number provided for the upload.");
        Assert.assertEquals(metadata.getSize(), "489.25KB", "File size in the metadata does not match the expected file size of 489.25KB.");
        Assert.assertEquals(metadata.getState(), "processedSuccess", "Incorrect value being displayed in the metadata for the state of the upload.");
        Assert.assertEquals(metadata.getProcessingState(), "failure", "Incorrect value being displayed in the metadata for the EMS processing state of the upload.");
        Assert.assertEquals(metadata.getProcessingFailure(), "Failed Download Duplicate Detection", "Incorrect value being displayed in the metadata for the EMS processing failure of the upload.");
    }

    //@Test
    public void TestDiceFailure(){
        String uploadComments = "Selenium Upload - Test Dice and Deidentify";

        logger.info("Testing upload that'll fail dice and deidentify ...");
        Reporter.log("Tail Number: " + validTailNumber + "\nFile: " + validFileLocation + "\nComments: " + uploadComments);

        Utilities.setDiceAndDeidentifyMinFlightHours("24");
        uploadFlight.uploadData(validFileLocation, validTailNumber, uploadComments, false);
        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        UploadMetadata metadata = uploadRow.getMetadata();

        Assert.assertEquals(uploadRow.getTailNumber(), validTailNumber, "Tail number from the upload search results does not match the tail number entered.");
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Comments from the upload search results do not match the comments entered.");
        Assert.assertEquals(uploadRow.getUploadTime(), "Last hour", "Upload time from the upload search results does not match the expected upload time.");
        Assert.assertTrue(uploadRow.getIconClass().contains(timesCircleClass), "Times circle icon for a unsuccessful upload not being shown in the upload search results.");
        Assert.assertEquals(uploadRow.getDuplicateFlights(), "Not Available", "Duplicate flights from the upload search results does not match the expected duplicate flight.");
        Assert.assertEquals(uploadRow.getErrorMsg(), checkYourFileErrorMsg, "Error messages from failing flight dice and deidentify does not match the expected error message.");

        Assert.assertNotNull(metadata, "Error retrieving metadata for the selected upload.");
        Assert.assertNotNull(metadata.getUploadId(), "No Upload ID found for the upload.");
        Assert.assertNotNull(metadata.getDownloadId(), "No EMS Download ID found for the upload.");
        Assert.assertEquals(metadata.getName(), validTailNumber, "Tail number in metadata does not match tail number provided for the upload.");
        Assert.assertEquals(metadata.getSize(), "489.25KB", "File size in the metadata does not match the expected file size of 489.25KB");
        Assert.assertEquals(metadata.getState(), "processedSuccess", "Incorrect value being displayed in the metadata for the state of the upload.");
        Assert.assertEquals(metadata.getProcessingState(), "failure", "Incorrect value being displayed in the metadata for the EMS processing state of the upload.");
        Assert.assertEquals(metadata.getProcessingFailure(), "Failed Download Dice and Deidentify", "Incorrect value being displayed in the metadata for the EMS processing failure of the upload.");
    }

    @Test
    public void TestCancelUpload(){
        String uploadComments = "Selenium Upload - Test Canceling Upload";
        String expectedErrorMessage = "Canceled by the user";
        String largeFileLocation = "C:\\temp\\TestData\\N714SA.zip";
        String exclamationCircleClass = "fa-exclamation-circle";

        Reporter.log("Tail Number: " + validTailNumber + "\nFile: " + largeFileLocation + "\nComments: " + uploadComments);

        uploadFlight.uploadData(largeFileLocation, validTailNumber, uploadComments, true);

        UploadResultRow uploadRow = uploadFlight.getUploadRow(0);
        UploadMetadata metadata = uploadRow.getMetadata();

        Assert.assertNotEquals(uploadRow.getTailNumber(), validTailNumber, "Tail number from the upload search results does not match the tail number entered.");
        Assert.assertEquals(uploadRow.getComments(), uploadComments, "Comments from the upload search results do not match the comments entered.");
        Assert.assertEquals(uploadRow.getUploadTime(), "Last hour", "Upload time from the upload search results does not match the expected upload time.");
        Assert.assertTrue(uploadRow.getIconClass().contains(exclamationCircleClass), "Times circle icon for a unsuccessful upload not being shown in the upload search results.");
        Assert.assertEquals(uploadRow.getDuplicateFlights(), "Not Available", "Duplicate flights from the upload search results does not match the expected duplicate flight.");
        Assert.assertEquals(uploadRow.getErrorMsg(), expectedErrorMessage, "Error messages from failing flight dice and deidentify does not match the expected error message.");

        Assert.assertNotNull(metadata, "Error retrieving metadata for the selected upload.");
        Assert.assertNotNull(metadata.getUploadId(), "No Upload ID found for the upload.");
        Assert.assertEquals(metadata.getDownloadId(), "", "EMS Download ID found for the upload when one was not expected.");
        Assert.assertEquals(metadata.getName(), validTailNumber, "Tail number in metadata does not match tail number provided for the upload.");
        Assert.assertEquals(metadata.getSize(), "14254.96KB", "File size in the metadata does not match the expected file size of 14254.96KB");
        Assert.assertEquals(metadata.getState(), "canceled", "Incorrect value being displayed in the metadata for the state of the upload.");
        Assert.assertEquals(metadata.getProcessingState(), "", "Incorrect value being displayed in the metadata for the EMS processing state of the upload.");
        Assert.assertEquals(metadata.getProcessingFailure(), "", "Incorrect value being displayed in the metadata for the EMS processing failure of the upload.");
    }
}
