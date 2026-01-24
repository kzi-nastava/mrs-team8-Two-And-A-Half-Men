    package com.project.backend.config;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.server.ServerHttpRequest;
    import org.springframework.http.server.ServerHttpResponse;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.web.socket.WebSocketHandler;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
    import org.springframework.web.socket.server.HandshakeInterceptor;

    import java.util.Map;

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("/topic", "/queue"); //from server to client

            config.setApplicationDestinationPrefixes("/app"); //from client to server

            config.setUserDestinationPrefix("/user"); // user specific messages
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            System.out.println("=================================================");
            System.out.println("REGISTERING WEBSOCKET ENDPOINTS");
            System.out.println("=================================================");
            // endpoint for connection
            registry.addEndpoint("/socket")
                    .setAllowedOrigins("http://localhost:4200")
                    .withSockJS()
                    .setInterceptors(new HandshakeInterceptor() {
                        @Override
                        public boolean beforeHandshake(ServerHttpRequest request,
                                                       ServerHttpResponse response,
                                                       WebSocketHandler wsHandler,
                                                       Map<String, Object> attributes) throws Exception {
                            System.out.println("=== WEBSOCKET HANDSHAKE STARTING ===");
                            System.out.println("Request URI: " + request.getURI());
                            System.out.println("Headers: " + request.getHeaders());
                            return true;
                        }

                        @Override
                        public void afterHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Exception exception) {
                            if (exception != null) {
                                System.out.println("=== HANDSHAKE FAILED ===");
                                System.out.println("Error: " + exception.getMessage());
                                exception.printStackTrace();
                            } else {
                                System.out.println("=== HANDSHAKE SUCCESSFUL ===");
                            }
                        }
                    });
        }

    }
