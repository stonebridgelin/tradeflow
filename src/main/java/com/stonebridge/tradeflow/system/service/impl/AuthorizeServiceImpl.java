package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.utils.JwtUtil;
import com.stonebridge.tradeflow.common.utils.PasswordUtils;
import com.stonebridge.tradeflow.system.entity.User;
import com.stonebridge.tradeflow.system.entity.dto.LoginDto;
import com.stonebridge.tradeflow.system.entity.vo.RouterVo;
import com.stonebridge.tradeflow.system.service.AuthorizeService;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stonebridge.tradeflow.common.result.ResultCodeEnum.*;

@Slf4j
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    private final SysMenuService sysMenuService;
    private JdbcTemplate systemJdbcTemplate;

    @Autowired
    public AuthorizeServiceImpl(@Qualifier("systemJdbcTemplate") JdbcTemplate systemJdbcTemplate, SysMenuService sysMenuService) {
        this.systemJdbcTemplate = systemJdbcTemplate;
        this.sysMenuService = sysMenuService;
    }


    /**
     * 用户登录检查
     *
     * @param loginDto 登录信息
     * @return token
     */
    @Override
    public String loginCheck(LoginDto loginDto) {
        String sql = "select * from user where username = ?";
        log.info("用户登录：{}", loginDto.getUsername());
        User user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), loginDto.getUsername().trim());
        if (user == null) {
            log.info("用户不存在：{}", loginDto.getUsername());
            //用户不存在
            throw new CustomizeException(LOGIN_MOBLE_ERROR.getCode(), LOGIN_MOBLE_ERROR.getMessage());
        } else if (PasswordUtils.matches(loginDto.getPassword(), user.getPassword())) {
            if (user.getStatus().equals("1")) {
                log.info("用户已被禁用：{}", loginDto.getUsername());
                throw new CustomizeException(ACCOUNT_STOP.getCode(), ACCOUNT_STOP.getMessage());
            } else if (String.valueOf(user.getIsDeleted()).equals("1")) {
                log.info("用户已被删除：{}", loginDto.getUsername());
                throw new CustomizeException(ACCOUNT_DELETE.getCode(), ACCOUNT_DELETE.getMessage());
            } else {
                log.info("用户登录成功：{}", loginDto.getUsername());
                //密码正确，生成token
                return JwtUtil.generateToken(user.getUsername(), String.valueOf(user.getId()));
            }

        } else {
            log.info("用户密码错误：{}", loginDto.getUsername());
            //密码错误
            throw new CustomizeException(PASSWORD_ERROR.getCode(), PASSWORD_ERROR.getMessage());
        }
    }

    /**
     * 根据用户id获取用户信息（基本信息 菜单权限 按钮权限信息）
     *
     * @param userId : 用户id
     * @return : 用户信息（基本信息 菜单权限 按钮权限信息）
     */
    @Override
    public JSONObject getUserInfo(String userId) {
        log.info("用户信息：{}", userId);
        String sql = "select * from user where id = ?";
        User user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userId);
        if (user != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("id", user.getId());
            jsonObject.set("avatar", user.getAvatar());
            jsonObject.set("username", user.getUsername());
//            jsonObject.set("firstName", user.getFirstName());
//            jsonObject.set("lastName", user.getLastName());
//            jsonObject.set("email", user.getEmail());
//            jsonObject.set("phone", user.getPhone());
            //菜单权限数据
            //根据userId查询菜单权限值,菜单的权限是通过sys_menu.path和src/router/config.js里的path进行匹配的
            List<String> routerPaths = sysMenuService.getUserMenuListByUserId(userId);
            jsonObject.set("rights", routerPaths);
            //按钮权限数据
            //根据userId查询按钮权限值,按钮权限
            List<String> permsList = sysMenuService.getUserPermsListByUserId(userId);
            jsonObject.set("buttons", permsList);
            return jsonObject;
        }
        return null;
    }
}
