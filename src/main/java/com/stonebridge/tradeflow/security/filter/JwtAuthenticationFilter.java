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


    /**
     * JWT 工具类，用于解析和验证 JWT token
     */
    private final JwtUtil jwtUtil;

    /**
     * Redis 模板，用于从 Redis 获取用户权限信息
     */
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * 构造函数，通过依赖注入初始化所需组件
     *
     * @param jwtUtil       JWT 工具类
     * @param redisTemplate Redis 模板
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 过滤器核心逻辑，验证 JWT token，获取用户权限并设置到 Spring Security 上下文中
     *
     * @param request     HTTP 请求对象，包含客户端请求信息（如 Authorization 头）
     * @param response    HTTP 响应对象，用于后续响应
     * @param filterChain 过滤器链，用于继续处理请求
     * @throws ServletException 如果过滤器链处理失败
     * @throws IOException      如果请求或响应处理失败
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求的 Authorization 头中提取 JWT token（格式为 "Bearer <token>"）
        String token = SecurityUtil.getToken(request);

        // 检查 token 是否存在且不为空
        if (!StringUtils.isNullOrEmpty(token)) {
            // 解析 token 获取用户名
            String username = jwtUtil.getUsername(token);

            // 从 Redis 获取该用户对应的权限列表（键为用户名，值为权限字符串列表）
            List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(username);

            // 初始化权限集合，用于存储用户的权限信息
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // 如果权限列表不为空，将每个权限转换为 GrantedAuthority 对象
            if (!CollectionUtils.isEmpty(permissionValueList)) {
                for (String perms : permissionValueList) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(perms);
                    authorities.add(authority);
                }
            }

            // 创建认证对象，包含用户名、token 和权限信息
            // 注意：第二个参数（credentials）传递 token，仅用于上下文存储，非密码
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token, authorities);

            // 将认证对象存入 Spring Security 上下文，供后续权限校验使用（如 @PreAuthorize）
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            logger.debug("请求未携带 token: {} {}", request.getMethod(), request.getRequestURI());

        }
        // 继续执行过滤器链，处理后续请求
        filterChain.doFilter(request, response);
    }
}