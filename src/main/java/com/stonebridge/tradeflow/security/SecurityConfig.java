package com.stonebridge.tradeflow.security;

import com.stonebridge.tradeflow.security.filter.*;
import com.stonebridge.tradeflow.security.utils.JwtUtil;
import com.stonebridge.tradeflow.security.filter.JwtLoginFilter;
import com.stonebridge.tradeflow.security.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类，负责定义安全策略、过滤器链和异常处理逻辑
 */
@Configuration
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
@EnableMethodSecurity(prePostEnabled = true) // 启用方法级安全，支持 @PreAuthorize 等注解
public class SecurityConfig {

    // 依赖注入的工具类和组件
    private final JwtUtil jwtUtil; // JWT 工具类，用于生成和验证 JWT token
    private final RedisTemplate<String, Object> redisTemplate; // Redis 模板，用于存储 JWT 或会话相关数据
    private final AuthenticationConfiguration authenticationConfiguration; // 认证配置，用于获取 AuthenticationManager
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl; // 自定义无权限处理器
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl; // 自定义未认证处理器

    /**
     * 构造函数，通过 Spring 的依赖注入初始化所需组件
     *
     * @param jwtUtil                      JWT 工具类
     * @param redisTemplate                Redis 模板
     * @param authenticationConfiguration  认证配置
     * @param accessDeniedHandlerImpl      无权限处理器
     * @param authenticationEntryPointImpl 未认证处理器
     */
    @Autowired
    public SecurityConfig(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate, AuthenticationConfiguration authenticationConfiguration,
                          AccessDeniedHandlerImpl accessDeniedHandlerImpl, AuthenticationEntryPointImpl authenticationEntryPointImpl) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.authenticationConfiguration = authenticationConfiguration;
        this.accessDeniedHandlerImpl = accessDeniedHandlerImpl;
        this.authenticationEntryPointImpl = authenticationEntryPointImpl;
    }

    /**
     * 定义密码加密器 Bean，使用 PasswordUtil 提供的加密方式
     *
     * @return PasswordEncoder 密码加密器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordUtils().getPasswordEncoder(); // 返回自定义的密码加密器（如 BCrypt）
    }

    /**
     * 定义认证管理器 Bean，用于处理用户认证逻辑
     *
     * @return AuthenticationManager 认证管理器实例
     * @throws Exception 如果配置失败
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 从认证配置中获取认证管理器
    }

    /**
     * 配置 Spring Security 的安全过滤器链，定义请求拦截、异常处理、会话管理和过滤器等
     *
     * @param http                  HttpSecurity 配置对象
     * @param authenticationManager 认证管理器
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 如果配置失败
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // 1. 配置异常处理
        // 设置未认证和无权限的处理方式
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointImpl) // 未提供有效身份凭据（例如未登录或 token 无效）时试图访问受保护资源的情况，返回 401 响应（如 JSON 格式）
                .accessDeniedHandler(accessDeniedHandlerImpl); // 试访问受保护资源（如通过 @PreAuthorize 限制的接口）但缺乏所需权限时，返回 403 响应（如 JSON 格式）

        // 2. 关闭 CSRF 保护
        // 由于使用 JWT 认证，无需 CSRF 保护，适合 RESTful API
        http.csrf().disable();

        // 3. 设置无状态会话管理
        // 配置为无状态会话，Spring Security 不会创建或使用 HttpSession，适合 JWT 认证
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 4. 配置权限规则
        // 定义哪些请求需要认证，哪些可以匿名访问
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
                // 允许匿名访问的路径（如登录、注册和 Swagger 相关接口）
                .antMatchers("/auth/register", "/druid-system/**", "/druid-business/**",
                        "/swagger-ui.html", "/webjars/**", "/v2/api-docs").permitAll()
                // 其他所有请求都需要认证
                // 显式要求 /user/info 需要认证
                .antMatchers("/user/info").authenticated()
                .anyRequest().authenticated();

        // 5. 配置注销
        // 定义注销接口和处理逻辑，清理 JWT 或 Redis 中的数据
        http.logout()
                .logoutUrl("/auth/logout") // 注销请求的 URL
                .addLogoutHandler(new TokenLogoutHandler(jwtUtil, redisTemplate)); // 自定义注销处理器

        // 6. 添加登录过滤器
        // 替换默认的 UsernamePasswordAuthenticationFilter，处理登录请求并生成 JWT
        JwtLoginFilter loginFilter = new JwtLoginFilter(authenticationManager, jwtUtil, redisTemplate);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // 7. 添加 JWT 认证过滤器
        // 在每次请求前验证 JWT，确保用户已认证
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(jwtUtil, redisTemplate);
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        // 8. 确保 ExceptionTranslationFilter 在过滤器链中
        // ExceptionTranslationFilter 负责将异常转换为 AuthenticationEntryPoint 或 AccessDeniedHandler 的响应
        // 放置在 JwtAuthenticationFilter 之后，确保认证完成后处理异常
        http.addFilterAfter(new ExceptionTranslationFilter(authenticationEntryPointImpl), JwtAuthenticationFilter.class);

        // 返回构建好的安全过滤器链
        return http.build();
    }
}