    package com.project.backend.config;

    import com.project.backend.security.websocket.ChanelInterceptor;
    import com.project.backend.security.websocket.CustomStompErrorHandler;
    import com.project.backend.security.websocket.HandshakeInterceptor;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    @Configuration
    @EnableWebSocketMessageBroker
    @RequiredArgsConstructor
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

        private final ChanelInterceptor chanelInterceptor;
        private final HandshakeInterceptor handshakeInterceptor;
        private final CustomStompErrorHandler customStompErrorHandler;

//        @Override
//        public void configureClientInboundChannel(ChannelRegistration registration) {
//            registration.interceptors(chanelInterceptor);
//        }


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
                    .setAllowedOriginPatterns("*")
                    .withSockJS()
                    .setInterceptors(handshakeInterceptor);
            registry.setErrorHandler(customStompErrorHandler);
        }
    }
