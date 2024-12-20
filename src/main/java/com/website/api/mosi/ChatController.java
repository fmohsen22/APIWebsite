package com.website.api.mosi;

import com.website.api.mosi.api.rest.GenericHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private GenericHttpClient httpClient;

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> receiveMessage(@RequestBody ChatMessage message) {
        // Get the message text sent by the user
        String userText = message.getText();

        // Prepare a response based on the user's message
        String responseText = null;
//        if (userText.toLowerCase().contains("hello")) {
//            responseText = "Hello! How can I assist you today? 😊";
//        } else if (userText.toLowerCase().contains("price")) {
//            responseText = "Our service prices vary. Could you specify the service you're interested in?";
//        } else if (userText.toLowerCase().contains("appointment")) {
//            responseText = "You can book an appointment through our website's 'Time Booking' section!";
//        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        responseText = String.valueOf(httpClient.sendRequest("https://fmohsen22-chatbot-cosmeticshop.hf.space/chat",
                HttpMethod.POST, headers, "{\n" +
                        "  \"user_input\": \""+userText+"\"\n" +
                        "}"));


        // Create a map to hold the response JSON structure
        Map<String, String> response = new HashMap<>();
        response.put("reply", responseText);

        // Return the map directly as JSON
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}

// Model class to represent the chat message
class ChatMessage {
    private String text;

    public String getText() {
        if (text==null){
            text="Please write something.";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

