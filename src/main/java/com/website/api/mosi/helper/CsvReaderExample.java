package com.website.api.mosi.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CsvReaderExample {

    private static final String CSV_FILE_PATH = "output.csv";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Method to read and normalize CSV file
    public List<CsvRecord> readCsvFile() {
        List<CsvRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", 3); // Split into dateandtime, function, and jsonfile
                if (parts.length == 3) {
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0], DATE_TIME_FORMATTER);
                    String function = parts[1];
                    String rawJson = parts[2];

                    // Normalize JSON by removing outer brackets if present
                    String normalizedJson = normalizeJson(rawJson);

                    records.add(new CsvRecord(dateTime, function, normalizedJson));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return records;
    }

    // Normalize JSON to remove extra outer brackets
    private String normalizeJson(String rawJson) {
        if (rawJson.startsWith("[[") && rawJson.endsWith("]]")) {
            return rawJson.substring(1, rawJson.length() - 1); // Remove one layer of brackets
        }
        return rawJson;
    }

    // Check if a function is available within a certain time range (in minutes)
    public boolean isFunctionAvailable(String functionName, int maxMinutes) {
        List<CsvRecord> records = readCsvFile();
        LocalDateTime now = LocalDateTime.now();

        return records.stream()
                .filter(record -> record.getFunction().equals(functionName))
                .anyMatch(record -> Duration.between(record.getDateTime(), now).toMinutes() <= maxMinutes);
    }

    // Get the latest result for a specific function
    public Optional<CsvRecord> getLatestResult(String functionName) {
        List<CsvRecord> records = readCsvFile();

        if (records.size()>50){
            System.out.println("general csv cleaning");
            cleanCsvFile(50);
        }

        // Get all results for the specific function
        List<CsvRecord> functionRecords = records.stream()
                .filter(record -> record.getFunction().equals(functionName))
                .sorted(Comparator.comparing(CsvRecord::getDateTime).reversed()) // Sort by latest date first
                .collect(Collectors.toList());

        // Remove older results if more than 3 exist
        if (functionRecords.size() > 3) {
            cleanOldResults(functionName);
        }

        // Return the latest result if available
        return functionRecords.isEmpty() ? Optional.empty() : Optional.of(functionRecords.get(0));
    }

    // Method to clean up older results
    // Method to clean up all older results for a specific function
    private void cleanOldResults(String functionName) {
        List<CsvRecord> allRecords = readCsvFile();

        // Get the latest record for the specific function
        Optional<CsvRecord> latestRecord = allRecords.stream()
                .filter(record -> record.getFunction().equals(functionName))
                .max(Comparator.comparing(CsvRecord::getDateTime));

        // If no latest record found, return without modifying the CSV
        if (!latestRecord.isPresent()) {
            System.out.println("No records found for function: " + functionName);
            return;
        }

        LocalDateTime latestDateTime = latestRecord.get().getDateTime();

        // Remove all records older than the latest result
        allRecords.removeIf(record -> record.getFunction().equals(functionName) &&
                record.getDateTime().isBefore(latestDateTime));

        // Write the updated records back to the CSV file
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
            for (CsvRecord record : allRecords) {
                writer.write(record.getDateTime().format(DATE_TIME_FORMATTER) + ";" +
                        record.getFunction() + ";" +
                        record.getJson() + "\n");
            }
            System.out.println("Older records cleaned for function: " + functionName);
        } catch (IOException e) {
            System.err.println("Error cleaning old records: " + e.getMessage());
        }
    }

    // Save a specific function's latest result to a text file
    public void saveLatestResultToFile(String functionName, String outputFilePath) {
        Optional<CsvRecord> latestRecord = getLatestResult(functionName);
        latestRecord.ifPresent(record -> {
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.write("Function: " + record.getFunction() + "\n");
                writer.write("Date and Time: " + record.getDateTime() + "\n");
                writer.write("Data: " + record.getJson() + "\n");
                System.out.println("Latest result saved to " + outputFilePath);
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        });
    }

    // Method to clean up the CSV file to retain only the latest n rows
    public void cleanCsvFile(int maxRowsToRetain) {
        List<CsvRecord> allRecords = readCsvFile();

        // Check if the file exceeds the desired row count
        if (allRecords.size() > maxRowsToRetain) {
            // Sort records by date in descending order
            List<CsvRecord> latestRecords = allRecords.stream()
                    .sorted(Comparator.comparing(CsvRecord::getDateTime).reversed())
                    .limit(maxRowsToRetain)
                    .collect(Collectors.toList());

            // Write only the latest records back to the file
            try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
                for (CsvRecord record : latestRecords) {
                    writer.write(record.getDateTime().format(DATE_TIME_FORMATTER) + ";" +
                            record.getFunction() + ";" +
                            record.getJson() + "\n");
                }
                System.out.println("CSV file cleaned. Retained the latest " + maxRowsToRetain + " rows.");
            } catch (IOException e) {
                System.err.println("Error writing cleaned CSV file: " + e.getMessage());
            }
        } else {
            System.out.println("CSV file does not exceed " + maxRowsToRetain + " rows. No cleaning needed.");
        }
    }

    public static void main(String[] args) {
        CsvReaderExample csvReader = new CsvReaderExample();
        csvReader.cleanCsvFile(3);

//        // Example 1: Check if "free-slots" is available within the last 5 minutes
//        boolean isAvailable = csvReader.isFunctionAvailable("freeslots_in_parking", 5);
//        System.out.println("Is 'free-slots' available in the last 5 minutes? " + isAvailable);
//
//        // Example 2: Get the latest result for "free-slots"
//        Optional<CsvRecord> latestResult = csvReader.getLatestResult("freeslots_in_parking");
//        latestResult.ifPresent(record -> {
//            System.out.println("Latest result for 'freeslots_in_parking':");
//            System.out.println("Date and Time: " + record.getDateTime());
//            System.out.println("JSON Data: " + record.getJson());
//        });
//
//        // Example 3: Save the latest result of "dropdown-list" to a text file
//        csvReader.saveLatestResultToFile("dropdown-list", "latest-dropdown-list.txt");
    }
}

// Helper class to represent a CSV record
