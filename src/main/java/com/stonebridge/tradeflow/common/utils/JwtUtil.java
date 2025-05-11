package com.stonebridge.tradeflow.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 推荐使用 256-bit 的 Base64 编码密钥（可放配置文件中）
    private static String SECRET_KEY = ""; // 示例用 key，请替换
    private static long EXPIRATION_TIME = 0L; // 1 天

    @Autowired
    public JwtUtil(JwtProperties jwtProperties) {
        SECRET_KEY = jwtProperties.getSecretKey();
        EXPIRATION_TIME = jwtProperties.getExpirationTime();
    }

    // 获取签名密钥
    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * ✅ 生成 JWT Token
     */
    public static String generateToken(String username, String userId) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    /**
     * ✅ 解析并返回 Claims，含自动过期校验和异常处理
     */
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

    /**
     * ✅ 获取用户名（subject）
     */
    public static String getUsername(String token) {
        Object val = getClaims(token).get("username");
        return val != null ? val.toString() : null;
    }

    /**
     * ✅ 获取用户 ID（userId 字段）
     */
    public static String getUserId(String token) {
        Object val = getClaims(token).get("userId");
        return val != null ? val.toString() : null;
    }

    /**
     * ✅ 判断 token 是否过期（可选扩展）
     */
    public static boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}