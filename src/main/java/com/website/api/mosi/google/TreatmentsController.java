package com.website.api.mosi.google;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TreatmentsController {

    @GetMapping("/api/treatments")
    public Map<String, Object> getTreatments() {
        Map<String, Object> response = new HashMap<>();
        try {
            GoogleSheetsService googleSheetsService = new GoogleSheetsService();
            JSONObject data = googleSheetsService.mergeAllPriceLists();

            response.put("success", true);
            response.put("data", data.toMap().get("priceList"));

        } catch (IOException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
