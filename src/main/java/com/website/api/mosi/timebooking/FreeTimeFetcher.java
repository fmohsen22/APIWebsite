package com.website.api.mosi.timebooking;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FreeTimeFetcher {
    private static final String APPLICATION_NAME = "Your Application Name";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private Calendar calendarService;
    private  final String calendarId= "sfaranak935@gmail.com";

    public FreeTimeFetcher() throws GeneralSecurityException, IOException {
//        GoogleCredentials credentials = GoogleCredentials
//                .fromStream(new FileInputStream("src/main/resources/envisage-443710-5eaaca6c210f.json"))
//                .createScoped(Collections.singleton(CalendarScopes.CALENDAR_READONLY));


        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR_READONLY));


        calendarService = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public List<String> getFreeSlots( int numberOfWeeksFromNow) throws IOException {
        List<String> freeSlots = new ArrayList<>();

        // the search always start from today and now
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusWeeks(numberOfWeeksFromNow);

                // Format dates to ISO 8601
        String timeMin = startDateTime.atZone(ZoneId.of("Australia/Sydney")).toInstant().toString();
        String timeMax = endDateTime.atZone(ZoneId.of("Australia/Sydney")).toInstant().toString();

        // Retrieve events within the time range
        Events events = calendarService.events().list(calendarId)
                .setTimeMin(DateTime.parseRfc3339(timeMin))
                .setTimeMax(DateTime.parseRfc3339(timeMax))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();

        // Compare the gaps between events to find free slots
        LocalDateTime current = startDateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Event event : items) {
            if (event.getStart() != null && event.getStart().getDateTime() != null) {

                String str = event.getStart().getDateTime().toString();

                // Use OffsetDateTime for parsing ISO-8601 formatted strings with a time zone
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(str);

                // Convert OffsetDateTime to LocalDateTime (without time zone)
                LocalDateTime eventStart = offsetDateTime.toLocalDateTime();

                System.out.println("Parsed LocalDateTime: " + eventStart);
                if (current.isBefore(eventStart)) {
                    freeSlots.add(current.format(formatter) + " to " + eventStart.format(formatter));
                }

                offsetDateTime = OffsetDateTime.parse(event.getEnd().getDateTime().toString());

                // Convert OffsetDateTime to LocalDateTime (without time zone)
                current = offsetDateTime.toLocalDateTime();
            }
        }

        if (items.isEmpty()) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime start = startDateTime.withHour(9).withMinute(0);
            LocalDateTime end = endDateTime.withHour(17).withMinute(0);
            System.out.println("Free Slot: " + start.format(formatter) + " to " + end.format(formatter));
        }
        // Check for free time after the last event
        if (current.isBefore(endDateTime)) {
            freeSlots.add(current.format(formatter) + " to " + endDateTime.format(formatter));
        }

        return freeSlots;
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        FreeTimeFetcher freeTimeFetcher = new FreeTimeFetcher();

        // Define the start and end dates
        //LocalDateTime eightWeeksLater = now.plusWeeks(8);
        //LocalDateTime nextTime =LocalDateTime.of(2024,12,20,17,0);


        List<String> freeSlots = freeTimeFetcher.getFreeSlots(8);

        System.out.println("Free Slots:");
        freeSlots.forEach(System.out::println);


        JSONObject result = new JSONObject();
        int i=1;
        for (var slot:freeSlots){
            result.put("free_slot_"+i++,slot.toString());
        }
        System.out.println(result);
    }
}
