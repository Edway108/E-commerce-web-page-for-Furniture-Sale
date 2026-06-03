package com.furnituree.furnituree.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000;
    private static final String DEFAULT_DEV_SECRET = "change-this-secret-to-a-long-random-value-32chars";
    private static final Key key = buildSigningKey();

    private static Key buildSigningKey() {
        String secret = System.getenv().getOrDefault("JWT_SECRET", DEFAULT_DEV_SECRET);

        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters for HS256");
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public static boolean isTokenValid(String token) {
        return !getClaims(token).getExpiration().before(new Date());
    }
}
