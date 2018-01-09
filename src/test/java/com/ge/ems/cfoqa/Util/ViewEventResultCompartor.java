package com.ge.ems.cfoqa.Util;

import java.util.Comparator;

/**
 * Created by jeff.kramer on 9/6/2017.
 */
public class ViewEventResultCompartor implements Comparator<ViewEventResultRow> {

    @Override
    public int compare(ViewEventResultRow o1, ViewEventResultRow o2){
        int sortOne = o1.getFlightId().compareTo(o2.getFlightId());
        if(sortOne == 0){
            return o1.getEvent().compareTo(o2.getEvent());
        }

        return sortOne;
    }
}
