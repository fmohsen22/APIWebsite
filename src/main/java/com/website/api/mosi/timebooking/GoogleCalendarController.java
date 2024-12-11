package com.website.api.mosi.timebooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final FreeTimeFetcher freeTimeFetcher;

    private final DropdownlistGenerator dropdownlistGenerator;

    @Autowired
    public GoogleCalendarController() throws GeneralSecurityException, IOException {
        this.freeTimeFetcher = new FreeTimeFetcher();
        this.dropdownlistGenerator = new DropdownlistGenerator();
    }

    @PostMapping("/free-slots")
    public List<String> getFreeSlots(@RequestBody int weeksFromNow) {
        try {
            return freeTimeFetcher.getFreeSlots(weeksFromNow);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching free slots: " + e.getMessage());
        }
    }



    @GetMapping("/dropdown-list")
    public List<DropdownListObject> getDropdownList() {
        return dropdownlistGenerator.getDropdownListObjects();
    }

}