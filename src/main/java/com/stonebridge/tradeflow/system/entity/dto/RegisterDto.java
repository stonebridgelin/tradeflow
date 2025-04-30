package com.stonebridge.tradeflow.system.entity.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phone;
    private String avatarUrl;
}
