package com.website.api.mosi.timebooking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.api.mosi.helper.CsvReaderExample;
import com.website.api.mosi.helper.CsvRecord;
import com.website.api.mosi.helper.CsvWriterExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.website.api.mosi.helper.TimeManager.addMinutesToTime;
import static com.website.api.mosi.helper.TimeManager.timeFormatter;

@CrossOrigin(origins = {
        "http://localhost:8000",
        "http://localhost:63342",
        "http://127.0.0.1:8000",
        "https://fmohsen22.github.io",
        "https://localhost:8080"
})
@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final CalendarFetcher calendarFetcher;

    private final DropdownlistGenerator dropdownlistGenerator;

    @Autowired
    public GoogleCalendarController() throws GeneralSecurityException, IOException {
        this.calendarFetcher = new CalendarFetcher();
        this.dropdownlistGenerator = new DropdownlistGenerator();
    }

    @PostMapping("/free-slots")
    public List<String> getFreeSlots(@RequestBody int weeksFromNow) {
        try {

            List<String> response= calendarFetcher.getFreeSlots(weeksFromNow);
            CsvWriterExample csvWriterExample = new CsvWriterExample();
            csvWriterExample.writeToCsv("free-slots", Collections.singletonList(response));


            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching free slots: " + e.getMessage());
        }
    }



    @GetMapping("/dropdown-list")
    public List<DropdownListObject> getDropdownList() {

        CsvReaderExample csvReader = new CsvReaderExample();

        // Check if the function "dropdown-list" is available within the last 5 minutes
        boolean isAvailable = csvReader.isFunctionAvailable("dropdown-list", 5);
        List<DropdownListObject> dropdownListObjects = Collections.emptyList(); // Default to an empty list

        if (!isAvailable) {
            // Generate a new list if not available
            dropdownListObjects = dropdownlistGenerator.getDropdownListObjects(); // Assuming this method generates the list
            CsvWriterExample csvWriterExample = new CsvWriterExample();
            csvWriterExample.writeToCsv("dropdown-list", Collections.singletonList(dropdownListObjects));
        } else {
            // Get the latest result for "dropdown-list" from the CSV file
            Optional<CsvRecord> latestRecord = csvReader.getLatestResult("dropdown-list");
            if (latestRecord.isPresent()) {
                String json = latestRecord.get().getJson();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // Convert JSON string to List<DropdownListObject>
                    dropdownListObjects = objectMapper.readValue(json, new TypeReference<List<DropdownListObject>>() {});
                } catch (Exception e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                }
            }
        }

        return dropdownListObjects;
    }


    public boolean isTimeSlotAvailable(String startDateTime, String endDateTime) throws IOException {



        // Parse beginn and end into LocalDateTime
        LocalDateTime beginnTime = timeFormatter(startDateTime);
        LocalDateTime endTime = timeFormatter(endDateTime);

        // Iterate through the free slots
        for (String slot : getFreeSlots(8)) {
            // Split the free slot into start and end
            String[] parts = slot.split(" to ");
            if (parts.length != 2) continue; // Skip invalid entries

            LocalDateTime slotStart = LocalDateTime.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime slotEnd = LocalDateTime.parse(parts[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            // Check if the requested time is fully within the free slot
            if (!beginnTime.isBefore(slotStart) && !endTime.isAfter(slotEnd)) {
                return true; // The time slot is available
            }
        }

        return false; // No matching free slot found
    }

    @PostMapping("/appointments")
    public ResponseEntity<String> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        System.out.println("Received appointment: " + appointmentRequest);

        try {
            String beginn = "";
            String end = "";

            // Split the time range into two parts
            String[] parts = appointmentRequest.getTimeSlot().split(" to ");
            if (parts.length == 2) {
                beginn = parts[0];
                end = parts[1];
            } else {
                System.out.println("Invalid time range format.");
                return ResponseEntity.badRequest().body("{\"message\": \"Invalid time slot format.\"}");
            }

            // Check for conflicting events
            boolean isSlotAvailable = isTimeSlotAvailable(beginn, end);
            if (!isSlotAvailable) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"message\": \"The time slot is already booked. Please choose another slot.\"}");
            }

            // Proceed to book the appointment
            String _result = calendarFetcher.bookAppointment(
                    "Automated Appointment for " + appointmentRequest.getFullName(),
                    "This appointment is created by " + appointmentRequest.getFullName() +
                            " for " + appointmentRequest.getTreatment() +
                            ". Customer Contact info: " + appointmentRequest.getEmail() + ", and phone number: " + appointmentRequest.getPhone() +
                            ". Created at: " + LocalDateTime.now(ZoneId.of("Australia/Sydney")),
                    beginn, // Start time
                    addMinutesToTime(beginn, appointmentRequest.getDuration()), // End time
                    appointmentRequest.getEmail() // Attendee email
            );
            System.out.println(_result);

            return ResponseEntity.ok("{\"message\": \"Appointment successfully created. Thank you! In case you want to change the appointment, please call us.\"}");

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"An error occurred while creating the appointment. Please try again later.\"}");
        }
    }

}