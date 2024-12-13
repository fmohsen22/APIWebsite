package com.website.api.mosi.helper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
public class TimeManager {
    public static String addMinutesToTime(String firstTime,int addMinToOriginalTime) {


        // Parse the time string into a ZonedDateTime object
        ZonedDateTime time = ZonedDateTime.parse(firstTime);

        // Add minutes
        ZonedDateTime updatedTime = time.plusMinutes(addMinToOriginalTime);

        // Convert back to string

        return updatedTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }


    public static LocalDateTime timeFormatter( String inputDateTime){
        // Input string in UTC (with Z)
        //String inputDateTime = "2024-12-14T08:00:00.000Z";

        // Parse the string as a ZonedDateTime in UTC
        ZonedDateTime utcDateTime = ZonedDateTime.parse(inputDateTime);

        // Convert to Sydney time
        return utcDateTime.withZoneSameInstant(ZoneId.of("Australia/Sydney")).toLocalDateTime();
    }
}
