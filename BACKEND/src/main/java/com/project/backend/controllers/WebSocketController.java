package com.project.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    // REST endpoint
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/sendMessageRest")
    public ResponseEntity<?> sendMessage(
            @RequestBody Map<String, String> message
    ) {
        if (message.containsKey("message")) {
            convertAndSend(message);
            return new ResponseEntity<>(message, new HttpHeaders(), HttpStatus.OK);
        }

        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // WebSockets endpoint
    @MessageMapping("/send/message")
    public Map<String, String> broadcastNotification(String message) {
        Map<String, String> messageConverted = parseMessage(message);

        if (!messageConverted.isEmpty()) {
            convertAndSend(messageConverted);
        }

        return messageConverted;
    }

    private void convertAndSend(Map<String, String> messageConverted) {
        if (messageConverted.containsKey("toId") && messageConverted.get("toId") != null
                && !messageConverted.get("toId").isEmpty()) {
            this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + messageConverted.get("toId"),
                    messageConverted);
            this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + messageConverted.get("fromId"),
                    messageConverted);
        } else {
            this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageConverted);
        }
    }

    // object mapper is thread safe, but expensive
    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<String, String> parseMessage(String message) {
        try {
            return mapper.readValue(message,
                    new TypeReference<>() {});
        } catch (JacksonException e) {
            return Collections.emptyMap();
        }
    }
}
