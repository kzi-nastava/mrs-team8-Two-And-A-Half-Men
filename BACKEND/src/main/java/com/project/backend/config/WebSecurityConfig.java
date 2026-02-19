package com.project.backend.config;

import com.project.backend.security.auth.JWTAuthetntificationFilter;
import com.project.backend.security.auth.RestAuthenticationEntryPoint;
import com.project.backend.service.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

//    @Autowired
//    private TokenUtils tokenUtils;
    @Autowired
    private JWTAuthetntificationFilter jwtAuthenticationFilter;
    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
    //Enkripcija sifra ako zelimo da cuvamo sifre u bazi podataka enkriptoivane (glasanje)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    // Definisemo prava pristupa za zahteve ka odredjenim URL-ovima/rutama
    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint));
        http.authorizeHttpRequests(request -> {
            request.requestMatchers("/files/**").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers( "/api/v1/login").permitAll()
                    .requestMatchers("/api/v1/users/register").permitAll()
                    .requestMatchers("/api/v1/reset-password").permitAll()
                    .requestMatchers("/api/v1/rides/estimates").permitAll()
                    .requestMatchers("/api/v1/activate").permitAll()
                    .requestMatchers("/api/v1/activate/drivers").permitAll()
                    .requestMatchers("/api/v1/forgot-password/**").permitAll()
                    .requestMatchers("/api/v1/drivers/**").permitAll()
                    .requestMatchers("/api/v1/rides/estimates").permitAll()
                    .requestMatchers("/api/v1/rides/{id}").permitAll()
                    .requestMatchers("/api/v1/rides/*/rating").permitAll()
                    .requestMatchers("/api/v1/rides/*/notes").permitAll()
                    .requestMatchers("/socket/**").permitAll()
                    // 🔒 ALL OTHER API endpoints require auth
                    .requestMatchers("/api/**").authenticated()

                    // 🌍 EVERYTHING ELSE (Angular frontend)
                    .anyRequest().permitAll();
        });
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()//.requestMatchers(HttpMethod.POST, "/auth/login")
                .requestMatchers(HttpMethod.GET, "/", "/webjars/*", "/*.html", "/favicon.ico",
                        "/*/*.html", "/*/*.css", "/*/*.js");

    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "OPTIONS", "DELETE", "PATCH")); // or simply "*"
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
