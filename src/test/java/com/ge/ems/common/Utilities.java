package com.ge.ems.common;

import com.ge.ems.api.commons.PropertiesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by jeff.kramer on 8/10/2017.
 */
public class Utilities {

    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);
    
    public static final String API_ASYNC_QUERY_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String API_QUERY_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";
    public static final String APP_QUERY_DATE_PATTERN = "yyyy-MMM-dd";

    private static final String DUPLICATE_DETECTION_SCRIPT = "\\src\\test\\resources\\cfoqa\\scripts\\Activate-DuplicationDetection.ps1\"" +
            " -Username " + PropertiesHandler.properties.getProperty(PropertiesHandler.USERNAME) +
            " -Password " + PropertiesHandler.properties.getProperty(PropertiesHandler.PASSWORD) +
            " -ComputerName " + PropertiesHandler.properties.getProperty(PropertiesHandler.EMS_SYSTEM) +
            " -active ";

    private static final String DICE_AND_DEIDENTIFY_MIN_FLIGHT_HOURS_SCRIPT = "\\src\\test\\resources\\cfoqa\\scripts\\Alter-DiceAndDeidentifyMinFlightHours.ps1\"" +
            " -Username " + PropertiesHandler.properties.getProperty(PropertiesHandler.USERNAME) +
            " -Password " + PropertiesHandler.properties.getProperty(PropertiesHandler.PASSWORD) +
            " -ComputerName " + PropertiesHandler.properties.getProperty(PropertiesHandler.EMS_SYSTEM) +
            " -MinFlightHours ";

    public static final String FOQA_FLIGHTS_DATABASE = "[ems-core][entity-type][foqa-flights]";
    public static final String FOQA_DOWNLOADS_DATABASE = "[ems-core][entity-type][foqa-downloads]";
    public static final String APM_EVENTS_DATABASE = "[ems-apm][entity-type][events:profile-<profile_id>]";

    public static Boolean activateDuplicateDetection(){
        logger.info("Activating Duplicate Detection on " + PropertiesHandler.properties.getProperty(PropertiesHandler.EMS_SYSTEM));
        return runPowerShellCommand(DUPLICATE_DETECTION_SCRIPT + "\"true\"");
    }

    public static Boolean deactivateDuplicateDetection(){
        logger.info("Deactivating Duplicate Detection on " + PropertiesHandler.properties.getProperty(PropertiesHandler.EMS_SYSTEM));
        return runPowerShellCommand(DUPLICATE_DETECTION_SCRIPT + "\"false\"");
    }

    public static Boolean setDiceAndDeidentifyMinFlightHours(String minFlightHours){
        logger.info("Setting minimum length of flight required for a valid flight property in Flight Dice and Deidentify to " + minFlightHours);
        return runPowerShellCommand(DICE_AND_DEIDENTIFY_MIN_FLIGHT_HOURS_SCRIPT + minFlightHours);
    }

    private static Boolean runPowerShellCommand(String script){
        String command = "powershell.exe \"" + System.getProperty("user.dir").replace(" ", "`\" \"") + script;

        System.out.println("Running command:" + command);
        try {
            Process psProcess = Runtime.getRuntime().exec(command);
            psProcess.getOutputStream().close();
            String line;
            System.out.println("Output:");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(psProcess.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                System.out.println(line);
            }
            stdout.close();
            System.out.println("Error:");
            BufferedReader stderr = new BufferedReader(new InputStreamReader(psProcess.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                System.out.println(line);
            }
            stderr.close();
            System.out.println("Done");
        } catch (IOException ioex) {
            return false;
        }

        return true;
    }

    public static String formatAPIDateAsAppDate(LocalDateTime apiTime, ZoneId apiTZ, ZoneId targetTZ){
        DateTimeFormatter appFormatter = DateTimeFormatter.ofPattern(APP_QUERY_DATE_PATTERN);

        ZonedDateTime utcDateTime = apiTime.atZone(apiTZ);
        ZonedDateTime systemDateTime = utcDateTime.withZoneSameInstant(targetTZ);
        String date = systemDateTime.format(appFormatter);

        return date.substring(0, 5) + date.substring(5, 8).toUpperCase() + date.substring(8);
    }

    public static ZoneId getSystemDefaultZoneId(){
        return ZoneId.systemDefault();
    }
}
