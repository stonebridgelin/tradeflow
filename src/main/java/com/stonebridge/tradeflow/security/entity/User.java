package com.stonebridge.tradeflow.security.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Thor
 * @公众号 Java架构栈
 */
@Data
public class User implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private String token;

}
