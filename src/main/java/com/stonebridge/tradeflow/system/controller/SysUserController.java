package com.stonebridge.tradeflow.system.controller;

import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysUserService;

import java.util.List;

@Tag(name = "System库sys_user表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/sys_user")
public class SysUserController {

    private SysUserService sysUserService;

    private JdbcTemplate systemJdbcTemplate;

    @Autowired
    public SysUserController(SysUserService sysUserService, JdbcTemplate systemJdbcTemplate) {
        this.sysUserService = sysUserService;
        this.systemJdbcTemplate = systemJdbcTemplate;
    }


    @GetMapping("/{id}")
    @Operation(summary = "根据获取用户信息", description = "根据用户 ID 获取详细信息")
    public Result<SysUser> getSysUserById(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Integer id) {
        String sql = "SELECT * FROM sys_user WHERE id = ?";
        SysUser user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SysUser.class), id);
        if (user != null) {
            return Result.ok(user);
        } else {
            return Result.ok();
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户信息", description = "根据用户 ID 获取详细信息")
    public Result<List<SysUser>> getSysUserList() {
        List<SysUser> list = sysUserService.list();
        return Result.ok(list);
    }

}
