package com.website.api.mosi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @PostMapping("/message")
    public ResponseEntity<String> receiveMessage(@RequestBody ChatMessage message) {
        // For now, echo the received message back

        String responseText =  message.getText();

        // Create a map to hold the response JSON structure
        Map<String, String> response = new HashMap<>();
        response.put("reply", responseText);

        return ResponseEntity.ok(String.valueOf(response));
    }
}

// Model class to represent the chat message
class ChatMessage {
    private String text;

    public String getText() {
        if (text==null){
            text="Mosi is working on it 😁😋.";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

