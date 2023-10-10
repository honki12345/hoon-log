package me.honki12345.hoonlog.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenizer {

    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    // TODO application.yaml 로 빼기
    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30 minutes
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret,
        @Value("${jwt.refreshKey}") String refreshSecret) {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    public String createAccessToken(Long id, String username, List<String> roles) {
        return createToken(id, username, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }

    public String createNewAccessToken(String refreshToken) {
        Claims claims = parseRefreshToken(refreshToken);
        Long userId = Long.valueOf((Integer) claims.get("id"));
        String username = (String) claims.get("name");
        List<String> roles = (List<String>) claims.get("roles");
        return createAccessToken(userId, username, roles);
    }

    public String createRefreshToken(Long id, String username, List<String> roles) {
        return createToken(id, username, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    private String createToken(Long id, String username, List<String> roles,
        Long expire, byte[] secretKey) {
        Claims claims = Jwts.claims();
        claims.put("roles", roles);
        claims.put("id", id);
        claims.put("name", username);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + expire))
            .signWith(getSigningKey(secretKey))
            .compact();
    }

    public Long getUserIdFromAccessToken(String token) {
        Claims claims = parseAccessToken(token);
        return Long.valueOf((Integer) claims.get("id"));
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = parseRefreshToken(token);
        return Long.valueOf((Integer) claims.get("id"));
    }

    public List<String> getRolesFromAccessToken(String token) {
        Claims claims = parseAccessToken(token);
        return (List<String>) claims.get("roles");
    }


    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey(secretKey))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }
}
