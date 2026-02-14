package com.project.backend.security.auth;

import com.project.backend.service.security.CustomUserDetailsService;
import com.project.backend.util.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthetntificationFilter extends OncePerRequestFilter {
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username;
        String authToken = tokenUtils.getToken(request);

        try {
            System.out.println(authToken);
            if(authToken != null && !authToken.equals("")) {
                username = tokenUtils.getUsernameFromToken(authToken);
                System.out.println(username);
                if(username != null) {
                    var userDetails = userDetailsService.loadUserByUsername(username);
                    if(tokenUtils.validateToken(authToken, userDetails)) {
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("GRESKA PRILIKOM PROVERE TOKENA: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
