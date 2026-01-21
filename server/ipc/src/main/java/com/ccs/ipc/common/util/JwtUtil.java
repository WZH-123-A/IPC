package com.ccs.ipc.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥（应该配置在配置文件中，这里使用默认值）
     */
    @Value("${jwt.secret:ipc-secret-key-for-jwt-token-generation-please-change-in-production}")
    private String secret;

    /**
     * JWT过期时间（毫秒），默认24小时
     */
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return generateToken(claims);
    }

    /**
     * 生成JWT Token
     *
     * @param claims 自定义声明
     * @return JWT Token
     */
    private String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("username") : null;
    }

    /**
     * 从Token中获取Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析JWT Token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            log.error("验证JWT Token失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断Token是否过期
     *
     * @param claims Claims
     * @return 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 获取Token的过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }
}

