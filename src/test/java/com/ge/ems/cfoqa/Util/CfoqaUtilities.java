package com.ge.ems.cfoqa.Util;

import com.ge.ems.common.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jeff.kramer on 4/24/2017.
 */
public class CfoqaUtilities {

    private final static Logger logger = LoggerFactory.getLogger(CfoqaUtilities.class);

    public static final String API_ASYNC_QUERY_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String API_QUERY_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";
    public static final String APP_QUERY_DATE_PATTERN = "yyyy-MMM-dd";

    public static String formatAPIDateAsAppDate(LocalDateTime apiTime, ZoneId apiTZ, ZoneId targetTZ){
        DateTimeFormatter appFormatter = DateTimeFormatter.ofPattern(Utilities.APP_QUERY_DATE_PATTERN);

        ZonedDateTime utcDateTime = apiTime.atZone(apiTZ);
        ZonedDateTime systemDateTime = utcDateTime.withZoneSameInstant(targetTZ);
        String date = systemDateTime.format(appFormatter);

        return date.substring(0, 5) + date.substring(5, 8).toUpperCase() + date.substring(8);
    }

    public static ZoneId getSystemDefaultZoneId(){
        return ZoneId.systemDefault();
    }

    public static String formatAPIDateAsEventContextDate(String date){
        int splitSpot = date.indexOf("T");
        date = date.substring(0, splitSpot);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date tempDate = df.parse(date);
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTime(tempDate);
            date = new SimpleDateFormat("EEE MMM dd yyyy").format(cal.getTime());
        } catch (Exception ex){
            Assert.fail("Error parsing date - " + ex);
        }

        return date;
    }

    public static String convertToKBString(Long bytes){
        logger.info("Converting " + bytes.toString() + " to KB...");
        DecimalFormat df = new DecimalFormat("#.##");

        Double b = bytes.doubleValue();
        Double kb = BigDecimal.valueOf(b / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

        return df.format(kb) + "KB";
    }
}
