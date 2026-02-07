package com.project.backend.security.websocket;

import com.project.backend.repositories.AppUserRepository;
import com.project.backend.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {
    private final TokenUtils tokenUtils;
    private final UserDetailsService userDetailsService;
    private final AppUserRepository appUserRepository;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        System.out.println("=== WEBSOCKET HANDSHAKE STARTING ===");
        System.out.println("Request URI: " + request.getURI());
        System.out.println("Headers: " + request.getHeaders());
        if (!request.getHeaders().containsHeader("Authorization")) {
            System.out.println("No Authorization header found");
            return true; // Allow handshake to proceed without authentication
        }
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Invalid Authorization header format");
            return true; // Allow handshake to proceed without authentication
        }
        String token = authHeader.substring(7);
        if (token.isEmpty()) {
            System.out.println("No token found in handshake request");
            return true; // Allow handshake to proceed without authentication
        }
        var username = tokenUtils.getUsernameFromToken(token);
        if (username == null) {
            System.out.println("Invalid token: Unable to extract username");
            return true; // Allow handshake to proceed without authentication
        }
        var userDetails = userDetailsService.loadUserByUsername(username);
        if(!tokenUtils.validateToken(token, userDetails)) {
            return true;
        }
        var appUser = appUserRepository.findByEmail(userDetails.getUsername());
        if(appUser == null) {
            System.out.println("User not found for username: " + userDetails.getUsername());
            return true; // Allow handshake to proceed without authentication
        }
        // Store user details in attributes for later use
        attributes.put("userId", appUser.getId());
        attributes.put("user", appUser);
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            System.out.println("=== HANDSHAKE FAILED ===");
            System.out.println("Error: " + exception.getMessage());
            // exception.printStackTrace();
        } else {
            System.out.println("=== HANDSHAKE SUCCESSFUL ===");
        }
    }
}
