package com.website.api.mosi.timebooking;

import com.website.api.mosi.google.GoogleSheetsService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class DropdownlistGenerator {
    private List<DropdownListObject> dropdownListObjects;

    public DropdownlistGenerator() throws GeneralSecurityException, IOException {
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


