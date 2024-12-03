package com.website.api.mosi.traffic;

import com.website.api.mosi.api.rest.GenericHttpClient;
import com.website.api.mosi.carpark.CarParkResponse;
import com.website.api.mosi.helper.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TrafficController {

    @Autowired
    private GenericHttpClient httpClient;

    @GetMapping("/api/traffic_rowdata")
    public ResponseEntity<String> getTrafficInfoNearMyShop(){

        String url = "https://api.tomtom.com/traffic/services/4/flowSegmentData/absolute/10/json?key=g9TAlTY4DvfKQkfMhkgS8PXy1KVDUL4O&point=-33.730279,150.967280";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // Send GET request
        return httpClient.sendRequest(url, HttpMethod.GET, headers, null);

    }

    @GetMapping("/api/trafficReport")
    public Map<String, String> getFreeSlotsInParking(){

        TrafficResponse trafficResponse=null;
        try {
            trafficResponse = JsonHelper.readJsonFromString(getTrafficInfoNearMyShop().getBody(), TrafficResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        int currentSpeed = trafficResponse.getFlowSegmentData().getCurrentSpeed();
        int freeFlowSpeed = trafficResponse.getFlowSegmentData().getFreeFlowSpeed();
        boolean incident = trafficResponse.getFlowSegmentData().isRoadClosure();

        String info =generateTrafficStatusMessage(currentSpeed,freeFlowSpeed,incident);




        // Construct the JSON response
        Map<String, String> response = new HashMap<>();
        response.put("inf", info);


        return response;
    }

    public static String generateTrafficStatusMessage(int currentSpeed, int freeFlowSpeed, boolean isRoadClosure) {

        // Check for road closure
        if (isRoadClosure) {
            return "The road near 7 Maitland Place is currently closed. Please use an alternate route.";
        }

        // Calculate the traffic percentage
        float trafficPercentage = ((float) currentSpeed / freeFlowSpeed) * 100;

        // Determine traffic classification
        String trafficCondition;
        if (trafficPercentage >= 80) {
            trafficCondition = "Smooth Traffic";
        } else if (trafficPercentage >= 50) {
            trafficCondition = "Moderate Traffic";
        } else {
            trafficCondition = "Heavy Traffic";
        }

        // Generate dynamic message
        return String.format(
                "Traffic near 7 Maitland Place is currently %s with a speed of %d km/h. %s",
                trafficCondition,
                currentSpeed,
                trafficCondition.equals("Heavy Traffic") ? "Expect significant delays." :
                        trafficCondition.equals("Moderate Traffic") ? "You might experience slight delays." :
                                "Traffic is flowing smoothly."
        );
    }


}
