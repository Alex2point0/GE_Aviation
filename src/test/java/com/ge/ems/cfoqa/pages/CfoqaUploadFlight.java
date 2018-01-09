package com.ge.ems.cfoqa.pages;

import com.ge.ems.api.util.Utils;
import com.ge.ems.cfoqa.Util.UploadMetadata;
import com.ge.ems.cfoqa.Util.UploadResultRow;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CfoqaUploadFlight extends BasePage{

    private static final Logger logger = LoggerFactory.getLogger(CfoqaUploadFlight.class);

    private By loadingSpinner = By.cssSelector("ems-processing-status px-spinner");
    private By fileUploadInput = By.cssSelector("#fileuploader input");
    private By tailInput = By.cssSelector("#tail #input");
    private By tailNumberVaadinComboBoxItem = By.cssSelector("#tail-box vaadin-combo-box-item");
    private By commentTextArea = By.cssSelector("#comments #textarea");
    private By uploadButton = By.id("uploadbtn");
    private By uploadStatusBar = By.id("bar");
    private By uploadStatusText = By.cssSelector(".text.style-scope.ems-ingestion-status");
    private By cancelUpload = By.cssSelector(".flex.flex--row.flex--middle.style-scope.ems-ingestion-status i");
    private By uploadDataDiv = By.cssSelector("ems-processing-status iron-list .item.style-scope.iron-data-table");
    private By uploadDataRow = By.cssSelector("ems-processing-status iron-list data-table-row");
    private By uploadDataMetadata = By.cssSelector("data-table-row-detail");
    private By viewUploadButton = By.cssSelector(".btn.btn--primary.u-m.style-scope.upload-view");
    private By bucketInput = By.cssSelector("#labelAndInputContainer input");
    private By elementTag = By.cssSelector("px-view#upload");

    public CfoqaUploadFlight(WebDriver driver){ super(driver); }

    public void uploadFile(String file){
        if(isPresent(fileUploadInput, 5)) {
            find(fileUploadInput).clear();

            logger.info("Attempting to add the following file to the file upload widget: " + file);
            find(fileUploadInput).sendKeys(file);

        } else {
            Assert.fail("File upload input not present on the page.");
        }
    }

    public void enterTailNumber(String tailNumber){
        logger.info("Entering tail number - " + tailNumber);
        if (isClickable(tailInput, 2)) {
            /*Utils.threadSleep(500);*/
            click(tailInput);
            type(tailNumber, tailInput);

            if(isClickable(tailNumberVaadinComboBoxItem, 1)){
                click(tailNumberVaadinComboBoxItem);
            }

        } else {
            Assert.fail("Unable to input Tail Number for the upload.");
        }
    }

    public void enterComments(String comment){
        logger.info("Entering comments - " + comment);
        if(isClickable(commentTextArea, 10)){
            type(comment, commentTextArea);
        } else {
            Assert.fail("Unable to input Comments for the upload.");
        }
    }

    public void clickUploadButton(){
        if(find(uploadButton).isEnabled()) {
            logger.info("Clicking Upload Button");
            click(uploadButton);
        } else {
            Assert.fail("Upload button is not enabled.");
        }
    }

    public void clickDataRow(int index){
        if(isVisible(uploadDataRow, 10)){
            List<WebElement> dataRows = findList(uploadDataRow);
            WebElement dataRow = dataRows.get(index);

            clickAtCoordinates(dataRow, 100, 10);
            //new Actions(driver).moveToElement(dataRow, 100, 20).click().build().perform();

        } else {
            Assert.fail("The rows of upload data are not visible on the page.");
        }
    }

    public Boolean isUploadStatusVisible(){
        return isVisible(uploadStatusBar, 30);
    }

    public Boolean confirmUploadStatusText(){
        return isVisible(uploadStatusText, 15);
    }

    public Boolean confirmDataRowExpanded(int index){
        String expandedAttribute = getAttributeValue(findList(uploadDataRow).get(index), "expanded");

        return expandedAttribute.equals("true");
    }

    public void waitForCompletedUpload(){
        logger.info("Waiting for upload to complete.");
        while(isVisible(uploadStatusBar, 5)){
            try {
                Thread.sleep(500);
            } catch (Exception ex){
                Assert.fail("Error waiting for upload to complete:\n " + ex.getMessage());
            }
        }
        logger.info("Upload completed.");
    }

    public void waitForUploadDataLoad(){
        logger.info("Waiting for upload data to load.");

        if(!isVisible(loadingSpinner)){
            Utils.threadSleep(500);
        }

        int i = 0;
        while(isVisible(loadingSpinner)){
            Utils.threadSleep(1000);
            i++;
            if(i == 30){
                Assert.fail("Upload information took longer than 30 seconds to load.");
            }
        }
        Reporter.log("Waited approximately " + i + " seconds for the upload data to load.");
    }

    public LinkedHashMap<String, UploadResultRow> getSearchResults(){
        LinkedHashMap<String, UploadResultRow> appUploadData = new LinkedHashMap<>();
        ArrayList<String> processedElements = new ArrayList<>();

        int listSize;

        boolean eod = false;
        int i = 0;
        while(!eod){
            listSize = findList(uploadDataDiv).size();
            while(listSize == 0){
                Utils.threadSleep(500);
                listSize = findList(uploadDataDiv).size();
            }
            int j = i % listSize;

            String divStyle = getAttributeValue(findList(uploadDataDiv).get(j), "style");
            if(!processedElements.contains(divStyle)) {
                processedElements.add(divStyle);

                UploadResultRow resultRow = getUploadRow(j);

                appUploadData.put(resultRow.getMetadata().getUploadId(), resultRow);

                if (i == 0) {
                    scrollTo(viewUploadButton);
                }

                clickDataRow(j);
                logger.info("Result size - " + appUploadData.size());
                i++;
            } else {
                eod = true;
            }
        }

        return appUploadData;
    }

    public UploadResultRow getUploadRow(int index){
        logger.debug("Retrieving row " + index + " from the list of uploads");
        if(isPresent(uploadDataRow, 10)){
            List<WebElement> dataRow = findList(uploadDataRow);

            //logger.info("Data Row Size: " + dataRow.size());

            while(dataRow.size() == 1){
                logger.info("Data Row size 1.");
                dataRow = findList(uploadDataRow);
            }

            List<WebElement> dataCells = dataRow.get(index).findElements(By.cssSelector("data-table-cell"));

            WebElement image = dataCells.get(0).findElement(By.cssSelector("i"));

            String iconClass = getAttributeValue(image, "class");
            String message = text(dataCells.get(0));
            String uploadTime = text(dataCells.get(1));
            String tailNumber = text(dataCells.get(2));
            String comments = text(dataCells.get(3));
            String duplicateFlights = text(dataCells.get(4));

            UploadMetadata metadata = getUploadMetaData(index);

            return new UploadResultRow(uploadTime, tailNumber, iconClass, comments, message, duplicateFlights, metadata);
        } else {
            Assert.fail("Row " + index + " was not visible in the list of uploads on the Upload Flight page after 10 seconds.");
        }

        return null;
    }

    public UploadMetadata getUploadMetaData(int index){
        logger.info("Retrieving metadata for upload in row " + index + ".");
        clickDataRow(index);

        WebElement metadataData = findList(uploadDataMetadata).get(index);

        if(isVisible(metadataData, 10)){
            List<WebElement> metadataValues = metadataData.findElements(By.cssSelector("p"));
            metadataValues.remove(0);

            String uploadId = trimMetadata(text(metadataValues.get(0)));
            String downloadId = trimMetadata(text(metadataValues.get(1)));
            String name = trimMetadata(text(metadataValues.get(2)));
            String size = trimMetadata(text(metadataValues.get(3)));
            String state = trimMetadata(text(metadataValues.get(4)));
            String processingState = trimMetadata(text(metadataValues.get(5)));
            String processingFailure = trimMetadata(text(metadataValues.get(6)));

            return new UploadMetadata(uploadId, downloadId, name, size, state, processingState, processingFailure);
        }

        return null;
    }

    public void uploadData(String file, String tailNumber, String comments, boolean isCancelled){
        clickUploadButton();
        logger.info("Is upload button clickable - " + isUploadButtonClickable());
        uploadFile( file );
        enterTailNumber( tailNumber );
        enterComments( comments );

        if(isUploadButtonClickable()) {
            clickUploadButton();
        } else {
            Assert.fail("Upload button disabled with valid upload/tail number/comments entered.");
        }

        if(!isCancelled) {
            Assert.assertTrue(isUploadStatusVisible(), "Upload status bar not visible.");
            Assert.assertTrue(confirmUploadStatusText(), "Upload status text not visible.");
            waitForCompletedUpload();
        } else {
            Utils.threadSleep(5000);
            cancelUpload();
            Utils.threadSleep(5000);
            refreshPage();
        }

        waitForUploadDataLoad();
    }

    public CfoqaViewUpload clickViewUploadButton(){
        if(isClickable(viewUploadButton, 10)){
            logger.info("Clicking View Upload button");

            scrollTo(viewUploadButton);

            //clickAtCoordinates(viewUploadButton, 10, 10);
            click(viewUploadButton);

            return new CfoqaViewUpload(driver);
        }

        return null;
    }

    public Boolean isPageLoaded(){
        return isVisible(elementTag);
    }

    public void cancelUpload(){
        logger.info("Canceling the upload.");
        if(isVisible(cancelUpload, 1)){
            click(cancelUpload);
        } else {
            Assert.fail("Cancel Upload button is not visible.");
        }
    }

    public boolean isUploadButtonClickable(){
        String btnClass = getAttributeValue(find(uploadButton), "class");

        return !btnClass.contains("btn--disabled");
    }

    private String trimMetadata(String s){
        return s.substring(s.lastIndexOf(":") + 1).trim();
    }
}
