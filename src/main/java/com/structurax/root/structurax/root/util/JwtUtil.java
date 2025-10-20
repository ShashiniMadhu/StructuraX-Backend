package com.structurax.root.structurax.root.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.Pattern;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "secretsecretsecretsecretsecretsecretsecret123";
    private final long expirationMs = 86400000; // 1 day

    public String generateToken(String email, String type, Integer userId, String employeeId, String clientId, int supplierId, String adminId) {
        // Create the builder first
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .claim("type", type)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()));

        // Add employeeId, clientId, lecturerId if available
        if (employeeId != null) {
            builder.claim("employeeId", employeeId);
        }
        if (clientId != null) {
            builder.claim("clientId", clientId);
        }
        if (supplierId != 0) {
            builder.claim("supplierId", supplierId);
        }
        if (adminId != null) {
            builder.claim("adminId", adminId);
        }

        // Build and return the token
        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build()
                .parseClaimsJws(token).getBody().get("type", String.class);
    }


}
