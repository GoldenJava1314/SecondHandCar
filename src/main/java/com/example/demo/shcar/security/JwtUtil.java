package com.example.demo.shcar.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // 取得加密用的 Key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 產生 Token
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username) // 存帳號
                .setIssuedAt(new Date()) // 發行時間
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 過期時間
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token 取出 username
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 驗證 Token 是否有效
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username);
    }

    // 解析 Claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
