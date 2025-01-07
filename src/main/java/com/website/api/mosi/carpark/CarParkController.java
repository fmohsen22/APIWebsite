package com.website.api.mosi.carpark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.api.mosi.api.rest.GenericHttpClient;
import com.website.api.mosi.helper.CsvReaderExample;
import com.website.api.mosi.helper.CsvRecord;
import com.website.api.mosi.helper.CsvWriterExample;
import com.website.api.mosi.helper.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class CarParkController {

    @Autowired
    private GenericHttpClient httpClient;

    @GetMapping("/api/carpark")
    public ResponseEntity<String> getCarParkData() {
        // Define the URL and headers
        String url = "https://api.transport.nsw.gov.au/v1/carpark?facility=31";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "apikey eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJWOERjaDVSdjVRZXFYanI0Qkcxa2JiQTM5STFiOHN0U29DNjVMbXJXT3NnIiwiaWF0IjoxNzMzMDgxMzcwfQ.gC6jQeMEzrl5lksyOcJgb0Vt83ofUkhYDwB8nbGIIUY");

        // Send GET request
        return httpClient.sendRequest(url, HttpMethod.GET, headers, null);
    }

    @GetMapping("/api/freeslots_in_parking")
    public Map<String, String> getFreeSlotsInParking() {
        CsvReaderExample csvReader = new CsvReaderExample();

        // Check if the function "freeslots_in_parking" is available within the last 5 minutes
        boolean isAvailable = csvReader.isFunctionAvailable("freeslots_in_parking", 5);
        Map<String, String> response = new HashMap<>();

        if (!isAvailable) {
            // Fetch new data if not available
            CarParkResponse carParkResponse;
            try {
                carParkResponse = JsonHelper.readJsonFromString(getCarParkData().getBody(), CarParkResponse.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading car park data: " + e.getMessage());
            }

            int totalSpots = carParkResponse.getSpots();
            int occupiedSpots = carParkResponse.getOccupancy().getTotal();
            int freeSlots = totalSpots - occupiedSpots;

            // Construct the new response
            response.put("info", String.format(
                    "Currently, %d Parking Slots are available near the %s shop.",
                    freeSlots, carParkResponse.getFacilityName()
            ));

            // Write the new data to the CSV
            CsvWriterExample csvWriter = new CsvWriterExample();
            csvWriter.writeToCsv("freeslots_in_parking", Collections.singletonList(response));
        } else {
            // Get the latest result for "freeslots_in_parking" from the CSV
            Optional<CsvRecord> latestRecord = csvReader.getLatestResult("freeslots_in_parking");
            if (latestRecord.isPresent()) {
                String json = latestRecord.get().getJson();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // Convert JSON string to Map<String, String>
                    response = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
                } catch (Exception e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                    response.put("error", "Failed to parse cached response.");
                }
            }
        }

        return response;
    }
}

