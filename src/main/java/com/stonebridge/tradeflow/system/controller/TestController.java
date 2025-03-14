package com.stonebridge.tradeflow.system.controller;

import com.stonebridge.tradeflow.common.vo.Result;
import com.stonebridge.tradeflow.system.entity.SystemUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Controller的测试类") // 定义 API 组名称
@RequestMapping("test")
@Controller
public class TestController {

    private final JdbcTemplate systemJdbcTemplate;

    public TestController(@Qualifier("systemJdbcTemplate") JdbcTemplate systemJdbcTemplate) {
        this.systemJdbcTemplate = systemJdbcTemplate;
    }


    @GetMapping("/{id}")
    @ResponseBody
    @Operation(summary = "获取用户信息", description = "根据用户 ID 获取详细信息")
    public Result<SystemUser> getUserInfoById(@Parameter(description = "用户名", required = true, example = "1") @PathVariable Integer id) {
        String sql = "SELECT * FROM sys_user WHERE id = ?";
        SystemUser user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SystemUser.class), id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("User not found");
        }
    }
}
