package com.stonebridge.tradeflow.security.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component //只有在在容器中的组件，才会拥有Spring Boot提供的属性绑定功能
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private long expirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "JwtProperties{" +
                "secretKey='" + secretKey + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
