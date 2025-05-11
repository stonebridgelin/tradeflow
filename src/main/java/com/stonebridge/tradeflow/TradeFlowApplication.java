package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.common.utils.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class TradeFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeFlowApplication.class, args);
    }
}
