package com.stonebridge.tradeflow.system.controller;

import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysUserService;

import java.util.List;

@Tag(name = "System库sys_user表的Controller") // 定义 API 组名称
@RestController
@Slf4j
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
        log.info("根据用户ID开始获取用户信息，用户的ID是：{}",id);
        String sql = "SELECT * FROM sys_user WHERE id = ?";
        SysUser user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SysUser.class), id);
        if (user != null) {
            log.info("获取用户信息成功，用户的信息是：{}", user);
            return Result.ok(user);
        } else {
            log.warn("获取用户信息失败，，用户的ID是：{}",id);
            return Result.ok();
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有用户信息", description = "根据用户 ID 获取详细信息")
    public Result<List<SysUser>> getSysUserList() {
        log.info("获取所有用户信息");
        List<SysUser> list = sysUserService.list();
        log.info("获取所有用户信息成功，用户的个数是：{}", list.size());
        return Result.ok(list);
    }

}
