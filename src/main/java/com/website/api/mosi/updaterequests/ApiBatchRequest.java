package com.website.api.mosi.updaterequests;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Component
public class ApiBatchRequest {

    private static final List<String> GET_ENDPOINTS = List.of(
            //"https://apiwebsite-1.onrender.com/api/calendar/dropdown-list",
            "http://localhost:8080/api/calendar/dropdown-list",
            "http://localhost:8080/api/trafficReport",
            "http://localhost:8080/api/freeslots_in_parking",
            "http://localhost:8080/api/important_info"
    );

    // Map of POST endpoints and their respective payloads
    private static final Map<String, String> POST_ENDPOINTS = Map.of(
            "http://localhost:8080/api/calendar/free-slots", "8" // Sending a plain number as payload
 //           "https://apiwebsite-1.onrender.com/api/calendar/appointments", "{ \"key\": \"value\" }" // Example JSON payload
    );

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes (300,000 ms)
    public void runBatch() {
        // Handle GET Requests
        for (String endpoint : GET_ENDPOINTS) {
            try {
                sendGetRequest(endpoint);
            } catch (Exception e) {
                System.err.println("Error during GET request to " + endpoint + ": " + e.getMessage());
            }
        }

        // Handle POST Requests
        for (Map.Entry<String, String> entry : POST_ENDPOINTS.entrySet()) {
            try {
                sendPostRequest(entry.getKey(), entry.getValue()); // Send the endpoint and its payload
            } catch (Exception e) {
                System.err.println("Error during POST request to " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    private void sendGetRequest(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            System.out.println("GET request to " + endpoint + " succeeded.");
        } else {
            System.err.println("GET request to " + endpoint + " failed with response code: " + responseCode);
        }

        connection.disconnect();
    }

    private void sendPostRequest(String endpoint, String payload) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true); // Enable output for POST
        connection.setRequestProperty("Content-Type", "application/json"); // Adjust Content-Type as needed
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);

        // Write the payload
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("POST request to " + endpoint + " succeeded.");
        } else {
            System.err.println("POST request to " + endpoint + " failed with response code: " + responseCode);
        }

        connection.disconnect();
    }
}