package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.User;
import com.stonebridge.tradeflow.system.service.UserService;
import com.stonebridge.tradeflow.system.vo.UserQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Tag(name = "System库user表的Controller") // 定义 API 组名称
@RestController
@Slf4j
@RequestMapping("/system/user")
public class UserController {

    private final UserService userService;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserController(UserService userService, JdbcTemplate jdbcTemplate) {
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = "/findByPage/{pageNum}/{pageSize}")
    public Result<Object> findByPage(UserQueryVo userQueryVo, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
        // 参数校验
        if (pageNum <= 0 || pageSize <= 0) {
            return Result.fail("分页参数错误");
        }
        // 创建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        JSONObject resultObjct = userService.findByPage(page, userQueryVo);
        return Result.ok(resultObjct);
    }


    @GetMapping("/{id}")
    @Operation(summary = "根据获取用户信息", description = "根据用户 ID 获取详细信息")
    public Result<User> getSysUserById(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Integer id) {
        log.info("根据用户ID开始获取用户信息，用户的ID是：{}", id);
        String sql = "SELECT * FROM user WHERE id = ?";
        User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
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
    public Result<List<User>> getSysUserList() {
        log.info("获取所有用户信息");
        List<User> list = userService.list();
        log.info("获取所有用户信息成功，用户的个数是：{}", list.size());
        return Result.ok(list);
    }

}
