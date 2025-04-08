// com.stonebridge.tradeflow.system.entity.dto.LoginResponse.java
package com.stonebridge.tradeflow.system.entity.dto;

public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}