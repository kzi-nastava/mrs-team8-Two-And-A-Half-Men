package com.project.backend.listeners;

import com.project.backend.models.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        Principal user = event.getUser();

        if (user instanceof AppUser appUser) {
            System.out.println("User connected: "+ appUser.getUsername() + " (ID: " +appUser.getId() + "), Session: "+ sessionId);

            // Your connection logic here
            // For example:
            // - Update user status to online
            // - Add to active users list
            // - Send welcome message
        }
        else {
            System.out.println("Anonymous session connected: " + sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        Principal user = event.getUser();

        if (user instanceof AppUser appUser) {
            System.out.println("User disconnected: "+ appUser.getUsername() + " (ID: " +appUser.getId() + "), Session: "+ sessionId);


            // Your cleanup logic here
            // For example:
            // - Update user status to offline
            // - Clean up any session-specific data
            // - Notify other users
            // - Remove from active users list

        } else {
            System.out.println("Anonymous session disconnected: " + sessionId);
        }
    }
}
