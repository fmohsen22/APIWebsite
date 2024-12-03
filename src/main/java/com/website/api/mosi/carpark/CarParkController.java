package com.website.api.mosi.carpark;

import com.website.api.mosi.api.rest.GenericHttpClient;
import com.website.api.mosi.helper.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> getFreeSlotsInParking(){

        CarParkResponse carParkResponse=null;
        try {
            carParkResponse = JsonHelper.readJsonFromString(getCarParkData().getBody(), CarParkResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        int totalSpots = carParkResponse.getSpots();
        int OccupiedSpots = carParkResponse.getOccupancy().getTotal();

        int freeSlots= totalSpots - OccupiedSpots;


        // Construct the JSON response
        Map<String, String> response = new HashMap<>();
        response.put("inf", String.format(
                "Currently, %d Parking Slots are available near the %s shop.",
                freeSlots,carParkResponse.getFacilityName()
        ));


        return response;
    }
}

