package com.stonebridge.tradeflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.security.filter.*;
import com.stonebridge.tradeflow.security.filter.JwtLoginFilter;
import com.stonebridge.tradeflow.security.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security 配置类，负责定义安全策略、过滤器链和异常处理逻辑
 */
@Configuration
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
@EnableMethodSecurity(prePostEnabled = true) // 启用方法级安全，支持 @PreAuthorize 等注解
public class SecurityConfig {

    // 依赖注入的工具类和组件
    private final RedisTemplate<String, Object> redisTemplate; // Redis 模板，用于存储 JWT 或会话相关数据
    private final AuthenticationConfiguration authenticationConfiguration; // 认证配置，用于获取 AuthenticationManager
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl; // 自定义无权限处理器
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl; // 自定义未认证处理器

    /**
     * 构造函数，通过 Spring 的依赖注入初始化所需组件
     *
     * @param redisTemplate                Redis 模板
     * @param authenticationConfiguration  认证配置
     * @param accessDeniedHandlerImpl      无权限处理器
     * @param authenticationEntryPointImpl 未认证处理器
     */
    @Autowired
    public SecurityConfig(RedisTemplate<String, Object> redisTemplate, AuthenticationConfiguration authenticationConfiguration,
                          AccessDeniedHandlerImpl accessDeniedHandlerImpl, AuthenticationEntryPointImpl authenticationEntryPointImpl) {
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
     * 配置 DaoAuthenticationProvider，设置不隐藏 UsernameNotFoundException
     * @param userDetailsService 用户详情服务
     * @param passwordEncoder 密码加密器
     * @return 自定义的 DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false); // 不隐藏用户不存在异常
        return provider;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, AuthenticationProvider daoAuthenticationProvider) throws Exception {
        http.cors().and(); // 开启跨域
        http.authenticationProvider(daoAuthenticationProvider);
        // 1. 配置异常处理
        // 设置未认证和无权限的处理方式
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointImpl) // 401
                .accessDeniedHandler(accessDeniedHandlerImpl); // 403
        // 2. 关闭 CSRF 保护
        // 由于使用 JWT 认证，无需 CSRF 保护，适合 RESTful API
        http.csrf().disable();

        // 3. 设置无状态会话管理
        // 配置为无状态会话，Spring Security 不会创建或使用 HttpSession，适合 JWT 认证
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 4. 配置权限规则
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
                // 允许匿名访问的路径（如登录、注册和 Swagger 相关接口）
                .antMatchers(
                        "/auth/register", "/druid-system/**", "/druid-business/**",
                        "/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/v2/api-docs", "/v3/api-docs/**",
                        "/doc.html", "/favicon.ico", "/static/**", "/public/**"
                ).permitAll()
                // 其他所有请求都需要认证
                // 显式要求 /user/info 需要认证
                .antMatchers("/user/info").authenticated()
                .anyRequest().authenticated();

        // 5. 配置注销
        // 定义注销接口和处理逻辑，清理 JWT 或 Redis 中的数据
        http.logout().logoutUrl("/auth/logout").addLogoutHandler(new TokenLogoutHandler(redisTemplate)).logoutSuccessHandler(jsonLogoutSuccessHandler());
        // 6. 添加登录过滤器
        // 替换默认的 UsernamePasswordAuthenticationFilter，处理登录请求并生成 JWT
        JwtLoginFilter loginFilter = new JwtLoginFilter(authenticationManager, redisTemplate);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        // 7. 添加 JWT 认证过滤器
        // 在每次请求前验证 JWT，确保用户已认证
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(redisTemplate);
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        // 8. 确保 ExceptionTranslationFilter 在过滤器链中
        // ExceptionTranslationFilter 负责将异常转换为 AuthenticationEntryPoint 或 AccessDeniedHandler 的响应
        // 放置在 JwtAuthenticationFilter 之后，确保认证完成后处理异常
        http.addFilterAfter(new ExceptionTranslationFilter(authenticationEntryPointImpl), JwtAuthenticationFilter.class);

        // 返回构建好的安全过滤器链
        return http.build();
    }
    /**
     * 自定义注销成功处理器，返回 JSON 响应，code 为数字类型
     * @return LogoutSuccessHandler 注销成功处理器
     */
    @Bean
    public LogoutSuccessHandler jsonLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");

            // 使用 ObjectMapper 序列化 JSON
            ObjectMapper mapper = new ObjectMapper();
            Result<String> result = Result.ok("Logout successful");
            response.getWriter().write(mapper.writeValueAsString(result));
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}