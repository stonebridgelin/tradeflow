package com.stonebridge.tradeflow.security.filter;

import com.mysql.cj.util.StringUtils;
import com.stonebridge.tradeflow.security.utils.JwtUtil;
import com.stonebridge.tradeflow.security.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.Collection;
import java.util.List;

/**
 * 自定义 JWT 认证过滤器，继承 OncePerRequestFilter 确保每次请求只执行一次
 * 用于拦截每个 HTTP 请求，验证请求头中的 JWT token，从 Redis 获取用户权限，
 * 并将认证信息存储到 Spring Security 上下文中，以便后续权限校验
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数，初始化过滤器所需的依赖
     *
     * @param jwtUtil            JWT 工具类
     * @param redisTemplate      Redis 模板
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 过滤器核心逻辑，验证 JWT token，获取用户权限并设置到 Spring Security 上下文中
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 如果过滤器链处理失败
     * @throws IOException      如果请求或响应处理失败
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 检查请求路径，跳过 /auth/login 的 token 验证
        String requestURI = request.getRequestURI();
        if ("/auth/login".equals(requestURI)) {
            logger.debug("跳过 /auth/login 的 token 验证，请求: {} {}", request.getMethod(), requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 从 Authorization 头提取 token
        String token = SecurityUtil.getToken(request);

        // 检查 token 是否有效
        if (!StringUtils.isNullOrEmpty(token)) {
            // 验证 token
            if (!jwtUtil.validateToken(token)) {
                logger.warn("无效的 token，请求: {} {}", request.getMethod(), requestURI);
                filterChain.doFilter(request, response); // 触发未认证处理
                return;
            }

            // 获取用户名
            String username = jwtUtil.getUsername(token);

            // 从 Redis 获取权限
            List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(username);

            // 构建权限集合
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if (!CollectionUtils.isEmpty(permissionValueList)) {
                for (String perms : permissionValueList) {
                    authorities.add(new SimpleGrantedAuthority(perms));
                }
            }

            // 创建认证对象
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token, authorities);

            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.debug("为用户 {} 设置认证，请求: {} {}", username, request.getMethod(), requestURI);
            filterChain.doFilter(request, response); // 触发未认证处理
        } else {
            logger.debug("请求未携带 token: {} {}", request.getMethod(), requestURI);
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}