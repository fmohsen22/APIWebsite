package com.website.api.mosi.google;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.api.mosi.helper.CsvReaderExample;
import com.website.api.mosi.helper.CsvRecord;
import com.website.api.mosi.helper.CsvWriterExample;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
public class GoogleComponentsController {

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

    @GetMapping("/api/important_info")
    public Map<String, Object> getImportantInfo() throws GeneralSecurityException, IOException {
        Map<String, Object> response = new HashMap<>();

        CsvReaderExample csvReader = new CsvReaderExample();
        boolean isAvailable = csvReader.isFunctionAvailable("important_info", 5);

        if (!isAvailable) {
            GoogleDocsService googleDocsService = new GoogleDocsService();
            String documentText = googleDocsService.readDocumentText();

            response.put("info", documentText);

            CsvWriterExample csvWriterExample = new CsvWriterExample();
            csvWriterExample.writeToCsv("important_info", Collections.singletonList(response));

        } else {

            Optional<CsvRecord> latestResult = csvReader.getLatestResult("important_info");
            if (latestResult.isPresent()) {
                String json = latestResult.get().getJson();

                // Parse the JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode rootNode = objectMapper.readTree(json);

                    // Check if the root is an array or an object
                    if (rootNode.isArray()) {
                        // Handle JSON array
                        List<Map<String, Object>> list = objectMapper.convertValue(rootNode, new TypeReference<List<Map<String, Object>>>() {});
                        if (!list.isEmpty()) {
                            response = list.get(0); // Use the first element of the array
                        } else {
                            response.put("error", "The JSON array is empty.");
                        }
                    } else if (rootNode.isObject()) {
                        // Handle JSON object
                        response = objectMapper.convertValue(rootNode, new TypeReference<Map<String, Object>>() {});
                    } else {
                        response.put("error", "Unexpected JSON structure.");
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                    response.put("error", "Failed to parse the JSON response.");
                }
            }
            return response;
        }
        return response;
    }
}

