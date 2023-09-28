package com.saadahmedev.authenticationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    private String generateToken(Map<String, Object> claims, String username) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder().setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime * (30L * 24 * 60 * 60 * 1000)))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
