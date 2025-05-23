package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.entity.dto.AssginRoleDto;
import com.stonebridge.tradeflow.system.service.UserService;
import com.stonebridge.tradeflow.system.entity.vo.UserQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Tag(name = "用户管理的的接口类", description = "完成用户的增删改查操作，以及为用户授权角色信息，对应Sys_User表")
// 定义 API 组名称
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

    @Operation(summary = "分页查询用户的信息，列表形式返回")
    @GetMapping(value = "/findByPage/{pageNum}/{pageSize}")
    public Result<Object> findByPage(UserQueryVo userQueryVo, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
        // 参数校验
        if (pageNum <= 0 || pageSize <= 0) {
            return Result.fail("分页参数错误");
        }
        // 创建分页对象
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        JSONObject resultObjct = userService.findByPage(page, userQueryVo);
        return Result.ok(resultObjct);
    }


    @Operation(summary = "根据ID根据获取用户详细信息")
    @GetMapping("/{id}")
    public Result<SysUser> getSysUserById(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Integer id) {
        log.info("根据用户ID开始获取用户信息，用户的ID是：{}", id);
        String sql = "SELECT * FROM user WHERE id = ?";
        SysUser sysUser = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SysUser.class), id);
        if (sysUser != null) {
            log.info("获取用户信息成功，用户的信息是：{}", sysUser);
            return Result.ok(sysUser);
        } else {
            log.warn("获取用户信息失败，，用户的ID是：{}", id);
            return Result.ok();
        }
    }

    @Operation(summary = "获取所有用户信息")
    @GetMapping("/list")
    public Result<List<SysUser>> getSysUserList() {
        log.info("获取所有用户信息");
        List<SysUser> list = userService.list();
        log.info("获取所有用户信息成功，用户的个数是：{}", list.size());
        return Result.ok(list);
    }

    @Operation(summary = "为用户分配权限时，获取所有角色信息和当前用户的被赋予的所有角色")
    @GetMapping(value = "/getAllRoles/{userId}")
    public Result<JSONObject> getAllRoles(@PathVariable(value = "userId") Long userId) {
        log.info("获取所有角色信息和当前用户的被赋予的所有角色，用户的ID是：{}", userId);
        JSONObject jsonObject = userService.getAllRoles(userId);
        log.info("获取所有角色信息和当前用户的被赋予的所有角色成功,数据为：{}", jsonObject);
        return Result.ok(jsonObject);
    }

    /**
     * 为用户分配角色信息，将角色信息保存在sys_user_role表中
     *
     * @param assginRoleDto 用户角色id和被授权的角色信息
     * @return 分配结果
     */
    @Operation(summary = "为用户分配角色信息，将角色信息保存在sys_user_role表中")
    @PostMapping("/doAssign")
    public Result<JSONObject> doAssign(@RequestBody AssginRoleDto assginRoleDto) {
        log.info("分配角色，数据为：{}", assginRoleDto);
        userService.doAssign(assginRoleDto);
        return Result.ok();
    }

    /**
     * 根据用户的id删除用户信息
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @Operation(summary = "根据用户的id删除用户信息")
    @DeleteMapping("delete/{id}")
    public Result deleteUser(@PathVariable(value = "id") Long id) {
        log.info("删除用户信息，用户的ID是：{}", id);
        userService.removeById(id);
        log.info("删除用户信息成功，用户的ID是：{}", id);
        return Result.ok();
    }

    /**
     * 根据ID查询用户信息
     *
     * @param userId 用户ID
     * @return JSON格式的用户数据
     */
    @Operation(summary = "根据ID查询用户详细信息，并以Map形式返回")
    @GetMapping("getUserById/{id}")
    public Result<Map<String, Object>> getUserById(@PathVariable(value = "id") Long userId) {
        log.info("根据ID查询用户信息，用户的ID是：{}", userId);
        Map<String, Object> userMap = userService.getUserById(userId);
        log.info("查询用户信息成功，用户的信息是：{}", userMap.toString());
        return Result.ok(userMap);
    }

    /**
     * 更新用户信息
     *
     * @param sysUser 用户信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("update")
    public Result updateUser(@RequestBody SysUser sysUser) {
        log.info("更新用户信息，用户的信息是：{}", sysUser.toString());
        sysUser.setUpdateTime(new Date());
        userService.updateById(sysUser);
        log.info("更新用户信息成功，用户的信息是：{}", sysUser.toString());
        return Result.ok();
    }
}
