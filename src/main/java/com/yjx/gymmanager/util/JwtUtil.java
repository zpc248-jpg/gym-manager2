package com.yjx.gymmanager.util;

import com.yjx.gymmanager.common.CurrentUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long expireMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expire-hours}") long expireHours) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = Duration.ofHours(expireHours).toMillis();
    }

    public String createToken(CurrentUser user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("relatedId", user.getRelatedId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMillis))
                .signWith(secretKey)
                .compact();
    }

    public CurrentUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new CurrentUser(
                claims.get("userId", Number.class).longValue(),
                claims.get("username", String.class),
                claims.get("role", String.class),
                claims.get("relatedId", Number.class) == null ? null : claims.get("relatedId", Number.class).longValue()
        );
    }
}
