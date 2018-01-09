package com.ge.ems.cfoqa.Util;

/**
 * Created by jeff.kramer on 9/5/2017.
 */
public class ViewEventResultRow {
    private Integer flightId;
    private String collectionDate;
    private String event;
    private String tailNumber;

    public ViewEventResultRow(Integer flightId, String collectionDate, String event, String tailNumber) {
        this.flightId = flightId;
        this.collectionDate = collectionDate;
        this.event = event;
        this.tailNumber = tailNumber;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public String getEvent() {
        return event;
    }

    public String getTailNumber() {
        return tailNumber;
    }
}
