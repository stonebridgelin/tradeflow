package com.stonebridge.tradeflow.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static String SECRET_KEY = "";
    private static long EXPIRATION_TIME = 0L;

    @Autowired
    public JwtUtil(JwtProperties jwtProperties) {
        SECRET_KEY = jwtProperties.getSecretKey();
        EXPIRATION_TIME = jwtProperties.getExpirationTime();
    }

    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("username", username)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    private static Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已过期", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("不支持的 JWT", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("无效的 JWT", e);
        } catch (SignatureException e) {
            throw new RuntimeException("JWT 签名验证失败", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token 参数为空", e);
        }
    }

    public static String getUsername(String token) {
        Object val = getClaims(token).get("username");
        return val != null ? val.toString() : null;
    }

    public static boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 验证 JWT token 的有效性
     *
     * @param token 要验证的 token
     * @return true 如果 token 有效（未过期、签名正确、格式合法），否则 false
     */
    public static boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Token is null or empty");
            return false;
        }
        try {
            Claims claims = getClaims(token);
            // 额外检查过期时间（虽然 getClaims 已包含）
            if (claims.getExpiration().before(new Date())) {
                logger.warn("Token is expired for subject: {}", claims.getSubject());
                return false;
            }
            logger.debug("Token validated successfully for subject: {}", claims.getSubject());
            return true;
        } catch (RuntimeException e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}