package com.project.backend.security.websocket;

import com.project.backend.models.AppUser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChanelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // Get user from handshake attributes
        AppUser user = null;
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            user = (AppUser) sessionAttributes.get("user");
        }
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = handleNotificationsSubscribe(accessor, user);

            System.out.println("✓ Subscription authorized: " + destination);
        }

        return message;
    }

    private static @Nullable String handleNotificationsSubscribe(StompHeaderAccessor accessor, AppUser user) {
        String destination = accessor.getDestination();

        // Validate subscription authorization
        if (destination != null && destination.startsWith("/topic/notifications/")) {
            if (user == null) {
                throw new AccessDeniedException("Unauthorized: No user information found in session");
            }
            String requestedUserId = destination.replace("/topic/notifications/", "");

            if (!requestedUserId.equals(user.getId().toString())) {
                throw new AccessDeniedException("Unauthorized: Cannot subscribe to another user's notifications");
            }
        }
        return destination;
    }
}