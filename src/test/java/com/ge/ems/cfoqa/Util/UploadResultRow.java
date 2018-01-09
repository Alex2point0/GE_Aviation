package com.ge.ems.cfoqa.Util;

public class UploadResultRow {
    private String uploadTime;
    private String tailNumber;
    private String iconClass;
    private String comments;
    private String errorMsg;
    private String duplicateFlights;
    private UploadMetadata metadata;

    public UploadResultRow(String uploadTime, String tailNumber, String iconClass, String comments, String errorMsg, String duplicateFlights, UploadMetadata metadata){
        this.uploadTime = uploadTime;
        this.tailNumber = tailNumber;
        this.iconClass = iconClass;
        this.comments = comments;
        this.errorMsg = errorMsg;
        this.duplicateFlights = duplicateFlights;
        this.metadata = metadata;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getComments() { return comments; }

    public String getErrorMsg() { return errorMsg; }

    public String getDuplicateFlights() { return duplicateFlights; }

    public UploadMetadata getMetadata() { return metadata; }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setDuplicateFlights(String duplicateFlights) {
        if(duplicateFlights.equals("0")){
            duplicateFlights = "Not Available";
        }

        this.duplicateFlights = duplicateFlights;
    }

    public void setMetadata(UploadMetadata metadata) {
        this.metadata = metadata;
    }
}
