package com.website.api.mosi.timebooking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.website.api.mosi.google.GoogleSheetsService;
import com.website.api.mosi.helper.CacheManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class DropdownlistGenerator {
    private List<DropdownListObject> dropdownListObjects;

    public DropdownlistGenerator() throws GeneralSecurityException, IOException {
        // Define a cache key for the dropdown list
        String cacheKey = "dropdownList";

        // Check if the data is in the cache
        String cachedData = CacheManager.get(cacheKey);

        if (cachedData != null) {
            System.out.println("Serving dropdown list from cache...");
            Type listType = new TypeToken<List<DropdownListObject>>() {}.getType();
            dropdownListObjects = new Gson().fromJson(cachedData, listType);
            return;
        }

        dropdownListObjects = new ArrayList<>();
        GoogleSheetsService googleSheetsService = new GoogleSheetsService();

        for (var topic : googleSheetsService.getSheetNames()) {

            List<List<Object>> lists = googleSheetsService.getSheetData(topic);

            List<Treatments> treatmentsList = new ArrayList<>();
            for (var detail : lists) {
                if (detail.get(0).equals("Treatment Name") ||
                        detail.get(1).toString().startsWith("Price") ||
                        detail.get(3).toString().startsWith("Duration"))
                    continue;

                Treatments treatments = new Treatments();
                treatments.setTreatment(detail.get(0).toString());
                treatments.setPrice(Double.valueOf((String) detail.get(1)));
                treatments.setDescripton(detail.get(2).toString());
                treatments.setDuration(Integer.parseInt(detail.get(3).toString()));

                treatmentsList.add(treatments);
            }
                dropdownListObjects.add(new DropdownListObject(treatmentsList,topic));
        }
    }

    public List<DropdownListObject> getDropdownListObjects() {
        return dropdownListObjects;
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
    DropdownlistGenerator dropdown = new DropdownlistGenerator();

    System.out.println(dropdown.toString());

}
}


