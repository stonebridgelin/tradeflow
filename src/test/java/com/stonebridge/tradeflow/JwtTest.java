package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.common.utils.JwtUtil;
import org.junit.jupiter.api.Test;

public class JwtTest {

    @Test
    public void testJwt() {
        String token = JwtUtil.generateToken("stonebridge", 7L);
        System.out.println(token);
        String username = JwtUtil.getUsername(token);
        System.out.println(username);
        Long userId = JwtUtil.getUserId(token);
        System.out.println(userId);
        boolean expired = JwtUtil.isTokenExpired(token);
        System.out.println(expired);
    }
}
