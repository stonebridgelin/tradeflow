package com.stonebridge.tradeflow.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.stonebridge.tradeflow.security.entity.SecurityUser;
import com.stonebridge.tradeflow.security.entity.User;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.mapper.SysUserMapper;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import com.stonebridge.tradeflow.system.service.SysRoleService;
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
     * @param sysUserMapper     用户数据访问接口
     * @param sysMenuService 权限服务
     * @param sysRoleService       角色服务
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
        log.debug("loadUserByUsername called with username: {}", username);

        // 验证用户名是否为空
        if (StringUtils.isNullOrEmpty(username)) {
            log.warn("Username is empty or null");
            throw new UsernameNotFoundException("用户名不能为空");
        }

        // 使用 MyBatis Plus 的 QueryWrapper 构造查询条件，根据用户名查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("username", username);
        List<SysUser> users = sysUserMapper.selectList(queryWrapper);
        // 记录查询结果的日志，显示找到的用户数量
        log.debug("Queried users for username {}: {} records found", username, users.size());

        // 处理查询结果
        if (users.isEmpty()) {
            // 用户不存在，记录警告日志并抛出异常
            log.warn("No user found for username: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        } else if (users.size() > 1) {
            // 发现重复用户名，记录错误日志并抛出异常
            log.error("Multiple users found for username: {}. Found {} records: {}", username, users.size(), users);
            throw new UsernameNotFoundException("发现多个用户名为: " + username);
        }

        // 获取唯一的用户信息
        SysUser sysUser = users.get(0);
        log.debug("Selected user: {}", sysUser);

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
}