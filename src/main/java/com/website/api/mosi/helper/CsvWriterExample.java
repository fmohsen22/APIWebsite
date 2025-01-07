package com.website.api.mosi.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvWriterExample {

    private static final String CSV_FILE_PATH = "output.csv";

    public void writeToCsv(String functionName, List<Object> ListObjects) {
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH, true)) { // Append mode
            // Get current date and time
            String dateAndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Convert List<DropdownListObject> to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonList = objectMapper.writeValueAsString(ListObjects);

            // Write to CSV file in the specified format
            writer.append(dateAndTime)
                    .append(";")
                    .append(functionName) // Function name (e.g., dropdownList)
                    .append(";")
                    .append(jsonList)
                    .append("\n");

            System.out.println("Data written to CSV successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

}