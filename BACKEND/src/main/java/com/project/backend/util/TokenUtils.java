package com.project.backend.util;

import com.project.backend.models.AppUser;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenUtils {

    @Value("${spring.application.name}")
    private String APP_NAME ;
    @Value("${jwt.secret}")
    private String SECRET ;
    @Value("${jwt.expires-in}")
    private int EXPIRES_IN ;
    @Value("${jwt.auth.header}")
    private String AUTH_HEADER ;
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    @PostConstruct
    public void checkJwtKey() {
        System.out.println("JWT key length (chars): " + SECRET.length());
        System.out.println("JWT key bytes: " +
                SECRET.getBytes(StandardCharsets.UTF_8).length);
    }
        public String generateToken(AppUser user) {
            return Jwts.builder().setIssuer(APP_NAME).setSubject(user.getEmail())
                    .setAudience(generateAudience()).setIssuedAt(new Date())
                    .claim("role",  "ROLE_" + user.getClass().getSimpleName().toUpperCase()).setExpiration(generateExpirationDate())
                    .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SIGNATURE_ALGORITHM).compact();
        }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + EXPIRES_IN);
    }

    private String generateAudience() {
        return "web";
    }
    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        AppUser user = (AppUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        return (username != null
                && username.equals(userDetails.getUsername()) );
    }


    public int getExpiredIn() {
        return EXPIRES_IN;
    }
}

