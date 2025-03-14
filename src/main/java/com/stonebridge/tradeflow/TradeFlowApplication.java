package com.stonebridge.tradeflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.stonebridge.tradeflow.system.mapper", sqlSessionFactoryRef = "systemSqlSessionFactory")
@MapperScan(basePackages = "com.stonebridge.tradeflow.business.mapper", sqlSessionFactoryRef = "businessSqlSessionFactory")
public class TradeFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeFlowApplication.class, args);
    }
}
