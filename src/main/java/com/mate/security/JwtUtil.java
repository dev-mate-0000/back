package com.mate.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Getter
    private final Long accessTokenExpiredMs = 24 * 60 * 60L;

    @Getter
    private final Long refreshTokenExpiredMs = 30 * 24 * 60 * 60L;

    @Getter
    private final String accessTokenName = "Authorization";

    @Getter
    private final String refreshTokenName = "Refresh";

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public String createJwt(Long id, String name, Long expiredMs) {
        return Jwts.builder()
                .claim("id", id)
                .claim("name", name)

                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Cookie createCookie(String key, String value, Long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiredMs.intValue());
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
