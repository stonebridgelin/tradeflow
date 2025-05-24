package com.stonebridge.tradeflow.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.security.entity.SecurityUser;
import com.stonebridge.tradeflow.security.entity.User;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.mapper.SysUserMapper;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import com.stonebridge.tradeflow.system.service.SysRoleService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务类，实现 Spring Security 的 UserDetailsService 接口
 * 用于从数据库加载用户信息（包括用户名、密码、角色和权限），供 Spring Security 进行认证和授权
 * 该类通过 MyBatis Plus 查询用户数据，并整合角色和权限信息，返回 SecurityUser 对象
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 用户账号状态 ：0正常
     */
//    public static final String USER_STATUS_NORMAL = "0";
    /**
     * 用户账号状态：1停用
     */
    public static final String USER_STATUS_STOP = "1";
    /**
     * 用户账号状态： 2离职
     */
    public static final String USER_STATUS_RESIGN = "2";

    /**
     * 用户账号已经逻辑删除
     */
    public static final String USER_DELETED = "1";

    /**
     * 用户数据访问接口，用于查询数据库中的用户信息
     */
    private SysUserMapper sysUserMapper;

    /**
     * 权限服务，用于获取用户的权限信息
     */
    private SysMenuService sysMenuService;

    /**
     * 角色服务，用于获取用户的角色信息
     */
    private SysRoleService sysRoleService;

    /**
     * 构造函数，通过依赖注入初始化所需的 Mapper 和服务
     *
     * @param sysUserMapper  用户数据访问接口
     * @param sysMenuService 权限服务
     * @param sysRoleService 角色服务
     */
    @Autowired
    public UserDetailsServiceImpl(SysUserMapper sysUserMapper, SysMenuService sysMenuService, SysRoleService sysRoleService) {
        this.sysUserMapper = sysUserMapper;
        this.sysMenuService = sysMenuService;
        this.sysRoleService = sysRoleService;
    }

    /**
     * 根据用户名加载用户详情，返回 UserDetails 对象
     * 该方法由 Spring Security 调用，用于认证过程中获取用户信息（用户名、密码、权限等）
     *
     * @param username 用户输入的用户名，用于查询数据库
     * @return UserDetails 用户详情对象，包含用户名、密码、角色和权限
     * @throws UsernameNotFoundException 如果用户名为空或用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 记录调试日志，显示正在加载的用户名
        log.debug("加载用户详情: {}", username);

        // 使用 MyBatis Plus 的 QueryWrapper 构造查询条件，根据用户名查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("username", username);
        List<SysUser> users = sysUserMapper.selectList(queryWrapper);
        // 记录查询结果的日志，显示找到的用户数量
        log.debug("查询用户 {}: 找到 {} 条记录", username, users.size());
        // 处理查询结果
        if (users.isEmpty()) {
            // 用户不存在，记录警告日志并抛出异常
            log.warn("用户不存在: {}", username);
            throw new AuthenticationFailureException(ResultCodeEnum.ACCOUNT_ERROR.getCode(), ResultCodeEnum.ACCOUNT_ERROR.getMessage());
        } else if (users.size() > 1) {
            // 发现重复用户名，记录错误日志并抛出异常
            log.error("发现多个用户名为 {}: {} 条记录", username, users.size());
            throw new AuthenticationFailureException(ResultCodeEnum.SERVICE_ERROR.getCode(), "发现多个用户名");
        }

        // 获取唯一的用户信息
        SysUser sysUser = users.get(0);
        log.debug("选择用户: {}", sysUser);

        // 检查账号状态
        String status = sysUser.getStatus();
        if (USER_STATUS_STOP.equals(status)) {
            log.warn("用户账号已停用: {}", username);
            throw new AuthenticationFailureException(ResultCodeEnum.ACCOUNT_STOP.getCode(), ResultCodeEnum.ACCOUNT_STOP.getMessage());
        }
        if (USER_STATUS_RESIGN.equals(status)) {
            log.warn("用户离职: {}", username);
            throw new AuthenticationFailureException(ResultCodeEnum.ACCOUNT_EXPIRED.getCode(), ResultCodeEnum.ACCOUNT_EXPIRED.getMessage());
        }
        if (USER_DELETED.equals(status)) {
            log.warn("用户离职: {}", username);
            throw new AuthenticationFailureException(ResultCodeEnum.ACCOUNT_DELETE.getCode(), ResultCodeEnum.ACCOUNT_DELETE.getMessage());
        }

        // 查询用户的角色列表（角色编码）
        List<String> roleCodes = sysRoleService.getRoleCodesByUserId(Long.valueOf(sysUser.getId()));
        log.debug("Role codes for user {}: {}", username, roleCodes);

        // 查询用户的权限列表
        List<String> permissions = sysMenuService.getPermissionsByUserId(Long.valueOf(sysUser.getId()));
        log.debug("Permissions for user {}: {}", username, permissions);

        // 合并角色和权限，构成完整的权限集合
        List<String> authorities = new ArrayList<>();
        authorities.addAll(roleCodes);
        authorities.addAll(permissions);

        // 创建 User 对象，复制 SysUser 的属性
        User user = new User();
        BeanUtils.copyProperties(sysUser, user);

        // 创建 SecurityUser 对象，设置用户信息和权限列表
        SecurityUser securityUser = new SecurityUser();
        securityUser.setCurrentUserInfo(user);
        securityUser.setPermissionValueList(authorities);

        // 返回 SecurityUser 作为 UserDetails，供 Spring Security 使用
        return securityUser;
    }

    /**
     * 自定义认证失败异常，携带错误码
     */
    @Getter
    public static class AuthenticationFailureException extends UsernameNotFoundException {
        private final Integer code;

        public AuthenticationFailureException(Integer code, String msg) {
            super(msg);
            this.code = code;
        }

        public AuthenticationFailureException(Integer code, String msg, Throwable cause) {
            super(msg, cause);
            this.code = code;
        }
    }
}