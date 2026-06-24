package org.example.ndemy_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.ndemy_backend.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class jwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @jakarta.annotation.PostConstruct
    void validateSecret() {
        if (secretKey == null || secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(User user) {
        return buildToken(new HashMap<>(), user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return buildToken(claims, user, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)                          // 0.11.x usa setClaims()
                .setSubject(user.getEmail())                     // setSubject()
                .claim("role", user.getRole().name())
                .claim("userId", user.getId().toString())
                .setIssuedAt(new Date())                         // setIssuedAt()
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // requiere algoritmo explícito
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = extractClaims(token);
        String type = claims.get("type", String.class);
        return "refresh".equals(type);                           // evita la advertencia de inversión
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()                              // 0.11.x usa parserBuilder()
                .setSigningKey(getSigningKey())                  // setSigningKey()
                .build()
                .parseClaimsJws(token)                          // parseClaimsJws() en lugar de parseSignedClaims()
                .getBody();                                      // getBody() en lugar de getPayload()
    }
}