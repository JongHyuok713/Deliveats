package com.example.deliveats.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String base64Secret;
    @Value("${jwt.access-token-validity-seconds}")
    private long accessTokenValiditySeconds;
    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    private final CustomUserDetailsService userDetailsService;

    private Key getKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            throw new IllegalStateException("""
                    [ERROR] JWT secret key is too weak!
                    Base64 문자열을 최소 32바이트(256비트) 이상으로 생성해야 합니다.
                    (Linux): openssl rand -base64 32
                    (windows): [Convert]::ToBase64String((1..32 | ForEach-Object {Get-Random -Maximum 256}))
                    """);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("jwt.secret must be a valid Base64 string.", e);
        }
    }

    // 액세스 토큰 생성
    public String createAccessToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValiditySeconds * 1000);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("username", username, "role", role))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValiditySeconds * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //인증 객체 반환
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 사용자 정보 추출
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
