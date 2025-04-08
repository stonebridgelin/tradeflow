package com.stonebridge.tradeflow.system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Controller的测试类") // 定义 API 组名称
@RequestMapping("test")
@RestController
public class TestController {

    private final JdbcTemplate systemJdbcTemplate;

    @Autowired
    public TestController(@Qualifier("systemJdbcTemplate") JdbcTemplate systemJdbcTemplate) {
        this.systemJdbcTemplate = systemJdbcTemplate;
    }

}
