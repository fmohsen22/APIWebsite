package com.website.api.mosi.timebooking;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

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

public class CalendarFetcher {
    private static final String APPLICATION_NAME = "Envisage";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private Calendar calendarService;
    private  final String calendarId= "sfaranak935@gmail.com";

    public CalendarFetcher() throws GeneralSecurityException, IOException {
//        GoogleCredentials credentials = GoogleCredentials
//                .fromStream(new FileInputStream("src/main/resources/envisage-443710-5eaaca6c210f.json"))
//                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

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

    public String bookAppointment(String title, String description, String startDateTime, String endDateTime, String attendeeEmail) throws IOException, GeneralSecurityException {
        // Fetch credentials from environment variables
        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        String refreshToken = System.getenv("GOOGLE_REFRESH_TOKEN");

        // Use the refresh token to obtain an access token
        GoogleTokenResponse tokenResponse;
        try {
            tokenResponse = new GoogleRefreshTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    refreshToken,
                    clientId,
                    clientSecret
            ).execute();
        } catch (TokenResponseException e) {
            throw new IOException("Failed to obtain access token. Verify your refresh token and credentials.", e);
        }

        // Create a credential with the access token
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(tokenResponse.getAccessToken());

        // Build the Calendar service using the credential
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Envisage").build();

        // Create the event
        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        // Set start and end times
        event.setStart(new EventDateTime().setDateTime(new DateTime(startDateTime)).setTimeZone("Australia/Sydney"));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(endDateTime)).setTimeZone("Australia/Sydney"));

        // Add attendees
        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail(attendeeEmail)
        };
        event.setAttendees(List.of(attendees));

        // Add reminders
        event.setReminders(new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(List.of(
                        new EventReminder().setMethod("email").setMinutes(24 * 60),
                        new EventReminder().setMethod("popup").setMinutes(10)
                )));

        // Insert the event into the calendar
        try {
            event = service.events().insert("primary", event).execute();
            return "Event created: " + event.getHtmlLink();
        } catch (IOException e) {
            throw new IOException("Failed to create event. Ensure the service account has access.", e);
        }
    }
    public static void main(String[] args) {
        try {
            // Initialize CalendarFetcher
            CalendarFetcher calendarFetcher = new CalendarFetcher();

            // Get free slots
            List<String> freeSlots = calendarFetcher.getFreeSlots(8);

            System.out.println("Free Slots:");
            freeSlots.forEach(System.out::println);

            // Prepare the booking details
            try {
                String _result = calendarFetcher.bookAppointment(
                        "Automated Appointment",
                        "This is a test event created with automated OAuth.",
                        "2024-12-17T10:00:00+10:00", // Start time
                        "2024-12-17T11:00:00+10:00", // End time
                        "example@example.com"        // Attendee email
                );
                System.out.println(_result);
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
            }

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }}
