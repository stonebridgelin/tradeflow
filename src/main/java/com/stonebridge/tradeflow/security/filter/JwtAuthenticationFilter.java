package com.stonebridge.tradeflow.security.filter;

import com.mysql.cj.util.StringUtils;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.security.utils.JwtUtil;
import com.stonebridge.tradeflow.security.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 JWT 认证过滤器，继承 OncePerRequestFilter 确保每次请求只执行一次
 * 用于拦截每个 HTTP 请求，验证请求头中的 JWT token，从 Redis 获取用户权限，
 * 并将认证信息存储到 Spring Security 上下文中，以便后续权限校验
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> SKIP_AUTH_PATHS = Arrays.asList("/auth/login", "/auth/refresh", "/auth/register");

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (SKIP_AUTH_PATHS.contains(requestURI)) {
            logger.debug("跳过 {} 的 token 验证，请求: {} {}，IP: {}", requestURI, request.getMethod(), requestURI, request.getRemoteAddr());
            filterChain.doFilter(request, response);
            return;
        }

        String token = SecurityUtil.getToken(request);
        if (!StringUtils.isNullOrEmpty(token)) {
            try {
                if (redisTemplate.hasKey("blacklist:" + token)) {
                    logger.warn("使用黑名单中的 token: {}，请求: {} {}，IP: {}", token, request.getMethod(), requestURI, request.getRemoteAddr());
                    SecurityContextHolder.clearContext();
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), ResultCodeEnum.LOGIN_AUTH, "Token 已失效");
                    return;
                }

                if (!jwtUtil.validateToken(token)) {
                    logger.warn("无效的 token，请求: {} {}，IP: {}", request.getMethod(), requestURI, request.getRemoteAddr());
                    SecurityContextHolder.clearContext();
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), ResultCodeEnum.LOGIN_AUTH, ResultCodeEnum.LOGIN_AUTH.getMessage());
                    return;
                }

                String username = jwtUtil.getUsername(token);

                // 直接从 Redis 获取权限，移除 Caffeine 缓存
                List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(username);

                Collection<GrantedAuthority> authorities = new ArrayList<>();
                if (!CollectionUtils.isEmpty(permissionValueList)) {
                    for (String perms : permissionValueList) {
                        authorities.add(new SimpleGrantedAuthority(perms));
                    }
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("为用户 {} 设置认证，请求: {} {}，IP: {}", username, request.getMethod(), requestURI, request.getRemoteAddr());

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                logger.warn("JWT 验证失败: {}，请求: {} {}，IP: {}", e.getMessage(), request.getMethod(), requestURI, request.getRemoteAddr());
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), ResultCodeEnum.LOGIN_AUTH, e.getMessage());
                return;
            }
        } else {
            logger.debug("请求未携带 token: {} {}，IP: {}", request.getMethod(), requestURI, request.getRemoteAddr());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), ResultCodeEnum.LOGIN_AUTH, ResultCodeEnum.LOGIN_AUTH.getMessage());
            return;
        }
    }

    /**
     * 发送错误响应并终止过滤器链
     */
    private void sendErrorResponse(HttpServletResponse response, int status, ResultCodeEnum errorCode, String message) throws IOException {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("code", errorCode.getCode());
        errorData.put("message", message);
        Result result = Result.ok(errorData);
        SecurityUtil.out(response, result);
    }
}