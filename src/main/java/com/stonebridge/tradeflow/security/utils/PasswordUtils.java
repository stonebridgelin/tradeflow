package com.stonebridge.tradeflow.security.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类，使用 BCrypt 进行密码加密和验证
 * 适用于 Spring Boot 3.0.2 和 Spring Security
 */
@Component
public class PasswordUtils {

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 构造函数，初始化 BCryptPasswordEncoder
     * 默认强度为 10，可根据需要调整（4-31，越大越安全但性能开销更高）
     */
    public PasswordUtils() {
        this.passwordEncoder = new BCryptPasswordEncoder(10);
    }

    /**
     * 加密密码
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码
     * @throws IllegalArgumentException 如果密码为空或无效
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        System.out.println("加密:" + rawPassword);
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     * @throws IllegalArgumentException 如果密码或加密密码为空
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码或加密密码不能为空");
        }
        System.out.println("明文密码:" + rawPassword + ";加密后的密码" + encodedPassword);
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Add getter for BCryptPasswordEncoder
    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
