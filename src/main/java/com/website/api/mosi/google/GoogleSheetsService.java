package com.website.api.mosi.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSheetsService {
    private static final String APPLICATION_NAME = "Envisage Cosmetic Clinic";
    private static final String SPREADSHEET_ID = "1yMlJwnJL7CisTRku1U0etbUTRTxoam0QGOSOKBJNwW4";
    private static final String RANGE = "!A:D"; // Range for all tabs

    private Sheets sheetsService;

    public GoogleSheetsService() throws IOException, GeneralSecurityException {
//        GoogleCredentials credentials = GoogleCredentials
//                .fromStream(new FileInputStream("src/main/resources/envisage-443710-5eaaca6c210f.json"))
//                .createScoped(List.of(SheetsScopes.SPREADSHEETS_READONLY));

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")))
                .createScoped(List.of(SheetsScopes.SPREADSHEETS_READONLY));

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        sheetsService = new Sheets.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Method to fetch data from a specific sheet
    public List<List<Object>> getSheetData(String sheetName) throws IOException {
        String range = "'" + sheetName + "'" + RANGE; // Include the tab name in the range
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        return response.getValues();
    }

    // Method to fetch all sheet names
    public List<String> getSheetNames() throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
        List<String> sheetNames = new ArrayList<>();

        spreadsheet.getSheets().forEach(sheet -> {
            sheetNames.add(sheet.getProperties().getTitle());
        });

        return sheetNames;
    }

    // Method to merge all data into the desired JSON structure
    public JSONObject mergeAllPriceLists() throws IOException, GeneralSecurityException {
        List<String> sheetNames = getSheetNames();
        JSONArray categories = new JSONArray();

        for (String sheetName : sheetNames) {
            List<List<Object>> sheetData = getSheetData(sheetName);

            if (sheetData != null && sheetData.size() > 1) { // Skip if no data or only headers
                JSONObject category = new JSONObject();
                JSONArray treatments = new JSONArray();

                for (int i = 1; i < sheetData.size(); i++) { // Skip the header row
                    List<Object> row = sheetData.get(i);
                    JSONObject treatment = new JSONObject();

                    treatment.put("name", row.size() > 0 ? row.get(0) : "N/A");
                    treatment.put("price", row.size() > 1 ? row.get(1) : "N/A");
                    treatment.put("description", row.size() > 2 ? row.get(2) : "N/A");
                    treatment.put("duration", row.size() > 3 ? row.get(3) : "N/A");

                    treatments.put(treatment);
                }

                category.put("category", sheetName); // Use sheet name as category
                category.put("treatments", treatments);

                categories.put(category);
            }
        }

        JSONObject result = new JSONObject();
        result.put("priceList", categories);
        return result;
    }

    public static void main(String[] args) {
        try {
            GoogleSheetsService googleSheetsService = new GoogleSheetsService();
            JSONObject priceList = googleSheetsService.mergeAllPriceLists();
            System.out.println(priceList.toString(2)); // Pretty-print JSON
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}