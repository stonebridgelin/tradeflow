package com.stonebridge.tradeflow.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.security.entity.SecurityUser;
import com.stonebridge.tradeflow.security.service.UserDetailsServiceImpl;
import com.stonebridge.tradeflow.security.utils.JwtUtil;
import com.stonebridge.tradeflow.security.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 自定义 JWT 登录过滤器，继承 Spring Security 的 UsernamePasswordAuthenticationFilter
 * 用于处理用户登录请求（/auth/login），验证用户名和密码，生成 JWT token，
 * 并将用户权限信息存储到 Redis，返回 JSON 格式的登录结果
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtLoginFilter.class);


    /**
     * 认证管理器，用于执行用户认证逻辑
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JWT 工具类，用于生成和解析 JWT token
     */
    private final JwtUtil jwtUtil;

    /**
     * Redis 模板，用于存储用户权限信息
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数，初始化过滤器所需的依赖并设置登录接口路径
     *
     * @param authenticationManager 认证管理器
     * @param jwtUtil               JWT 工具类
     * @param redisTemplate         Redis 模板
     */
    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        // 设置登录接口路径为 /auth/login，覆盖默认的 /login
        setFilterProcessesUrl("/auth/login");
    }

    /**
     * 尝试执行用户认证，从请求的 JSON 体中解析用户名和密码
     *
     * @param request  HTTP 请求对象，包含登录请求的 JSON 数据
     * @param response HTTP 响应对象，用于后续响应
     * @return Authentication 认证结果，包含用户信息和权限
     * @throws AuthenticationException 如果认证失败（如用户名密码为空或解析错误）
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 检查请求方法，仅允许 POST
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            // 非 POST 请求，返回 405 错误
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("message", ResultCodeEnum.METHOD_NOT_ALLOWED.getMessage());
            errorData.put("code", ResultCodeEnum.METHOD_NOT_ALLOWED.getCode());

            // 使用 SecurityUtil 的 out 方法将 Result 对象序列化为 JSON 并写入响应
            SecurityUtil.out(response, Result.ok(errorData));
            return null; // 终止认证流程
        }

        try {
            // 使用 ObjectMapper 从请求的 JSON 体中解析登录数据
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> loginData = mapper.readValue(request.getInputStream(), Map.class);
            String username = loginData.get("username");
            String password = loginData.get("password");
            // 存入 request attribute，供 unsuccessfulAuthentication 使用
            request.setAttribute("attemptedUsername", username);
            // 检查用户名和密码是否为空
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                throw new UserDetailsServiceImpl.AuthenticationFailureException(
                        ResultCodeEnum.ARGUMENT_VALID_ERROR.getCode(),
                        ResultCodeEnum.ARGUMENT_VALID_ERROR.getMessage()
                );
            }

            // 创建认证对象，包含用户名和密码
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

            // 调用认证管理器执行认证，验证用户名和密码
            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            // 如果解析 JSON 数据失败，抛出认证异常
            throw new AuthenticationException("解析登录数据失败", e) {
            };
        }
    }

    /**
     * 认证成功后的处理逻辑，生成 JWT token，存储权限到 Redis，并返回 JSON 响应
     *
     * @param request    HTTP 请求对象
     * @param response   HTTP 响应对象，用于返回 token
     * @param chain      过滤器链
     * @param authResult 认证结果，包含用户信息
     * @throws IOException      如果写入响应失败
     * @throws ServletException 如果过滤器链处理失败
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        // 从认证结果中获取用户信息
        SecurityUser user = (SecurityUser) authResult.getPrincipal();
        String username = user.getUsername();

        // 使用 JwtUtil 生成 JWT token
        String token = jwtUtil.generateToken(username);

        // 将用户权限列表存储到 Redis，键为用户名，值为权限列表，设置 12 小时过期时间
        redisTemplate.opsForValue().set(username, user.getPermissionValueList(), 12, TimeUnit.HOURS);

        // 构建成功响应，包含生成的 token
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("code", "200");
        // 使用 SecurityUtil 的 out 方法将 Result 对象序列化为 JSON 并写入响应
        SecurityUtil.out(response, Result.ok(data));
    }

    /**
     * 认证失败后的处理逻辑，返回 JSON 格式的错误响应
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象，用于返回错误信息
     * @param failed   认证异常，包含失败原因
     * @throws IOException      如果写入响应失败
     * @throws ServletException 如果过滤器链处理失败
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String username = (String) request.getAttribute("attemptedUsername");
        logger.warn("登录失败，用户: {}，原因: {}，请求: {} {}，IP: {}", username, failed.getMessage(), request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), failed);
        // 构建失败响应，包含错误消息
        Map<String, Object> errorData = new HashMap<>();
        Integer code;
        String message;

        if (failed instanceof UserDetailsServiceImpl.AuthenticationFailureException) {
            UserDetailsServiceImpl.AuthenticationFailureException ex = (UserDetailsServiceImpl.AuthenticationFailureException) failed;
            code = ex.getCode();
            message = ex.getMessage();
        } else if (failed instanceof BadCredentialsException) {
            code = ResultCodeEnum.PASSWORD_ERROR.getCode();
            message = ResultCodeEnum.PASSWORD_ERROR.getMessage();
        } else {
            code = ResultCodeEnum.LOGIN_AUTH.getCode();
            message = ResultCodeEnum.LOGIN_AUTH.getMessage();
        }

        errorData.put("message", message);
        errorData.put("code", code);
        // 使用 SecurityUtil 的 out 方法返回错误响应
        // ResultCodeEnum.LOGIN_AUTH 提供认证失败的错误码和消息（如 401 和“登录失败”）
        SecurityUtil.out(response, Result.ok(errorData));
    }
}