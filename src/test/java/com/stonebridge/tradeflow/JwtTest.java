package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.security.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTest {

    @Test
    public void testJwt() {
        String token = JwtUtil.generateToken("lin");
        System.out.println(token);
//        token = "eyJ6aXAiOiJHWklQIiwiYWxnIjoiSFMyNTYifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcxNzA2NTMz0VEqLU4t8kwBqjJUgnDyEnNTgdyczDylWgBlCDnEQgAAAA.pUO5ECVeKbdRqpMm6upXb_OVxJbJmBKWN7GPsXBGX40";
        String username = JwtUtil.getUsername(token);
        System.out.println(username);
        boolean expired = JwtUtil.isTokenExpired(token);
        System.out.println(expired);
    }
}
