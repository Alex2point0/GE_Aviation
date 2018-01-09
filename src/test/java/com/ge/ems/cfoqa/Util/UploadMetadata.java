package com.ge.ems.cfoqa.Util;

/**
 * Created by jeff.kramer on 8/7/2017.
 */
public class UploadMetadata {
    private String uploadId;
    private String downloadId;
    private String name;
    private String size;
    private String state;
    private String processingState;
    private String processingFailure;

    public UploadMetadata(String uploadId, String downloadId, String name, String size, String state, String processingState, String processingFailure) {
        this.uploadId = uploadId;
        this.downloadId = downloadId;
        this.name = name;
        this.size = size;
        this.state = state;
        this.processingState = processingState;
        this.processingFailure = processingFailure;
    }

    public String getUploadId() {
        return uploadId;
    }

    public String getDownloadId() { return downloadId; }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getState() {
        return state;
    }

    public String getProcessingState() {
        return processingState;
    }

    public String getProcessingFailure() {
        return processingFailure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDownloadId(String downloadId) { this.downloadId = downloadId; }

    public void setProcessingState(String processingState) {
        if(!processingState.equals("notProcessed")) {
            this.processingState = processingState;
        } else {
            this.processingState = "";
        }
    }

    public void setProcessingFailure(String processingFailure) {
        this.processingFailure = processingFailure;
    }
}
