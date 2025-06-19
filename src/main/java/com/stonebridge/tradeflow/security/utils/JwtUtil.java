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
            // 修改：使用 parserBuilder() 替代已弃用的 parser()
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token 已过期", e);
        } catch (UnsupportedJwtException e) {
            logger.warn("不支持的JWT: {}", e.getMessage());
            throw new RuntimeException("不支持的 JWT", e);
        } catch (MalformedJwtException e) {
            logger.warn("无效的JWT: {}", e.getMessage());
            throw new RuntimeException("无效的 JWT", e);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // 修改：使用正确的SignatureException包路径
            logger.warn("JWT签名验证失败: {}", e.getMessage());
            throw new RuntimeException("JWT 签名验证失败", e);
        } catch (IllegalArgumentException e) {
            logger.warn("Token参数为空: {}", e.getMessage());
            throw new RuntimeException("Token 参数为空", e);
        }
    }

    public static String getUsername(String token) {
        try {
            Object val = getClaims(token).get("username");
            return val != null ? val.toString() : null;
        } catch (RuntimeException e) {
            logger.warn("获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (RuntimeException e) {
            logger.warn("检查token过期状态失败: {}", e.getMessage());
            return true; // 如果无法解析，认为已过期
        }
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

    /**
     * 从token中获取过期时间
     *
     * @param token JWT token
     * @return 过期时间，如果token无效则返回null
     */
    public static Date getExpirationDate(String token) {
        try {
            return getClaims(token).getExpiration();
        } catch (RuntimeException e) {
            logger.warn("获取token过期时间失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取签发时间
     *
     * @param token JWT token
     * @return 签发时间，如果token无效则返回null
     */
    public static Date getIssuedAtDate(String token) {
        try {
            return getClaims(token).getIssuedAt();
        } catch (RuntimeException e) {
            logger.warn("获取token签发时间失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查token是否可以被刷新（距离过期还有一定时间）
     *
     * @param token JWT token
     * @param refreshWindow 刷新窗口时间（毫秒），在过期前多长时间允许刷新
     * @return true如果可以刷新，否则false
     */
    public static boolean canTokenBeRefreshed(String token, long refreshWindow) {
        try {
            Date expiration = getClaims(token).getExpiration();
            Date now = new Date();
            Date refreshTime = new Date(expiration.getTime() - refreshWindow);
            return now.after(refreshTime) && now.before(expiration);
        } catch (RuntimeException e) {
            logger.warn("检查token刷新状态失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 刷新token（重新生成）
     *
     * @param token 原始token
     * @return 新的token，如果原token无效则返回null
     */
    public static String refreshToken(String token) {
        try {
            String username = getUsername(token);
            if (username != null) {
                return generateToken(username);
            }
            return null;
        } catch (Exception e) {
            logger.warn("刷新token失败: {}", e.getMessage());
            return null;
        }
    }
}