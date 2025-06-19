package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.security.utils.PasswordUtils;
import com.stonebridge.tradeflow.security.utils.SecurityContextHolderUtil;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.entity.dto.AssginRoleDto;
import com.stonebridge.tradeflow.system.entity.dto.RegisterDto;
import com.stonebridge.tradeflow.system.service.SysUserService;
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

import static com.stonebridge.tradeflow.common.result.ResultCodeEnum.ACCOUNT_ERROR;
import static com.stonebridge.tradeflow.common.result.ResultCodeEnum.LOGIN_AUTH;

@Tag(name = "用户管理的的接口类", description = "完成用户的增删改查操作，以及为用户授权角色信息，对应Sys_User表")
// 定义 API 组名称
@RestController
@Slf4j
@RequestMapping("/system/user")
public class SysUserController {
    private static final String AUTH_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private final SysUserService sysUserService;

    private final JdbcTemplate jdbcTemplate;

    private final PasswordUtils passwordUtils;

    @Autowired
    public SysUserController(SysUserService sysUserService, JdbcTemplate jdbcTemplate, PasswordUtils passwordUtils) {
        this.sysUserService = sysUserService;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordUtils = passwordUtils;
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
        JSONObject resultObjct = sysUserService.findByPage(page, userQueryVo);
        return Result.ok(resultObjct);
    }


    @Operation(summary = "根据ID根据获取用户详细信息")
    @GetMapping("/{id}")
    public Result<SysUser> getSysUserById(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Integer id) {
        log.info("根据用户ID开始获取用户信息，用户的ID是：{}", id);
        String sql = "SELECT * FROM sys_user WHERE id = ?";
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
        List<SysUser> list = sysUserService.list();
        log.info("获取所有用户信息成功，用户的个数是：{}", list.size());
        return Result.ok(list);
    }

    @Operation(summary = "为用户分配权限时，获取所有角色信息和当前用户的被赋予的所有角色")
    @GetMapping(value = "/getAllRoles/{userId}")
    public Result<JSONObject> getAllRoles(@PathVariable(value = "userId") Long userId) {
        log.info("获取所有角色信息和当前用户的被赋予的所有角色，用户的ID是：{}", userId);
        JSONObject jsonObject = sysUserService.getAllRoles(userId);
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
        sysUserService.doAssign(assginRoleDto);
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
        sysUserService.removeById(id);
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
        Map<String, Object> userMap = sysUserService.getUserById(userId);
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
        sysUserService.updateById(sysUser);
        log.info("更新用户信息成功，用户的信息是：{}", sysUser.toString());
        return Result.ok();
    }

    /**
     * 获取用户信息接口,从spring security作用域获取用户id，然后查询用户信息并返回。
     * @return Result 封装的用户信息（UserInfoVO）
     */
    @Operation(summary = "用户信息", description = "获取当前用户信息")
    @GetMapping("info")
    public Result<ObjectNode> info() {
        // 1. 从spring security中获取用户id
        String userId = SecurityContextHolderUtil.getUserId();
        // 2. 查询用户信息
        //根据用户id获取用户信息（基本信息 菜单权限 按钮权限信息）
        ObjectNode userInfo = sysUserService.getUserInfo(userId);
        if (userInfo == null) {
            log.warn("User info not found for userId: {}", userId);
            throw new CustomizeException(ACCOUNT_ERROR.getCode(), ACCOUNT_ERROR.getMessage());
        }
        // 4. 返回用户信息
        return Result.ok(userInfo);
    }

    @Operation(summary = "用户注册", description = "处理用户注册请求")
    @PostMapping("register")
    public Result register(@RequestBody RegisterDto registerDto) {
        log.info("开始处理用户注册请求: username={}", registerDto.getUsername());
        try {
            SysUser newSysUser = new SysUser();
            newSysUser.setUsername(registerDto.getUsername().trim());
            newSysUser.setPassword(passwordUtils.encodePassword(registerDto.getPassword().trim()));
            newSysUser.setFirstName(registerDto.getFirstName().trim());
            newSysUser.setLastName(registerDto.getLastName().trim());
            newSysUser.setEmail(registerDto.getEmail().trim());
            newSysUser.setPhone(registerDto.getPhone().trim());
            newSysUser.setAvatar(registerDto.getAvatarUrl().trim());
            newSysUser.setCreateTime(new Date());
            newSysUser.setUpdateTime(new Date());
            newSysUser.setStatus("0");
            sysUserService.save(newSysUser);
            log.info("用户 {} 注册成功", registerDto.getUsername());
            return Result.ok();
        } catch (Exception exception) {
            log.error("用户 {}", registerDto.getUsername(), exception);
            throw exception;
        }
    }
}
