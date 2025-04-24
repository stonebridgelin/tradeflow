package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.vo.SysUserQueryVo;
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
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService sysUserService;

    private final JdbcTemplate systemJdbcTemplate;

    @Autowired
    public SysUserController(SysUserService sysUserService, JdbcTemplate systemJdbcTemplate) {
        this.sysUserService = sysUserService;
        this.systemJdbcTemplate = systemJdbcTemplate;
    }

    @GetMapping(value = "/findByPage/{pageNum}/{pageSize}")
    public Result<Object> findByPage(SysUserQueryVo sysUserDto, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
        // 参数校验
        if (pageNum <= 0 || pageSize <= 0) {
            return Result.fail("分页参数错误");
        }
        // 创建分页对象
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        JSONObject resultObjct = sysUserService.findByPage(page, sysUserDto);
        return Result.ok(resultObjct);
    }


    @GetMapping("/{id}")
    @Operation(summary = "根据获取用户信息", description = "根据用户 ID 获取详细信息")
    public Result<SysUser> getSysUserById(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Integer id) {
        log.info("根据用户ID开始获取用户信息，用户的ID是：{}", id);
        String sql = "SELECT * FROM sys_user WHERE id = ?";
        SysUser user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SysUser.class), id);
        if (user != null) {
            log.info("获取用户信息成功，用户的信息是：{}", user);
            return Result.ok(user);
        } else {
            log.warn("获取用户信息失败，，用户的ID是：{}", id);
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
