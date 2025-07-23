package com.structurax.root.structurax.root.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "secretsecretsecretsecretsecretsecretsecret123";
    private final long expirationMs = 86400000; // 1 day

    public String generateToken(String email, String role,String employeeId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("employeeId", employeeId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public String generateTokenForClient(String email,String role,String clientId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("clientId", clientId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
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
                .parseClaimsJws(token).getBody().get("role", String.class);
    }
}
