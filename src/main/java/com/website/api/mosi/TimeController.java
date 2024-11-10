package com.website.api.mosi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TimeController {

    @GetMapping("/time")
    public Map<String, String> getCurrentTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        Map<String, String> times = new HashMap<>();
        times.put("tehran", LocalDateTime.now(ZoneId.of("Asia/Tehran")).format(formatter));
        times.put("vienna", LocalDateTime.now(ZoneId.of("Europe/Vienna")).format(formatter));
        times.put("sydney", LocalDateTime.now(ZoneId.of("Australia/Sydney")).format(formatter));

        return times;
    }

}
