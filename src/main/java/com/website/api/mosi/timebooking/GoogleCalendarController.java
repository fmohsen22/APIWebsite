package com.website.api.mosi.timebooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            return calendarFetcher.getFreeSlots(weeksFromNow);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching free slots: " + e.getMessage());
        }
    }



    @GetMapping("/dropdown-list")
    public List<DropdownListObject> getDropdownList() {
        return dropdownlistGenerator.getDropdownListObjects();
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