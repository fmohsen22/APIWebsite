package com.website.api.mosi;

import com.website.api.mosi.api.rest.GenericHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final GenericHttpClient httpClient;

    // Constructor Injection for GenericHttpClient
    public ChatController(GenericHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> receiveMessage(@RequestBody ChatMessage message) {
        // Get the message text sent by the user
        String userText = message.getUser_input();

        // Prepare headers for the HTTP request
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // Make a POST request to the external API
        ResponseEntity<String> externalResponse = httpClient.sendRequest(
                "https://fmohsen22-chatbot-cosmeticshop.hf.space/chat",
                HttpMethod.POST,
                headers,
                "{\n" +
                        "  \"user_input\": \"" + userText + "\"\n" +
                        "}"
        );

        // Extract the response text
        String responseText = externalResponse.getBody();

        // Create a map to hold the response JSON structure
        Map<String, String> response = new HashMap<>();
        response.put("reply", responseText);

        // Return the map directly as JSON
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

// Model class to represent the chat message
class ChatMessage {
    private String user_input;

    public String getUser_input() {
        if (user_input == null || user_input.trim().isEmpty()) {
            user_input = "Please write something.";
        }
        return user_input;
    }

    public void setUser_input(String user_input) {
        this.user_input = user_input;
    }


}

