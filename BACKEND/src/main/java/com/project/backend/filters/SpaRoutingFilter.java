package com.project.backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SpaRoutingFilter extends OncePerRequestFilter {

    private static final List<String> BACKEND_PREFIXES = List.of("/api", "/socket");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("FILTERING PATH: " + path);
        boolean isBackend    = BACKEND_PREFIXES.stream().anyMatch(path::startsWith);
        boolean isStaticFile = path.contains(".");

        if (!isBackend && !isStaticFile) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}