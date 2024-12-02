package com.website.api.mosi.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads a JSON file and converts it to the specified Java object type.
     *
     * @param filePath The path to the JSON file.
     * @param valueType The class type to map the JSON content to.
     * @param <T> The type of the returned object.
     * @return The object mapped from the JSON file.
     * @throws IOException If there is an error reading or parsing the JSON file.
     */
    public static <T> T readJsonFromFile(String filePath, Class<T> valueType) throws IOException {
        return objectMapper.readValue(new File(filePath), valueType);
    }

    /**
     * Converts a JSON string to the specified Java object type.
     *
     * @param jsonString The JSON string to parse.
     * @param valueType The class type to map the JSON content to.
     * @param <T> The type of the returned object.
     * @return The object mapped from the JSON string.
     * @throws IOException If there is an error parsing the JSON string.
     */
    public static <T> T readJsonFromString(String jsonString, Class<T> valueType) throws IOException {
        return objectMapper.readValue(jsonString, valueType);
    }
}
