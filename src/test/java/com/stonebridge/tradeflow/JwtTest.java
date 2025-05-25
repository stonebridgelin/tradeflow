package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.security.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTest {

    @Autowired
    PasswordUtils passwordUtils;
    @Test
    public void testJwt() {
        String token = passwordUtils.encodePassword("123456");
        System.out.println(token);

    }
}
