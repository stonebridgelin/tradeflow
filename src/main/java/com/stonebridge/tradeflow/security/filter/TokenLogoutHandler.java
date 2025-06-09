package com.stonebridge.tradeflow.security.filter;

import com.mysql.cj.util.StringUtils;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.security.utils.JwtUtil;
import com.stonebridge.tradeflow.security.utils.SecurityUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义注销处理器，用于处理 Spring Security 的用户注销逻辑
 * 当用户发起注销请求（如访问 /auth/logout）时，此处理器会验证 JWT token，
 * 删除 Redis 中存储的用户数据，并返回 JSON 格式的注销成功响应
 */
public class TokenLogoutHandler implements LogoutHandler {

    /**
     * JWT 工具类，用于解析和验证 JWT token
     */
    private JwtUtil jwtUtil;

    /**
     * Redis 模板，用于操作 Redis 数据库，删除用户相关数据
     */
    private RedisTemplate redisTemplate;

    /**
     * 构造函数，通过依赖注入初始化 JwtUtil 和 RedisTemplate
     * @param jwtUtil JWT 工具类
     * @param redisTemplate Redis 模板
     */
    public TokenLogoutHandler(JwtUtil jwtUtil, RedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 处理用户注销请求，清理 Redis 数据并返回注销成功的 JSON 响应
     * @param request HTTP 请求对象，包含客户端请求信息（如 Authorization 头）
     * @param response HTTP 响应对象，用于设置响应内容
     * @param authentication 当前用户的认证信息（可能为 null，取决于注销上下文）
     */
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 从请求的 Authorization 头中提取 JWT token
        String token = SecurityUtil.getToken(request);

        // 检查 token 是否存在且不为空
        if (!StringUtils.isNullOrEmpty(token)) {
            // 解析 token，获取用户名
            String username = jwtUtil.getUsername(token);

            // 删除 Redis 中与该用户关联的权限数据
            redisTemplate.delete(username);
            // 删除 Redis 中与该用户的token
            redisTemplate.delete("token:" + username);
        }
    }
}